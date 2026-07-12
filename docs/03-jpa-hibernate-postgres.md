# 03 · PostgreSQL, JPA & Hibernate

> Format: **What → Why → How → When → Advantages → Disadvantages → Why here → Alternatives (and why not).**

---

## 🐘 PostgreSQL

**What it is:** A powerful, free, open-source **relational database** — it stores data in
tables (rows and columns) and lets you query it with SQL.

**Why it is used:** Chat needs data that lasts: users and message history. A relational
DB gives durable, structured, queryable storage with strong consistency.

**How it is used:** We run PostgreSQL (in a Docker container), and the app connects to it
using a JDBC URL + username/password (see `application.properties`).

**When it is used:** Whenever you need reliable, structured, related data with
transactions — the default choice for most backends.

**Advantages:** Rock-solid & reliable (ACID transactions); rich SQL & data types (JSON,
arrays); huge community; free.

**Disadvantages:** You must design a schema up front; horizontal scaling is harder than
some NoSQL databases; overkill for tiny throwaway data.

**Why here:** It's the most respected open-source SQL database and a top resume keyword.
Chat data (users, messages) is naturally relational.

**Alternatives & why not:**
- **MySQL** — very similar; we chose Postgres for its richer features & reputation.
- **MongoDB (NoSQL)** — flexible documents, but weaker relational querying & consistency;
  our data is relational, so SQL fits better. ❌
- **H2 (in-memory)** — great for tests, but data vanishes on restart. ❌ Not for real history.

**🗄️ Real-life analogy:** A relational DB is a **spreadsheet workbook with strict rules** —
each sheet (table) has fixed columns, and sheets link by IDs. Everything stays organized
and consistent.

---

## 🧬 JPA (Jakarta Persistence API)

**What it is:** A Java **specification** (a standard set of interfaces/annotations) for
mapping Java objects to database tables — so you work with objects, not raw SQL.

**Why it is used:** Writing SQL by hand for every operation is repetitive and error-prone.
JPA lets you say "save this object" and it handles the SQL.

**How it is used:** Annotate a class with `@Entity`, mark the key with `@Id`, and use a
repository to save/find. (JPA is just the *rules*; Hibernate is the *engine* that does it.)

**When it is used:** Almost any Spring Boot app that talks to a relational database.

**Advantages:** Less boilerplate; database-independent code; works with objects naturally.

**Disadvantages:** Hides the SQL (can cause surprises like the "N+1 query" problem);
learning curve; can be slow if misused.

**Why here:** It's the standard Java persistence approach and pairs perfectly with Spring
Data — huge productivity boost and a must-know for interviews.

**Alternatives & why not:**
- **Plain JDBC** — full control, but tons of manual boilerplate SQL. ❌
- **MyBatis / jOOQ** — you write SQL but mapped nicely; great, but less common in Spring
  Boot tutorials/jobs than JPA. ❌ Off the mainstream learning path.

---

## ⚙️ Hibernate + Spring Data JPA

**What it is:** **Hibernate** is the most popular *implementation* of JPA (the actual
engine that turns objects into SQL). **Spring Data JPA** sits on top and auto-generates
your repository code from interfaces.

**Why it is used:** Together they remove almost all data-access boilerplate: you declare a
`repository interface` and get `save/find/delete` for free, plus "derived queries" from
method names.

**How it is used (in our app):**
- `MessageEntity` → the table.
- `MessageRepository extends JpaRepository` → free CRUD + `findTop50ByOrderByTimestampAsc()`.
- `ChatService` calls the repository; `spring.jpa.hibernate.ddl-auto=update` makes
  Hibernate create/update the table automatically from the entity.

**When it is used:** The default data layer for Spring Boot + relational DB.

**Advantages:** Massive boilerplate savings; derived queries; caching; transactions.

**Disadvantages:** "Magic" can hide performance issues (N+1); complex mappings get tricky.

**Why here:** Fastest way to persist messages cleanly, and the exact stack asked about in
Java interviews ("Do you know Hibernate/JPA?").

**Alternatives & why not:** covered above (JDBC, MyBatis, jOOQ) — all more manual.

**🤖 Real-life analogy:** JPA is the **rulebook**; Hibernate is the **robot** that follows it
to file your objects into the database drawers; Spring Data is the **assistant** that builds
the robot's controls for you from a short description.

---

## 🔗 How this maps to our code

| File | Layer | Role |
|------|-------|------|
| `MessageEntity.java` | Entity | Maps a message to the `messages` table |
| `MessageRepository.java` | Repository | Auto CRUD + `findTop50...` history query |
| `ChatService.java` | Service | Business logic: save message, load history |
| `ChatController.java` | Controller | WebSocket: receive → save → broadcast |
| `ChatHistoryController.java` | Controller | REST `GET /api/messages` for past messages |

**Layered flow:** Controller → Service → Repository → Hibernate → PostgreSQL.
This separation is the **Single Responsibility Principle** in action.

**Next:** run it for real (Docker Postgres + Spring Boot), then build the React UI.
