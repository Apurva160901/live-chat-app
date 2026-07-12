# 02 · WebSockets (and STOMP)

> Format: **What → Why → How → When → Advantages → Disadvantages → Why here → Alternatives (and why not).**

---

## 🔌 WebSocket

**What it is:** A protocol that keeps a **single, permanent, two-way connection** open
between the browser and the server. Once open, either side can send data at any time,
instantly — without making a new request each time.

**Why it is used:** Normal HTTP is one-way and one-shot: the browser asks, the server
answers, the connection closes. That's bad for chat, where the *server* needs to push
new messages to you the moment they arrive. WebSocket keeps the line open both ways.

**How it is used:** The browser opens a connection (a "handshake" that upgrades HTTP to
WebSocket) at a URL like `/ws`. After that, messages flow freely in both directions.
We put **STOMP** on top (see below) to add addressing + pub/sub.

**When it is used:** Chat, live notifications, multiplayer games, live dashboards,
collaborative editing, stock tickers — anything real-time and two-way.

**Advantages:** True real-time; low overhead after connect (no repeated headers);
server can push; two-way.

**Disadvantages:** More complex than HTTP; long-lived connections are harder to scale
and load-balance; can drop on flaky networks (needs reconnect logic); some proxies block it.

**Why here:** Chat is the textbook use case — the server must instantly push each new
message to every connected user. Polling would be slow and wasteful.

**Alternatives & why not:**
- **HTTP short polling** (ask "any new messages?" every 2s) — simple, but laggy and
  wasteful; hammers the server. ❌ Too slow for real chat.
- **Long polling** (hold the request open until data arrives) — better, but clunky and
  resource-heavy. ❌ WebSocket does this natively and cleaner.
- **SSE (Server-Sent Events)** — server→client push over HTTP, great for *one-way* feeds
  (notifications). ❌ It's one-way only; chat needs the client to push too.

**📞 Real-life analogy:** HTTP is sending **letters** — one question, one reply, envelope
closed. WebSocket is a **phone call** that stays connected: both people can talk the
moment they want, no redialing.

---

## ✉️ STOMP (on top of WebSocket)

**What it is:** A simple text protocol that adds **"envelopes with an address"** to raw
WebSocket, enabling **publish/subscribe** (pub/sub) messaging.

**Why it is used:** Raw WebSocket just sends bytes — it has no idea of "rooms",
"topics", or "who should get this". STOMP adds destinations so we can subscribe to a
topic and broadcast to all subscribers easily.

**How it is used (in our app):**
- Client **subscribes** to `/topic/public` → it now receives every broadcast there.
- Client **sends** a message to `/app/chat.send` → routed to our `ChatController`.
- Controller returns the message with `@SendTo("/topic/public")` → broker broadcasts it
  to everyone subscribed. That's the whole chat loop.

**When it is used:** Whenever you want pub/sub or multiple "channels" over WebSocket.

**Advantages:** Simple pub/sub; built into Spring; easy broadcasting; can later swap the
in-memory broker for a real one (RabbitMQ/ActiveMQ) with almost no code change.

**Disadvantages:** Extra layer to learn; the in-memory broker doesn't scale across
multiple servers (fine for now, fixed later with an external broker).

**Why here:** It makes "one message → everyone in the room sees it" trivial, and it's the
standard Spring way to do WebSocket chat.

**Alternatives & why not:**
- **Raw WebSocket (no STOMP)** — you'd hand-code rooms, routing, and broadcasting. ❌
  Reinventing the wheel.
- **Socket.IO** — popular in Node.js, but it's a JS-first library, not native to Spring. ❌ Off-stack.

**📮 Real-life analogy:** WebSocket is an open **phone line**; STOMP is the **switchboard**
that routes your call to the right department (`/topic/public`) and can conference-call
everyone subscribed.

---

## 🔗 How this maps to our code

| File | Role |
|------|------|
| `config/WebSocketConfig.java` | Opens the `/ws` endpoint; sets `/topic` (broadcast) + `/app` (send) prefixes |
| `chat/ChatMessage.java` | The message shape (type, sender, content, timestamp) as JSON |
| `chat/ChatController.java` | Receives `/app/chat.send`, broadcasts to `/topic/public` |

**Next:** `03` PostgreSQL + JPA/Hibernate (so messages are saved, not just broadcast).
