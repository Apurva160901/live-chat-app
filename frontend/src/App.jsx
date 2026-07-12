import { useEffect, useRef, useState } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import './App.css';

// Where the Spring Boot backend lives.
const API_BASE = 'http://localhost:8080';

export default function App() {
  const [username, setUsername] = useState('');
  const [joined, setJoined] = useState(false);
  const [connected, setConnected] = useState(false);
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');

  const clientRef = useRef(null);   // holds the STOMP client
  const bottomRef = useRef(null);   // used to auto-scroll to newest message

  // Auto-scroll to the latest message whenever the list changes.
  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  // Clean up the WebSocket connection if the component unmounts.
  useEffect(() => () => clientRef.current?.deactivate(), []);

  async function handleJoin(e) {
    e.preventDefault();
    const name = username.trim();
    if (!name) return;

    // 1) Load past messages via the REST API so we see the conversation so far.
    try {
      const res = await fetch(`${API_BASE}/api/messages`);
      if (res.ok) setMessages(await res.json());
    } catch (err) {
      console.error('Could not load history:', err);
    }

    // 2) Open the WebSocket connection (STOMP over SockJS).
    const client = new Client({
      webSocketFactory: () => new SockJS(`${API_BASE}/ws`),
      reconnectDelay: 3000, // auto-reconnect if the connection drops
      onConnect: () => {
        setConnected(true);
        // Subscribe to the broadcast topic: every message shows up here.
        client.subscribe('/topic/public', (frame) => {
          const msg = JSON.parse(frame.body);
          setMessages((prev) => [...prev, msg]);
        });
        // Announce that we joined.
        client.publish({
          destination: '/app/chat.join',
          body: JSON.stringify({ sender: name }),
        });
      },
      onDisconnect: () => setConnected(false),
      onStompError: (f) => console.error('STOMP error:', f.headers['message']),
    });

    client.activate();
    clientRef.current = client;
    setJoined(true);
  }

  function handleSend(e) {
    e.preventDefault();
    const content = input.trim();
    if (!content || !clientRef.current?.connected) return;
    clientRef.current.publish({
      destination: '/app/chat.send',
      body: JSON.stringify({ sender: username, content }),
    });
    setInput('');
  }

  // ---------- Username entry screen ----------
  if (!joined) {
    return (
      <div className="center-screen">
        <form className="join-card" onSubmit={handleJoin}>
          <h1>💬 Live Chat</h1>
          <p>Enter a name to join the room</p>
          <input
            autoFocus
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            placeholder="Your name"
            maxLength={20}
          />
          <button type="submit" disabled={!username.trim()}>Join chat</button>
        </form>
      </div>
    );
  }

  // ---------- Chat room screen ----------
  return (
    <div className="chat">
      <header className="chat-header">
        <span>💬 Live Chat</span>
        <span className={`status ${connected ? 'online' : 'offline'}`}>
          {connected ? '● connected' : '○ connecting…'}
        </span>
      </header>

      <main className="messages">
        {messages.map((m, i) => {
          if (m.type === 'JOIN' || m.type === 'LEAVE') {
            return <div key={i} className="system-msg">{m.content}</div>;
          }
          const mine = m.sender === username;
          return (
            <div key={i} className={`bubble-row ${mine ? 'mine' : 'theirs'}`}>
              <div className="bubble">
                {!mine && <div className="sender">{m.sender}</div>}
                <div className="content">{m.content}</div>
              </div>
            </div>
          );
        })}
        <div ref={bottomRef} />
      </main>

      <form className="composer" onSubmit={handleSend}>
        <input
          value={input}
          onChange={(e) => setInput(e.target.value)}
          placeholder={connected ? 'Type a message…' : 'Connecting…'}
          disabled={!connected}
        />
        <button type="submit" disabled={!connected || !input.trim()}>Send</button>
      </form>
    </div>
  );
}
