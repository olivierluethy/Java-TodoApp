# Todo App — Explained for a Total Beginner

This is a small **Todo list** app. You type a task, it gets saved, and it shows
up on screen. You can tick it off, edit it, or delete it.

This README explains **how the whole thing works**, assuming you know *nothing*
about Java. Read it top to bottom and it should just click. 🙂

---

## 1. The big picture: three boxes talking to each other

Every web app like this is really **three separate programs** passing messages:

```
   ┌─────────────┐      ┌─────────────┐      ┌─────────────┐
   │  1. BROWSER │ ───► │  2. BACKEND │ ───► │ 3. DATABASE │
   │   (the page │      │   (Java /   │      │ (PostgreSQL)│
   │  you click) │ ◄─── │   Quarkus)  │ ◄─── │             │
   └─────────────┘      └─────────────┘      └─────────────┘
      "the face"          "the brain"          "the memory"
```

1. **The browser** shows the page and the buttons. It can't save anything
   permanently — it just *asks* the backend to do things.
2. **The backend** is a Java program. It listens for requests, decides what to
   do, and talks to the database. This is the part you're learning.
3. **The database** is where the todos actually live, so they're still there
   after you close the browser or restart the app.

A helpful analogy — ordering at a restaurant:

| Restaurant            | This app                                  |
|-----------------------|-------------------------------------------|
| You, the customer     | The **browser** (you click things)        |
| The waiter            | The **backend** (takes your order)        |
| The kitchen storeroom | The **database** (where things are kept)  |

You never walk into the kitchen yourself. You tell the waiter, the waiter
fetches from the storeroom. Same here: the browser never touches the database —
it always goes through the backend.

---

## 2. What is an "API"? (the menu the waiter understands)

The browser and the backend talk over the internet using **HTTP** — the same
thing that happens when you visit any website. The backend offers a fixed list
of things it knows how to do. That list is called the **API**.

Each item in the API is an **endpoint** = a *verb* + an *address*:

| What you want to do        | Verb     | Address              |
|----------------------------|----------|----------------------|
| Show me all todos          | `GET`    | `/api/todos`         |
| Add a new todo             | `POST`   | `/api/todos`         |
| Tick a todo on/off         | `PUT`    | `/api/todos/{id}`    |
| **Edit a todo's text**     | `PATCH`  | `/api/todos/{id}`    |
| Delete a todo              | `DELETE` | `/api/todos/{id}`    |

- The **verb** is the *kind* of action. They're a fixed standard:
  `GET` = read, `POST` = create, `PUT`/`PATCH` = change, `DELETE` = remove.
- The **address** says *what* you're acting on. `{id}` is a placeholder for a
  todo's number — e.g. `/api/todos/3` means "the todo with id 3".

That's it. The whole "API" is just these five sentences the waiter understands.

---

## 3. Follow one todo from click to saved

Let's trace **exactly** what happens when you type "Buy milk" and click **Add**.
This is the most important section — if you get this, you get the app.

```
 You type "Buy milk" and click Add
   │
   │  ❶ The page's JavaScript sends:   POST /api/todos   { "title": "Buy milk" }
   ▼
 BACKEND receives it (the create() method in TodoResource.java)
   │
   │  ❷ It checks: is the title empty?  → if yes, reply "400 Bad Request"
   │  ❸ It makes a new Todo object and calls  todo.persist()
   ▼
 DATABASE runs:   INSERT INTO todos (title, completed, createdAt) VALUES (...)
   │
   │  ❹ The database saves the row and gives it an id, e.g. 1
   ▼
 BACKEND replies:  "201 Created"  + the saved todo as text:
                   { "id": 1, "title": "Buy milk", "completed": false, ... }
   │
   │  ❺ The page asks again:  GET /api/todos   (give me the fresh list)
   ▼
 The page redraws the list, and "Buy milk" appears at the top. ✅
```

Notice that adding a todo is actually **two** messages: one to *save it*
(`POST`), then one to *re-read the whole list* (`GET`). The app always re-reads
the list after a change, so what you see on screen is always exactly what's in
the database. Simple and trustworthy.

The other buttons work the same way, just with a different verb:

- **Tick the checkbox** → `PUT /api/todos/1` → backend flips `completed` true/false.
- **Edit (pencil)** → `PATCH /api/todos/1` with the new text → backend changes the title.
- **Delete (trash)** → `DELETE /api/todos/1` → backend removes the row.

