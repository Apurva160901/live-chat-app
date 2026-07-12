# Résumé brief — Live Chat Application

Ready-to-use entries for your resume / LinkedIn. Repo: https://github.com/Apurva160901/live-chat-app

---

## ✅ Recommended (Projects section)

**Real-Time Chat Application** — *Java, Spring Boot, React, WebSocket, Apache Kafka, PostgreSQL, Docker*

- Built a full-stack, **WhatsApp-style real-time messaging** app with **Spring Boot** and **React**, using **WebSocket/STOMP** for instant bidirectional 1:1 delivery.
- Architected an **event-driven** backend with **Apache Kafka** — decoupling message send from persistence/delivery so consumers scale independently and new ones (notifications, analytics) plug in without touching the send path.
- Secured the API with **stateless JWT auth** (Spring Security, BCrypt) and **authenticated WebSocket connections** to route private messages per user.
- Persisted users, conversations, and attachments in **PostgreSQL via Hibernate/JPA**; added avatars and **image/file sharing** (multipart uploads).
- **Containerized** the full stack (backend, frontend, Postgres, Kafka) with **Docker Compose** for one-command runs; wrote **JUnit + JPA-slice tests**.

---

## Compact (2 lines)

**Real-Time Chat App** | *Spring Boot, React, WebSocket, Kafka, PostgreSQL, JWT, Docker*
Full-stack real-time 1:1 messaging with an **event-driven Kafka** backend, **JWT-secured** WebSockets, PostgreSQL/JPA persistence, file sharing, and a **Docker Compose** stack with automated tests.

---

## One-liner (LinkedIn / summary)

Full-stack real-time chat app (Spring Boot + React + WebSocket + Kafka + PostgreSQL), featuring event-driven messaging, JWT security, and containerized deployment.

---

## Tips
- Lead with engineering keywords (Kafka, event-driven, WebSocket, JWT, Docker) — recruiters/ATS scan for them.
- Be ready to defend every bullet with a STAR story (why Kafka? how to scale WebSocket?).
- Only add metrics you actually measured (e.g. concurrent connections from a load test) — never invent numbers.
