# 06 · Authentication: JWT & Spring Security

> Format: **What → Why → How → When → Advantages → Disadvantages → Why here → Alternatives (and why not).**

---

## 🔐 JWT (JSON Web Token)

**What it is:** A signed, self-contained token the server gives you after login. It holds
claims (e.g. your username) and a **signature** the server can verify. Format:
`header.payload.signature`.

**Why it is used:** To prove who you are on every request **without the server storing a
session**. The token itself carries the identity, verified by the signature.

**How it is used (in our app):** On login, the server signs a JWT (`JwtService`). The React
app stores it and sends it as `Authorization: Bearer <token>` on every request. A filter
(`JwtAuthFilter`) validates it and marks the request as authenticated. The WebSocket sends
the same token on connect, so the socket knows who you are (needed for private messages).

**When it is used:** Stateless APIs, SPAs, mobile apps, service-to-service auth.

**Advantages:** Stateless (easy to scale — no shared session store); works across services;
carries claims; standard.

**Disadvantages:** Can't easily revoke before expiry; if stolen, it's usable until it
expires; payload is readable (base64) so never put secrets in it; needs safe storage on the
client.

**Why here:** Perfect for a React + Spring API: no server session, scales horizontally, and
it's a top interview keyword.

**Alternatives & why not:**
- **Server sessions + cookies** — server stores a session, browser holds a `JSESSIONID`
  cookie. Simple and easy to revoke, but stateful (needs sticky sessions or a shared store
  to scale). ❌ Less clean for a stateless SPA/API.
- **OAuth2 / OpenID Connect** ("Login with Google") — great for delegating auth to a
  provider; heavier to set up and not needed for a self-contained demo. ✅ Good future add.

**🎟️ Real-life analogy:** JWT is a **festival wristband**. Once you're checked in, the band
itself proves you're allowed in — staff just glance at it (verify), they don't look you up
in a list each time.

---

## 🛡️ Spring Security

**What it is:** Spring's framework for authentication (who you are) and authorization
(what you're allowed to do), via a chain of filters around every request.

**Why it is used:** Security is hard to get right by hand. Spring Security provides tested
building blocks: password hashing, the filter chain, rule-based access.

**How it is used (in our app):** `SecurityConfig` says `/api/auth/**` and `/ws/**` are
public, everything else needs a valid token; sessions are **stateless**; passwords are
hashed with **BCrypt**; our `JwtAuthFilter` runs on each request.

**When it is used:** Any Spring app that needs login/roles/protected endpoints.

**Advantages:** Robust, standard, flexible; handles hashing, CSRF, CORS, method security.

**Disadvantages:** Steep learning curve; lots of "magic"; easy to misconfigure.

**Why here:** It's the standard way to secure a Spring Boot API and integrates cleanly with
our JWT filter.

**Alternatives & why not:**
- **Hand-rolled auth** — reinventing security primitives; risky and not resume-worthy. ❌
- **Apache Shiro** — capable, but far less common in Spring shops than Spring Security. ❌

**🔑 BCrypt note:** we never store raw passwords — we store a **BCrypt hash** (slow + salted
on purpose), so even if the DB leaks, passwords aren't exposed.

**🏢 Real-life analogy:** Spring Security is **building security**: the lobby is open to all
(`/api/auth`), but past that you must show your wristband (JWT) at every door, and the
guards (filters) check it automatically.