After each one, the page re-reads the list with `GET` and redraws. Always the
same rhythm: **do the action, then reload the list.**

---

## 4. The words you'll keep hearing (plain-English glossary)

You don't need to memorize these, but here's what they mean in *this* app:

- **Java** — the programming language the backend is written in.
- **Quarkus** — a *framework*: a big helpful toolbox that does the boring,
  hard parts of a Java web app for you (listening for HTTP, converting data,
  talking to the database) so you only write the interesting bits.
- **Endpoint / Route** — one entry in the API (a verb + address), handled by one
  Java method.
- **Entity** — a Java class that mirrors a database table. Our `Todo` class
  mirrors the `todos` table. One Java `Todo` object = one row in the table.
- **Panache** — a Quarkus helper that lets a `Todo` object **save itself** with
  `todo.persist()`, instead of you writing SQL by hand. (`persist` just means
  "save".)
- **Hibernate** — the engine under Panache that translates Java objects into
  real SQL commands like `INSERT` and `SELECT`. You never see it working; it
  just does.
- **JSON** — the simple text format the browser and backend use to send data,
  e.g. `{ "title": "Buy milk" }`. Think of it as a labeled box of values.
- **CRUD** — the four basic things you can do with data: **C**reate, **R**ead,
  **U**pdate, **D**elete. Our five endpoints are exactly CRUD (with two kinds
  of update: toggle and edit).

---

## 5. Where everything lives (the file map)

```
todo-app/
├── docker-compose.yml          Starts the PostgreSQL database in one command
├── pom.xml                     The shopping list of tools the project needs
└── src/main/
    ├── java/ch/learn/
    │   ├── Todo.java           The "Todo" blueprint = one row in the database
    │   └── TodoResource.java   The API: the 5 endpoints (the waiter's skills)
    └── resources/
        ├── application.properties   Settings: which database, which port, etc.
        └── META-INF/resources/
            └── index.html      The ENTIRE front-end: the page + its JavaScript
```

Only **two Java files** to understand:

- **`Todo.java`** describes *what a todo is* (it has a title, a done-flag, and a
  created-time). It extends `PanacheEntity`, which automatically gives every
  todo an `id` number and the ability to save/find/delete itself.
- **`TodoResource.java`** describes *what you can do* with todos — it contains
  the five endpoint methods from the table above. Each method is a few lines.

And **one front-end file**, `index.html`, which holds both the visible page and
the JavaScript that calls the API. No build step, no npm — just a file.

---

## 6. How to run it yourself

You need: **Java (JDK 17+)**, **Docker**, and that's basically it (the included
`./mvnw` command downloads everything else).

```shell
# 1. Start the database (note: "docker compose", with a space)
docker compose up -d

# 2. Start the backend in dev mode (auto-reloads when you edit a file)
./mvnw quarkus:dev

# 3. Open the app in your browser
#    http://localhost:8080      (or 8090 if you changed the port)
```

> The front-end is served by the backend itself, so once step 2 is running,
> just open the address — there's nothing else to start.

Handy extras while it's running:
- **Swagger UI** (a clickable page to try the API): `http://localhost:8080/q/swagger-ui`
- **Dev UI** (Quarkus tools): `http://localhost:8080/q/dev/`

To stop: press `Ctrl+C` in the backend terminal, then `docker compose down`.

---

## 7. Try the API by hand (optional, but it makes it click)

You can talk to the backend directly from a terminal with `curl`, no browser
needed. (Swap `8080` for `8090` if that's your port.)

```shell
# See all todos
curl localhost:8080/api/todos

# Add one
curl -X POST localhost:8080/api/todos \
  -H 'Content-Type: application/json' -d '{"title":"Buy milk"}'

# Edit todo #1's text   (the new PATCH endpoint)
curl -X PATCH localhost:8080/api/todos/1 \
  -H 'Content-Type: application/json' -d '{"title":"Buy oat milk"}'

# Tick todo #1 on/off
curl -X PUT localhost:8080/api/todos/1

# Delete todo #1
curl -X DELETE localhost:8080/api/todos/1
```

Run these and watch the browser change after a refresh — you're doing exactly
what the page's buttons do, just by hand. That's the whole app. 🎉
