# 01 · Maven & Spring Boot

> Format for every concept: **What → Why → How → When → Advantages → Disadvantages → Why here → Alternatives (and why not).**

---

## 🔧 Maven

**What it is:** A build tool for Java. It compiles your code, downloads the libraries
you depend on, runs tests, and packages everything into a runnable file (a `.jar`).
It is driven by one file: `pom.xml`.

**Why it is used:** Real apps use dozens of libraries, each needing a specific version.
Managing that by hand is painful. Maven does it automatically from `pom.xml`.

**How it is used:** You list dependencies in `pom.xml`; Maven downloads them from
"Maven Central" (a giant online library store) and builds your project.
Common commands: `mvn compile`, `mvn test`, `mvn package`, `mvn spring-boot:run`.

**When it is used:** Every time you build, test, run, or package a Java project.

**Advantages:** Automatic dependency management; standard project layout everyone knows;
huge ecosystem; repeatable builds.

**Disadvantages:** XML is verbose; slower and less flexible than newer tools; the
"dependency tree" can get confusing when versions clash.

**Why here:** It's the most common, most interview-relevant Java build tool, and Spring
Boot has first-class Maven support.

**Alternatives & why not:**
- **Gradle** — faster, uses concise Groovy/Kotlin scripts. We skipped it because Maven's
  XML is easier to *read and learn* first, and it's more common in enterprise Java jobs.
- **Ant** — older, very manual. Outdated; no built-in dependency management.

**🍽️ Real-life analogy:** Maven is a **grocery-delivery service with a recipe card**
(`pom.xml`). You list ingredients (libraries); it fetches exact brands/versions and
lays out the kitchen the same way every time, so any cook can step in.

---

## 🌱 Spring Boot

**What it is:** A framework that makes building Java web apps fast by giving you
"batteries-included" defaults and **auto-configuration**, plus an **embedded web server**
so you just run a single program.

**Why it is used:** Plain Spring needs lots of manual setup (servers, config, wiring).
Spring Boot removes that boilerplate so you focus on features.

**How it is used:** You add "starter" dependencies (e.g. `spring-boot-starter-web`),
annotate a class with `@SpringBootApplication`, and call `SpringApplication.run(...)`.
Boot sees what's on the classpath and configures it for you (sees the web starter →
starts a Tomcat server; sees the JPA starter → sets up the database layer).

**When it is used:** For REST APIs, microservices, WebSocket servers, scheduled jobs —
most modern Java backends.

**Advantages:** Very fast to start; sensible defaults; embedded server (no separate
install); massive ecosystem; production features (health checks, metrics).

**Disadvantages:** "Magic" auto-config can be hard to debug; larger memory footprint;
so much is hidden that beginners may not learn the underlying Spring.

**Why here:** It's the industry standard for Java backends and the single most important
skill on a Java-developer resume. It gives us REST + WebSocket + JPA with almost no setup.

**Alternatives & why not:**
- **Plain Spring (no Boot)** — more control, but far more manual setup. Not worth it here.
- **Quarkus / Micronaut** — faster startup, great for serverless/K8s, but smaller
  communities and fewer jobs/tutorials — harder to learn from.
- **Node.js (Express)** — great, but it's JavaScript, not Java — off-goal for you.

**🏨 Real-life analogy:** Plain Spring is buying land and building a hotel from scratch.
Spring Boot is a **furnished serviced apartment** — plumbing, wifi, and furniture already
set up; you just move in (write features) and start living.

---

## 🔗 How this maps to our `pom.xml`

| In `pom.xml` | Meaning |
|--------------|---------|
| `spring-boot-starter-parent` | Inherit tested default versions for everything |
| `spring-boot-starter-web` | REST controllers + embedded Tomcat server |
| `spring-boot-starter-websocket` | Real-time messaging (live chat) |
| `spring-boot-starter-data-jpa` | Database access via Hibernate |
| `spring-boot-starter-validation` | Validate incoming data |
| `postgresql` (runtime) | Driver to connect to PostgreSQL |
| `@SpringBootApplication` | Marks the entry class; enables auto-config + scanning |

**Next docs:** `02` WebSockets · `03` JPA/Hibernate & PostgreSQL · `04` React · then Kafka,
Docker, Camunda, microservices, auth (JWT/OAuth/SSE), cookies & API headers.
