package ru.spbstu.horoshikh.coursework.task1;

import java.util.Locale;

/**
 * A point on the game map. Immutable, hence a record: the destination cannot
 * be mutated once the hero is already on the way.
 */
public record Point(double x, double y) {

    /** Straight-line distance to another point, in kilometres. */
    public double distanceTo(Point other) {
        return Math.hypot(other.x - x, other.y - y);
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(%.1f; %.1f)", x, y);
    }
}
