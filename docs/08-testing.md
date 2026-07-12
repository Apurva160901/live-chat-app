# 08 · Testing

> Format: **What → Why → How → When → Advantages → Disadvantages → Why here → Alternatives (and why not).**

---

## 🧪 Automated testing (JUnit + Spring test slices)

**What it is:** Code that automatically checks your code behaves correctly — run with
`mvn test`. We use **JUnit 5** (the test framework) plus Spring Boot's **test slices**.

**Why it is used:** To catch bugs early, prevent regressions when you change things, and
document expected behavior. Green tests give confidence to refactor.

**How it is used (in our app):**
- **Unit test** (`JwtServiceTest`) — plain JUnit, no Spring, no DB. Verifies token
  generation/validation logic directly. Fast.
- **Repository slice tests** (`UserRepositoryTest`, `DirectMessageRepositoryTest`) —
  `@DataJpaTest` loads *only* the JPA layer against an in-memory **H2** database, so tests
  need no real Postgres or Kafka. Verifies queries like `conversation()` and `findByUsername`.

**When it is used:** On every build, in CI, before merging/deploying.

**Advantages:** Fast feedback; safe refactoring; living documentation; H2 slices avoid
external dependencies so tests run anywhere.

**Disadvantages:** Tests take effort to write/maintain; slice tests don't catch everything
(H2 ≠ Postgres exactly); over-mocking can hide real issues.

**Why here:** Demonstrates professional habits (a big differentiator at 3 YOE), and the
H2-backed slices run in seconds with no setup.

**Alternatives & why not:**
- **Full `@SpringBootTest` for everything** — boots the whole app (incl. Kafka), slower and
  more fragile; overkill for logic/query checks. We use lighter slices instead. ✅ still good for a few end-to-end tests later.
- **Testcontainers** (real Postgres/Kafka in Docker during tests) — highest fidelity, great
  for integration tests, but heavier/slower. A strong future addition. ✅ good "what next" answer.
- **No tests** — fastest to write, disastrous to maintain. ❌

**Test pyramid idea:** many fast **unit** tests at the bottom, fewer **slice/integration**
tests in the middle, very few slow **end-to-end** tests at the top.

**🏗️ Real-life analogy:** Tests are the **smoke alarms** of your codebase — cheap to install,
and they scream the moment something breaks, long before it becomes a fire in production.

**Run them:** `cd backend && ./mvnw test`
