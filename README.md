# 💬 Live Chat Application (Java + React)

A real-time chat application built with **Spring Boot** (Java) and **React**, using
**WebSockets** for instant messaging and **PostgreSQL + Hibernate/JPA** for storing
users and message history.

> This project is built in phases as a learning + portfolio project. Each concept is
> documented in the [`docs/`](docs) folder using a fixed format:
> *what it is → why → how → when → advantages → disadvantages → why here → alternatives.*

## 🏗️ Architecture (Phase 1)

```
live-chat-app/
├─ backend/     Spring Boot app (REST + WebSocket + JPA)
│  └─ src/main/java/com/apurva/chat/
├─ frontend/    React app (chat UI)   [coming next]
└─ docs/        concept explanations + interview prep
```

## 🧩 Tech stack

| Layer | Technology | Role |
|-------|-----------|------|
| Frontend | React | Chat UI in the browser |
| Real-time | WebSocket (STOMP) | Instant two-way messaging |
| Backend | Spring Boot (Java 21) | REST APIs + WebSocket server |
| Persistence | Hibernate / JPA | Map Java objects to DB tables |
| Database | PostgreSQL | Store users & message history |

## 🗺️ Roadmap

- **Phase 1** — Live 1-room chat (Spring Boot + WebSocket + React + Postgres) ← *in progress*
- Phase 2 — Login (JWT), message history, multiple rooms, presence
- Phase 3 — Kafka events + notifications (event-driven architecture)
- Phase 4 — Docker Compose
- Phase 5 — Camunda moderation workflow
- Phase 6 — Split into microservices
- Phase 7 — Interview-prep PDF

## 🚀 Running (once Phase 1 is complete)

Instructions will be added here as we build. For now, see [`docs/`](docs).

---

Built by Apurva Gaikwad · a Java-developer portfolio project
