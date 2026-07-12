# 04 · React (frontend) + the WebSocket client

> Format: **What → Why → How → When → Advantages → Disadvantages → Why here → Alternatives (and why not).**

---

## ⚛️ React

**What it is:** A JavaScript library for building user interfaces out of reusable
**components**, where the screen automatically updates when the data ("state") changes.

**Why it is used:** Manually updating the DOM (page) as data changes is tedious and
bug-prone. React lets you describe *what* the UI should look like for a given state, and
it efficiently updates the page for you.

**How it is used:** You write components (functions returning JSX — HTML-like markup).
State is held with hooks like `useState`; side effects (like connecting a WebSocket) run
in `useEffect`. When state changes, React re-renders that component.

**When it is used:** Interactive web UIs — dashboards, chat, forms, SPAs (single-page apps).

**Advantages:** Component reuse; declarative (describe the "what", not the "how"); huge
ecosystem & jobs; fast virtual-DOM updates.

**Disadvantages:** Just the UI layer (you assemble routing, state libs, etc. yourself);
build tooling can be complex; fast-moving ecosystem.

**Why here:** It's the most popular frontend library, pairs naturally with a Spring Boot
REST/WebSocket backend, and gives us a clean, reactive chat UI. Great resume combo:
"Java + React full-stack".

**Alternatives & why not:**
- **Angular** — full framework, more opinionated & heavier; steeper curve. ❌ More than we need.
- **Vue** — lovely and simple, but fewer enterprise jobs than React. ❌ Less resume value.
- **Plain HTML + JS** — no structure; state/UI syncing becomes a mess as it grows. ❌

**🧱 Real-life analogy:** React is like **LEGO for UIs**. You build small blocks (components)
once and snap them together; change the instructions (state) and the model rebuilds itself.

---

## 🔌 The WebSocket client (STOMP.js + SockJS)

**What it is:** Browser libraries that open and manage the WebSocket connection to our
Spring backend. `SockJS` creates the connection (with fallbacks); `@stomp/stompjs` speaks
the STOMP protocol over it (subscribe / publish).

**Why it is used:** So the React app can receive broadcasts instantly and send messages,
matching the STOMP setup on the server.

**How it is used (in `App.jsx`):**
- `new SockJS('http://localhost:8080/ws')` → opens the connection.
- `client.subscribe('/topic/public', cb)` → receive every broadcast message.
- `client.publish({destination: '/app/chat.send', body})` → send a message.
- `fetch('/api/messages')` → load history via REST when the app opens.

**When it is used:** Any browser app that needs live, two-way data with a STOMP backend.

**Advantages:** Handles reconnects; clean subscribe/publish API; SockJS falls back if raw
WebSocket is blocked.

**Disadvantages:** Extra libraries; SockJS needs a `global` polyfill in Vite (we added it).

**Why here:** It's the standard client for a Spring STOMP backend, so client and server
speak the exact same language.

**Alternatives & why not:**
- **Native `WebSocket` API** — no STOMP, no auto-reconnect; you'd hand-roll everything. ❌
- **Socket.IO client** — needs a Socket.IO server (not our Spring STOMP). ❌ Mismatched.

**🎧 Real-life analogy:** SockJS is **plugging in the phone line**; STOMP.js is **knowing the
phone etiquette** (who to call = destination, who's listening = subscription).

---

## 🔗 How this maps to our code

| Piece | Role |
|-------|------|
| `App.jsx` | The whole chat UI: join screen + room, state via `useState` |
| `useState` | Holds username, messages, connection status, input text |
| `useEffect` | Auto-scroll to newest message; clean up the connection on exit |
| `SockJS` + `@stomp/stompjs` | Connect, subscribe to `/topic/public`, publish to `/app/*` |
| `fetch('/api/messages')` | Load chat history when joining |
| `vite.config.js` `define: { global }` | Fixes SockJS in the browser |

**Next:** run both servers together and watch two browser tabs chat in real time.
