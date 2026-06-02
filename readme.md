cd todo-app

docker compose up -d     # Datenbank starten
./mvnw quarkus:dev       # Backend starten
# → http://localhost:8090/ öffnen