package ch.learn;

import java.util.List;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * TodoResource exposes the REST API for managing todos under /api/todos.
 *
 * It's a JAX-RS resource (a plain class with annotated methods). Quarkus turns
 * each annotated method into an HTTP endpoint and uses Jackson to convert
 * between JSON and our Java objects automatically.
 *
 * Because we use the Active Record pattern (PanacheEntity), this class talks to
 * the {@link Todo} entity directly — no repository layer in between.
 */
@Path("/api/todos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TodoResource {

    /**
     * GET /api/todos
     * Returns every todo, newest first. Status 200 with a JSON array.
     */
    @GET
    public List<Todo> list() {
        return Todo.listAllOrderedByCreatedAt();
    }

    /**
     * POST /api/todos
     * Creates a new todo from a JSON body like {"title": "Buy milk"}.
     *
     * Write operations need @Transactional so Hibernate flushes the INSERT to
     * the database inside a transaction. Returns 201 Created with the new todo
     * (now carrying its generated id and createdAt).
     */
    @POST
    @Transactional
    public Response create(Todo todo) {
        // Basic validation: reject missing/blank titles with 400 Bad Request.
        if (todo == null || todo.title == null || todo.title.isBlank()) {
            throw new WebApplicationException("Title is required", Response.Status.BAD_REQUEST);
        }

        // Only trust the title from the client; the server owns id/completed/createdAt.
        Todo created = new Todo();
        created.title = todo.title.trim();
        created.completed = false;
        created.persist(); // INSERT happens here

        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    /**
     * PUT /api/todos/{id}
     * Toggles the completed flag of an existing todo.
     * Returns 200 with the updated todo, or 404 if no todo has that id.
     */
    @PUT
    @Path("/{id}")
    @Transactional
    public Todo toggle(@PathParam("id") Long id) {
        Todo todo = Todo.findById(id);
        if (todo == null) {
            throw new WebApplicationException("Todo " + id + " not found", Response.Status.NOT_FOUND);
        }
        // Flip the flag. Because the entity is "managed" inside this transaction,
        // Hibernate automatically issues an UPDATE when the method returns.
        todo.completed = !todo.completed;
        return todo;
    }

    /**
     * DELETE /api/todos/{id}
     * Deletes a todo. Returns 204 No Content on success, or 404 if not found.
     */
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = Todo.deleteById(id);
        if (!deleted) {
            throw new WebApplicationException("Todo " + id + " not found", Response.Status.NOT_FOUND);
        }
        return Response.noContent().build();
    }
}
