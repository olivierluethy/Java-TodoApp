# Java Todo App

A small full-stack **todo list** learning project: a Java/Quarkus REST backend
backed by PostgreSQL, with a plain HTML/JavaScript frontend served by the same
app. You can add, complete, edit, and delete tasks.

The active app lives in [`todo-app/`](todo-app/), which contains a detailed,
beginner-friendly README explaining how every part works.

## Features

- Create, list, complete, edit, and delete todos
- REST API under `/api/todos` (GET / POST / PUT / PATCH / DELETE)
- PostgreSQL persistence via Hibernate ORM with Panache (Active Record)
- Frontend (`index.html`) served from the same origin as the API

## Tech Stack

- **Backend:** Java + [Quarkus](https://quarkus.io/), Hibernate ORM with Panache
- **Database:** PostgreSQL 16 (via Docker Compose)
- **Frontend:** vanilla HTML / CSS / JavaScript
- **Build:** Maven (`./mvnw`)

## Run

```bash
cd todo-app
docker compose up -d     # start the PostgreSQL database
./mvnw quarkus:dev       # start the backend (and frontend) in dev mode
```

Then open <http://localhost:8090/>.

## Repository Layout

```
todo-app/   # the todo application (backend + frontend) — start here
todo-api/   # Quarkus starter scaffold (greeting resource)
```

> Database credentials in `docker-compose.yml` / `application.properties`
> (`todo` / `todo`) are local development defaults.
