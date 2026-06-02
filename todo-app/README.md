# Todo App — Quarkus + Panache + PostgreSQL

A small full-stack Todo application, built as a learning project to explore
Java backend development with [Quarkus](https://quarkus.io/) and a clean,
build-step-free frontend.

- **Backend:** Java, Quarkus 3.x, Hibernate ORM with Panache (Active Record),
  PostgreSQL, RESTEasy/Jackson for JSON, CORS enabled for local dev.
- **Frontend:** a single `index.html` (HTML5 + Tailwind via CDN + vanilla JS),
  served by Quarkus itself from `src/main/resources/META-INF/resources/`.

---

## Prerequisites

- **JDK 17+** — the project is currently configured for Java 17 (the version on
  this machine). To target **Java 21** as originally intended, install JDK 21
  and change `<maven.compiler.release>` in `pom.xml` from `17` to `21`.
- **Maven** — not strictly required; the included `./mvnw` wrapper downloads it.
- **Docker** (with Docker Compose) — to run PostgreSQL locally.

## 1. Start the database

```shell
docker-compose up -d
```

This starts a PostgreSQL 16 container (`tododb`) on `localhost:5432` with
database `tododb`, user `todo`, password `todo`. Data persists in a named
volume. Stop it later with `docker-compose down` (add `-v` to wipe the data).

## 2. Run the backend in dev mode

```shell
./mvnw quarkus:dev
```

Dev mode gives you live reload (edit Java/HTML and refresh — no restart). The
schema is recreated on each start (`drop-and-create`), so you always begin with
a clean database.

- App + frontend: <http://localhost:8080>
- Quarkus Dev UI: <http://localhost:8080/q/dev/>
- OpenAPI spec: <http://localhost:8080/q/openapi> · Swagger UI: <http://localhost:8080/q/swagger-ui>

## 3. Open the frontend

Just visit <http://localhost:8080> — the Todo UI is served by the backend, so
there's nothing else to start. (CORS is also enabled, so you can alternatively
open `index.html` from a different origin during development.)

---

## API endpoints

Base path: `/api/todos`

| Method   | Path              | Description                          | Status      |
|----------|-------------------|--------------------------------------|-------------|
| `GET`    | `/api/todos`      | List all todos, newest first         | `200`       |
| `POST`   | `/api/todos`      | Create a todo — body `{"title":"…"}` | `201` / `400` |
| `PUT`    | `/api/todos/{id}` | Toggle the `completed` flag          | `200` / `404` |
| `DELETE` | `/api/todos/{id}` | Delete a todo                        | `204` / `404` |

Quick test with `curl`:

```shell
curl -s localhost:8080/api/todos
curl -s -X POST localhost:8080/api/todos -H 'Content-Type: application/json' -d '{"title":"Buy milk"}'
curl -s -X PUT localhost:8080/api/todos/1
curl -s -X DELETE localhost:8080/api/todos/1 -i
```

---

## Project layout

```
todo-app/
├── docker-compose.yml                 PostgreSQL 16 for local dev
├── pom.xml                            Maven build + Quarkus extensions
└── src/main/
    ├── java/ch/learn/
    │   ├── Todo.java                  JPA entity (PanacheEntity, Active Record)
    │   └── TodoResource.java          REST API under /api/todos
    └── resources/
        ├── application.properties     DB, Hibernate, CORS, HTTP config
        └── META-INF/resources/
            └── index.html             The whole frontend (no build step)
```

## Packaging

```shell
./mvnw package
java -jar target/quarkus-app/quarkus-run.jar
```
