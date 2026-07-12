# 07 · Docker & Docker Compose

> Format: **What → Why → How → When → Advantages → Disadvantages → Why here → Alternatives (and why not).**

---

## 🐳 Docker

**What it is:** A tool to package an app **plus everything it needs** (runtime, libraries,
config) into a **container** — a lightweight, isolated box that runs the same anywhere.

**Why it is used:** It kills "works on my machine" bugs. The container behaves identically
on your laptop, a teammate's, or a server.

**How it is used:** You write a `Dockerfile` (a recipe: base image + copy code + how to
run). `docker build` turns it into an image; `docker run` starts a container from it.

**When it is used:** Packaging/shipping apps, running databases/brokers locally, CI, and
deploying to servers/Kubernetes.

**Advantages:** Consistent everywhere; isolated; fast to start; easy to share; the base
of modern deployment.

**Disadvantages:** A learning curve; images can get large; another layer to debug.

**Why here:** Lets anyone (a recruiter!) run our whole app with one command, and it's the
foundation for deploying. Strong DevOps signal on a resume.

**Alternatives & why not:**
- **Install everything manually** (Java, Node, Postgres, Kafka) — slow, fragile, differs
  per machine. ❌
- **VMs** — full virtual machines are heavy and slow vs. lightweight containers. ❌

**📦 Real-life analogy:** A container is a **shipping container**. However you transport it
(truck, ship, train / laptop, server, cloud), the contents stay exactly the same.

---

## 🧩 Docker Compose

**What it is:** A tool to define and run **multiple containers together** from one file
(`docker-compose.yml`) with a single command.

**Why it is used:** Our app is several services (Postgres, Kafka, backend, frontend).
Starting each by hand with the right options and networking is tedious and error-prone.

**How it is used:** Describe each service in `docker-compose.yml`; run `docker compose up`.
Compose builds images, creates a private network (services reach each other **by name** —
`postgres`, `kafka`, `backend`), and starts everything in order.

**When it is used:** Local multi-service development and simple multi-container deployments.

**Advantages:** One command for the whole stack; reproducible; built-in networking &
volumes; `depends_on` + health checks for start order.

**Disadvantages:** Great for one machine, but not a full production orchestrator (that's
Kubernetes); still needs the images to build.

**Why here:** Turns "install + configure 4 things" into `docker compose up` — reproducible
and demo-friendly.

**Alternatives & why not:**
- **Kubernetes** — powerful but heavy for local dev; overkill here (a future step). ❌ for now
- **Running each `docker run` by hand** — verbose and easy to get wrong. ❌

**🎛️ Real-life analogy:** If a container is one appliance, Compose is the **whole kitchen
wired up** — flip one switch and the fridge, oven, and lights all come on, already connected.

---

## 🔗 How this maps to our project

| File | Role |
|------|------|
| `backend/Dockerfile` | Multi-stage: build the jar with Maven, then run it on a slim JRE |
| `frontend/Dockerfile` | Build the React bundle, serve it with nginx |
| `frontend/nginx.conf` | Serve the app + proxy `/api`, `/ws`, `/uploads` to the backend |
| `docker-compose.yml` | Postgres + Kafka + backend + frontend, wired together |

**Networking note:** inside Compose, services talk by **service name** (e.g. the backend
uses `jdbc:postgresql://postgres:5432/...` and `kafka:9092`), not `localhost`. We pass
those via environment variables that override `application.properties`.

**Run it:** `docker compose up --build` → open `http://localhost:3000`.

**Next docs to add:** `05` Kafka, `06` JWT/Security (backfilling these), then tests & deploy.
