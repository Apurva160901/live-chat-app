# 💬 Live Chat Application (Java + React)

A full-stack, **real-time chat application** — WhatsApp-style 1:1 messaging built with
**Spring Boot**, **React**, **WebSockets**, **Kafka** (event-driven), **PostgreSQL**, and
**JWT** security. Containerized with **Docker Compose**.

> Built as a learning + portfolio project. Every technology is explained in [`docs/`](docs)
> using a fixed format: *what → why → how → when → pros → cons → why here → alternatives.*

## ✨ Features

- 🔐 **Login / register** with JWT auth (passwords BCrypt-hashed)
- 👥 **People dashboard** — see everyone who's registered
- 💬 **Private 1:1 messaging** in real time (WebSocket / STOMP)
- 🔔 **Unread badges + in-app toasts + desktop notifications** + tab-title count
- 🖼️ **Profile pictures** (avatar upload)
- 📎 **Image & file sharing** in chats
- 🎨 **Themes** (Dark / Light / WhatsApp / Ocean) + **responsive** (mobile → desktop)
- ⚡ **Event-driven**: messages flow through **Kafka** (decoupled persistence + delivery)

## 🏗️ Architecture

```
                 ┌─────────────┐   WebSocket + REST    ┌────────────────────────┐
  Browser  ◄────►│   React     │ ◄───────────────────► │   Spring Boot backend   │
 (nginx in       │  (frontend) │                        │  Security (JWT) · STOMP │
  Docker)        └─────────────┘                        │  REST · JPA             │
                                                        └───────┬─────────┬──────┘
                                       publish event            │         │
                                            ▼                   ▼         ▼
                                      ┌──────────┐        ┌──────────┐  delivers via
                                      │  Kafka   │──────► │ Postgres │  WebSocket to
                                      │ topic    │ consume│  (JPA)   │  the recipient
                                      └──────────┘        └──────────┘
```

**Message flow (event-driven):** send → `DmController` **publishes** a `ChatMessageEvent`
to Kafka → `ChatEventConsumer` **saves** it to Postgres **and delivers** it over WebSocket
to the recipient + sender.

## 🧩 Tech stack

| Layer | Technology |
|-------|-----------|
| Frontend | React (Vite), STOMP.js + SockJS |
| Real-time | WebSocket + STOMP |
| Backend | Spring Boot (Java) |
| Security | Spring Security + JWT (BCrypt) |
| Messaging | Apache Kafka (event-driven) |
| Persistence | Hibernate / JPA |
| Database | PostgreSQL |
| Infra | Docker + Docker Compose, nginx |

## 🚀 Run it

### Option A — Docker Compose (one command)
```bash
docker compose up --build
# then open http://localhost:3000
```
This starts PostgreSQL, Kafka, the backend, and the nginx-served frontend, all wired together.

### Option B — Local dev
```bash
# 1. Infra (Postgres + Kafka) via Docker
docker run -d --name chat-postgres -e POSTGRES_DB=chatdb -e POSTGRES_USER=chatuser \
  -e POSTGRES_PASSWORD=chatpass -p 5432:5432 postgres:15
# (plus a Kafka container on 9092)

# 2. Backend
cd backend && ./mvnw spring-boot:run    # http://localhost:8080

# 3. Frontend
cd frontend && npm install && npm run dev   # http://localhost:5173
```

## 📚 Documentation (`docs/`)

| # | Topic |
|---|-------|
| 01 | Maven & Spring Boot |
| 02 | WebSockets & STOMP |
| 03 | PostgreSQL, JPA & Hibernate |
| 04 | React & the WebSocket client |
| 05 | Kafka & event-driven architecture |
| 06 | JWT & Spring Security |
| 07 | Docker & Docker Compose |

## 🗺️ Status & roadmap

- ✅ Real-time chat, persistence, JWT auth, private DMs, avatars, file sharing, notifications
- ✅ Event-driven messaging with Kafka
- ✅ Docker Compose
- ⏭️ Automated tests · live deployment · (optional) Camunda workflow · split into microservices

---

Built by **Apurva Gaikwad** — a Java full-stack portfolio project.
