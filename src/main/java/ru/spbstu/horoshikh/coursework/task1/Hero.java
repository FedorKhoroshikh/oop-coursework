package ru.spbstu.horoshikh.coursework.task1;

import ru.spbstu.horoshikh.coursework.task1.movement.MovementStrategy;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * A hero of a computer game. Travels between points, yet knows nothing about
 * how the travelling is done: the way of moving lives in a strategy field and
 * is swapped at runtime through {@link #setMovement(MovementStrategy)}.
 */
public class Hero {

    private final String name;
    private Point position;
    private MovementStrategy movement;

    public Hero(String name, Point position, MovementStrategy movement) {
        this.name = Objects.requireNonNull(name, "hero name");
        this.position = Objects.requireNonNull(position, "starting position");
        this.movement = Objects.requireNonNull(movement, "movement strategy");
    }

    public String name() {
        return name;
    }

    public Point position() {
        return position;
    }

    public MovementStrategy movement() {
        return movement;
    }

    /** Swaps the way of moving while the program is running. */
    public void setMovement(MovementStrategy movement) {
        this.movement = Objects.requireNonNull(movement, "movement strategy");
    }

    /**
     * Travel to the destination using the current strategy.
     * <p>
     * All the work is delegated: there is not a single "if walking then ..."
     * branch here, and adding another way of moving leaves this method
     * untouched.
     */
    public void move(Point destination, Consumer<String> out) {
        Objects.requireNonNull(destination, "destination");
        movement.move(position, destination, out);
        position = destination;
    }

    @Override
    public String toString() {
        return "%s at %s, moving %s".formatted(name, position, movement.name());
    }
}
