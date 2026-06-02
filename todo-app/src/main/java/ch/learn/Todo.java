package ch.learn;

import java.time.LocalDateTime;
import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.panache.common.Sort;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/**
 * Todo is our single JPA entity, mapped to the "todos" table.
 *
 * It extends {@link PanacheEntity}, which uses the Active Record pattern:
 * the entity itself carries both the data (its fields) AND the persistence
 * operations (persist(), delete(), findById(), list(), ...). That means we
 * don't need a separate DAO/Repository class for simple CRUD — handy for a
 * learning project where we want to see everything in one place.
 *
 * PanacheEntity also gives us a generated {@code Long id} primary key for free,
 * so we only declare the fields that are specific to a Todo.
 */
@Entity
@Table(name = "todos")
public class Todo extends PanacheEntity {

    /** The text of the todo, e.g. "Buy milk". */
    public String title;

    /** Whether the todo has been ticked off. Defaults to false. */
    public boolean completed;

    /** When the todo was created — used to sort the list newest-first. */
    public LocalDateTime createdAt;

    /**
     * JPA lifecycle callback: Hibernate calls this automatically just before
     * the row is first inserted, so every new Todo gets a creation timestamp
     * without the REST layer having to set it.
     */
    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    /**
     * Returns all todos, newest first.
     *
     * This is a "static finder" — a Panache idiom where query methods live as
     * static methods on the entity, keeping all Todo-related logic together.
     */
    public static List<Todo> listAllOrderedByCreatedAt() {
        return listAll(Sort.by("createdAt").descending());
    }
}
