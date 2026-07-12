import { Fragment, useEffect, useRef, useState } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { apiGet, apiUpload } from './api';
import ThemePicker from './ThemePicker';
import Avatar from './Avatar';

const EMOJIS = ['😀', '😂', '😍', '👍', '🙏', '🎉', '🔥', '❤️', '😎', '😢', '🤔', '👏', '🙌', '✅', '🚀', '💯'];

// ---- small date/time helpers for the chat UI ----
function timeLabel(ts) {
  return new Date(ts).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
}
function sameDay(a, b) {
  return new Date(a).toDateString() === new Date(b).toDateString();
}
function dateLabel(ts) {
  const d = new Date(ts);
  const today = new Date();
  const yesterday = new Date();
  yesterday.setDate(today.getDate() - 1);
  if (d.toDateString() === today.toDateString()) return 'Today';
  if (d.toDateString() === yesterday.toDateString()) return 'Yesterday';
  return d.toLocaleDateString([], { day: 'numeric', month: 'short', year: 'numeric' });
}
function withinGroup(a, b) {
  return Math.abs(new Date(b).getTime() - new Date(a).getTime()) < 5 * 60 * 1000;
}

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
  const [showEmoji, setShowEmoji] = useState(false);
  const [online, setOnline] = useState(() => new Set());
  const [typingUser, setTypingUser] = useState(null);

  const clientRef = useRef(null);
  const bottomRef = useRef(null);
  const selectedRef = useRef(null);
  const loadedRef = useRef(new Set());
  const toastTimer = useRef(null);
  const typingTimer = useRef(null);
  const lastTypingSent = useRef(0);
  const showToastRef = useRef(() => {});
  const peopleRef = useRef([]);
  const refreshPeopleRef = useRef(() => {});
  selectedRef.current = selected;
  peopleRef.current = people;
  refreshPeopleRef.current = () =>
    apiGet('/api/users', auth.token).then(setPeople).catch(console.error);

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
    refreshPeopleRef.current();
    // Poll so users who register later show up without a manual refresh.
    const id = setInterval(() => refreshPeopleRef.current(), 8000);
    return () => clearInterval(id);
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

          // If this person isn't in our list yet (registered after we loaded it),
          // refresh so they appear immediately — no manual refresh needed.
          if (!peopleRef.current.some((p) => p.username === other)) {
            refreshPeopleRef.current();
          }

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

        // Presence: who's online (initial fetch + live updates on /topic/presence).
        apiGet('/api/presence', auth.token).then((list) => setOnline(new Set(list))).catch(() => {});
        client.subscribe('/topic/presence', (frame) => {
          try { setOnline(new Set(JSON.parse(frame.body))); } catch { /* ignore */ }
        });

        // Typing: someone is typing to me → show it for a few seconds.
        client.subscribe('/user/queue/typing', (frame) => {
          const { sender } = JSON.parse(frame.body);
          setTypingUser(sender);
          clearTimeout(typingTimer.current);
          typingTimer.current = setTimeout(() => setTypingUser(null), 3000);
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

  // Update the text box AND (throttled) tell the other person we're typing.
  function handleTyping(e) {
    setInput(e.target.value);
    const now = Date.now();
    if (selected && clientRef.current?.connected && now - lastTypingSent.current > 1500) {
      lastTypingSent.current = now;
      clientRef.current.publish({ destination: '/app/typing', body: JSON.stringify({ recipient: selected }) });
    }
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
              <div className="person-name">
                {p.displayName}
                <br />
                <small className={online.has(p.username) ? 'online-text' : ''}>
                  {online.has(p.username) ? '● online' : `@${p.username}`}
                </small>
              </div>
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
              <span className={`status ${online.has(selected) ? 'online' : 'offline'}`}>
                {typingUser === selected ? 'typing…' : (online.has(selected) ? '● online' : 'offline')}
              </span>
            </header>

            <main className="messages">
              {messages.map((m, i) => {
                const prev = messages[i - 1];
                const showDate = !prev || !sameDay(prev.timestamp, m.timestamp);
                const grouped = prev && !showDate && prev.sender === m.sender
                  && withinGroup(prev.timestamp, m.timestamp);
                const mine = m.sender === auth.username;
                return (
                  <Fragment key={i}>
                    {showDate && (
                      <div className="date-divider"><span>{dateLabel(m.timestamp)}</span></div>
                    )}
                    <div className={`bubble-row ${mine ? 'mine' : 'theirs'} ${grouped ? 'grouped' : ''}`}>
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
                        <span className="time">{timeLabel(m.timestamp)}</span>
                      </div>
                    </div>
                  </Fragment>
                );
              })}
              <div ref={bottomRef} />
            </main>

            <form className="composer" onSubmit={sendText}>
              <button type="button" className="emoji-btn" onClick={() => setShowEmoji((v) => !v)} title="Emoji">😊</button>
              {showEmoji && (
                <div className="emoji-palette">
                  {EMOJIS.map((e) => (
                    <span key={e} onClick={() => { setInput((t) => t + e); setShowEmoji(false); }}>{e}</span>
                  ))}
                </div>
              )}
              <label className="attach-btn" title="Attach image or file">
                📎
                <input type="file" hidden onChange={handleAttach} disabled={!connected} />
              </label>
              <input
                value={input}
                onChange={handleTyping}
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
