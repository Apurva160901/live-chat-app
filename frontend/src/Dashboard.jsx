import { useEffect, useRef, useState } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { apiGet, apiUpload } from './api';
import ThemePicker from './ThemePicker';
import Avatar from './Avatar';

function desktopNotify(msg) {
  if (typeof Notification === 'undefined' || Notification.permission !== 'granted') return;
  const body = msg.content || (msg.attachmentType === 'IMAGE' ? '📷 Photo' : '📎 File');
  new Notification(msg.sender, { body, tag: 'live-chat' });
}

/**
 * Main screen after login: sidebar of people + a 1:1 chat pane.
 * Supports avatars, unread badges, in-app toasts, and image/file attachments.
 */
export default function Dashboard({ auth, onLogout, onAuthUpdate }) {
  const [people, setPeople] = useState([]);
  const [selected, setSelected] = useState(null);
  const [convos, setConvos] = useState({});
  const [unread, setUnread] = useState({});
  const [toast, setToast] = useState(null);
  const [input, setInput] = useState('');
  const [connected, setConnected] = useState(false);

  const clientRef = useRef(null);
  const bottomRef = useRef(null);
  const selectedRef = useRef(null);
  const loadedRef = useRef(new Set());
  const toastTimer = useRef(null);
  const showToastRef = useRef(() => {});
  selectedRef.current = selected;

  const personOf = (username) => people.find((p) => p.username === username);
  const displayNameOf = (username) => personOf(username)?.displayName || username;

  showToastRef.current = (msg) => {
    setToast({ sender: msg.sender, content: msg.content || (msg.attachmentType === 'IMAGE' ? '📷 Photo' : '📎 File') });
    clearTimeout(toastTimer.current);
    toastTimer.current = setTimeout(() => setToast(null), 4000);
  };

  useEffect(() => {
    if (typeof Notification !== 'undefined' && Notification.permission === 'default') {
      Notification.requestPermission();
    }
  }, []);

  useEffect(() => {
    apiGet('/api/users', auth.token).then(setPeople).catch(console.error);
  }, [auth.token]);

  useEffect(() => {
    const client = new Client({
      webSocketFactory: () => new SockJS(window.location.origin + '/ws'),
      connectHeaders: { Authorization: `Bearer ${auth.token}` },
      reconnectDelay: 3000,
      onConnect: () => {
        setConnected(true);
        client.subscribe('/user/queue/messages', (frame) => {
          const msg = JSON.parse(frame.body);
          const other = msg.sender === auth.username ? msg.recipient : msg.sender;
          setConvos((prev) => ({ ...prev, [other]: [...(prev[other] || []), msg] }));

          if (msg.sender !== auth.username) {
            const viewingThisChat = selectedRef.current === msg.sender;
            const tabVisible = document.visibilityState === 'visible';
            if (!(viewingThisChat && tabVisible)) {
              setUnread((prev) => ({ ...prev, [msg.sender]: (prev[msg.sender] || 0) + 1 }));
              if (!tabVisible) desktopNotify(msg);
              else showToastRef.current(msg);
            }
          }
        });
      },
      onDisconnect: () => setConnected(false),
      onStompError: (f) => console.error('STOMP error:', f.headers['message']),
    });
    client.activate();
    clientRef.current = client;
    return () => client.deactivate();
  }, [auth.token, auth.username]);

  useEffect(() => {
    if (!selected || loadedRef.current.has(selected)) return;
    loadedRef.current.add(selected);
    apiGet(`/api/dm/${selected}`, auth.token)
      .then((history) => setConvos((prev) => ({ ...prev, [selected]: history })))
      .catch(console.error);
  }, [selected, auth.token]);

  useEffect(() => {
    if (selected) setUnread((prev) => (prev[selected] ? { ...prev, [selected]: 0 } : prev));
  }, [selected]);

  const totalUnread = Object.values(unread).reduce((a, b) => a + b, 0);
  useEffect(() => {
    document.title = totalUnread > 0 ? `(${totalUnread}) Live Chat` : 'Live Chat';
  }, [totalUnread]);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [convos, selected]);

  function publishDm(fields) {
    clientRef.current?.publish({
      destination: '/app/dm.send',
      body: JSON.stringify({ recipient: selected, content: '', ...fields }),
    });
  }

  function sendText(e) {
    e.preventDefault();
    const content = input.trim();
    if (!content || !selected || !clientRef.current?.connected) return;
    publishDm({ content });
    setInput('');
  }

  async function handleAttach(e) {
    const file = e.target.files?.[0];
    e.target.value = '';
    if (!file || !selected) return;
    try {
      const up = await apiUpload('/api/uploads', file, auth.token);
      publishDm({ attachmentUrl: up.url, attachmentType: up.type, attachmentName: up.name });
    } catch (err) {
      console.error('Attachment upload failed:', err);
    }
  }

  async function handleAvatar(e) {
    const file = e.target.files?.[0];
    e.target.value = '';
    if (!file) return;
    try {
      const dto = await apiUpload('/api/users/me/avatar', file, auth.token);
      onAuthUpdate({ avatarUrl: dto.avatarUrl });
    } catch (err) {
      console.error('Avatar upload failed:', err);
    }
  }

  const messages = selected ? (convos[selected] || []) : [];

  return (
    <div className={`app ${selected ? 'chat-open' : ''}`}>
      {toast && (
        <div className="toast" onClick={() => { setSelected(toast.sender); setToast(null); }}>
          <strong>{displayNameOf(toast.sender)}</strong>
          <span>{toast.content}</span>
        </div>
      )}

      <aside className="sidebar">
        <div className="sidebar-header">
          <div className="me">
            <label className="me-avatar" title="Change your photo">
              <Avatar name={auth.displayName} url={auth.avatarUrl} size={40} />
              <input type="file" accept="image/*" hidden onChange={handleAvatar} />
            </label>
            <div className="me-name">{auth.displayName}<br /><small>@{auth.username} · tap photo to change</small></div>
          </div>
          <div className="sidebar-actions">
            <ThemePicker />
            <button className="logout" onClick={onLogout}>Logout</button>
          </div>
        </div>

        <div className="people">
          {people.length === 0 && (
            <div className="empty">
              No other users yet.<br />Open another browser window and register someone to chat with!
            </div>
          )}
          {people.map((p) => (
            <div
              key={p.username}
              className={`person ${selected === p.username ? 'active' : ''}`}
              onClick={() => setSelected(p.username)}
            >
              <Avatar name={p.displayName} url={p.avatarUrl} size={40} />
              <div className="person-name">{p.displayName}<br /><small>@{p.username}</small></div>
              {unread[p.username] > 0 && <span className="badge">{unread[p.username]}</span>}
            </div>
          ))}
        </div>
      </aside>

      <section className="chat-pane">
        {!selected ? (
          <div className="no-chat">👈 Pick someone to start chatting</div>
        ) : (
          <>
            <header className="chat-header">
              <button className="back" onClick={() => setSelected(null)}>←</button>
              <Avatar name={displayNameOf(selected)} url={personOf(selected)?.avatarUrl} size={36} />
              <span className="chat-title">{displayNameOf(selected)}</span>
              <span className={`status ${connected ? 'online' : 'offline'}`}>
                {connected ? '● online' : '○ connecting…'}
              </span>
            </header>

            <main className="messages">
              {messages.map((m, i) => {
                const mine = m.sender === auth.username;
                return (
                  <div key={i} className={`bubble-row ${mine ? 'mine' : 'theirs'}`}>
                    <div className="bubble">
                      {m.attachmentUrl && m.attachmentType === 'IMAGE' && (
                        <a href={m.attachmentUrl} target="_blank" rel="noreferrer">
                          <img className="msg-image" src={m.attachmentUrl} alt={m.attachmentName} />
                        </a>
                      )}
                      {m.attachmentUrl && m.attachmentType !== 'IMAGE' && (
                        <a className="msg-file" href={m.attachmentUrl} target="_blank" rel="noreferrer" download>
                          📄 {m.attachmentName || 'Download file'}
                        </a>
                      )}
                      {m.content && <div className="content">{m.content}</div>}
                    </div>
                  </div>
                );
              })}
              <div ref={bottomRef} />
            </main>

            <form className="composer" onSubmit={sendText}>
              <label className="attach-btn" title="Attach image or file">
                📎
                <input type="file" hidden onChange={handleAttach} disabled={!connected} />
              </label>
              <input
                value={input}
                onChange={(e) => setInput(e.target.value)}
                placeholder={connected ? 'Type a message…' : 'Connecting…'}
                disabled={!connected}
              />
              <button type="submit" disabled={!connected || !input.trim()}>Send</button>
            </form>
          </>
        )}
      </section>
    </div>
  );
}
