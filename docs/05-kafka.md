# 05 · Kafka & Event-Driven Architecture

> Format: **What → Why → How → When → Advantages → Disadvantages → Why here → Alternatives (and why not).**

---

## 📨 Apache Kafka

**What it is:** A distributed **event streaming platform**. Producers publish **events** to
named **topics**; consumers read them. Kafka stores events durably and lets many consumers
read the same stream independently.

**Why it is used:** To **decouple** parts of a system. The sender of an event doesn't need
to know who processes it, or wait for them. This enables scaling and resilience.

**How it is used (in our app):** When a user sends a message, `DmController` **publishes** a
`ChatMessageEvent` to the topic `chat.messages` and returns immediately. A separate
`ChatEventConsumer` reads the event and (1) saves it to Postgres, (2) delivers it over
WebSocket. Producer and consumer are wired via Spring Kafka.

**When it is used:** High-throughput messaging/streaming, event-driven microservices,
audit logs, decoupling slow work from fast requests.

**Advantages:** Decoupling; horizontal scale (partitions); durable + replayable; many
independent consumers; battle-tested at huge scale.

**Disadvantages:** Operational complexity (a broker to run); adds latency vs. a direct
call; overkill for tiny apps; ordering only guaranteed within a partition.

**Why here:** It turns the app into a real **event-driven architecture** — the #1 keyword
for a backend resume — and lets us add consumers (notifications, analytics) later without
touching the send path.

**Alternatives & why not:**
- **RabbitMQ** — excellent message broker (and it's on this machine); great for queues/
  routing, but Kafka is stronger for high-throughput streams/replay and is the bigger
  resume keyword. (Good "alternative" answer in interviews.)
- **Direct method call** (what we had before) — simplest, but tightly coupled and doesn't
  scale or survive a slow consumer. ❌
- **Redis Pub/Sub** — fast but fire-and-forget (no durability/replay). ❌ for a message log.

**📮 Real-life analogy:** A **post office**. You drop a letter in the box (publish) and leave.
The system reliably delivers copies to everyone subscribed, whenever they're ready — you
never wait, and you don't need to know who reads it.

---

## ⚡ Event-Driven Architecture (the pattern)

**What it is:** A design where components communicate by **emitting and reacting to events**
rather than calling each other directly.

**Why it is used:** Loose coupling → each part can scale, fail, and evolve independently.

**How it is used here:** "message sent" is an **event**. The web layer just announces it;
the persistence + delivery layer reacts. Adding a new reaction (e.g. push notifications)
means adding a new consumer, not editing the sender.

**When it is used:** Microservices, real-time pipelines, systems needing scalability/
resilience.

**Advantages:** Decoupling; scalability; resilience; extensibility; natural audit trail.

**Disadvantages:** Harder to trace a flow end-to-end; eventual consistency; more moving
parts; debugging spans multiple components.

**Why here:** Demonstrates senior-level thinking (decoupling, scaling) and sets up the
future split into microservices.

**Alternatives & why not:** **Request/response** (direct calls) — simpler and easier to
trace, but tightly coupled and harder to scale. We started there and evolved — itself a
great "how I improved the design" interview story.

**🍽️ Real-life analogy:** A restaurant kitchen. A waiter clips an **order ticket** to the rail
(event) and moves on. The chef, and separately the billing desk, each react to it — the
waiter doesn't cook or bill, and doesn't wait.
