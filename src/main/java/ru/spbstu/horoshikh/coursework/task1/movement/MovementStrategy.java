package ru.spbstu.horoshikh.coursework.task1.movement;

import ru.spbstu.horoshikh.coursework.task1.Point;

import java.util.Locale;
import java.util.function.Consumer;

/**
 * A way for the hero to travel — the strategy of the Strategy pattern.
 * <p>
 * Implementations are interchangeable: {@link ru.spbstu.horoshikh.coursework.task1.Hero} depends on this
 * interface only and never learns whether the hero walks, rides or flies.
 * A new way of moving can be added without touching a single line in Hero.
 */
public interface MovementStrategy {

    /** Display name, used by the menu and the travel log. */
    String name();

    /** Average travel speed, km/h. */
    double speedKmH();

    /**
     * Travel from {@code from} to {@code to}.
     * <p>
     * The log goes into the supplied sink instead of {@code System.out}:
     * the coursework can feed the very same strategies into a JavaFX text
     * area without changing them.
     */
    void move(Point from, Point to, Consumer<String> out);

    /** Pure travel time, ignoring any per-strategy overhead, in hours. */
    default double hours(double distanceKm) {
        return distanceKm / speedKmH();
    }

    /** Hours in a human-readable form: {@code 3.5} -> {@code "3h 30m"}. */
    static String formatDuration(double hours) {
        int totalMinutes = (int) Math.round(hours * 60);
        return String.format(Locale.ROOT, "%dh %02dm", totalMinutes / 60, totalMinutes % 60);
    }
}
