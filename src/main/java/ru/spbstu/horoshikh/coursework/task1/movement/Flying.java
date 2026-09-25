package ru.spbstu.horoshikh.coursework.task1.movement;

import ru.spbstu.horoshikh.coursework.task1.Point;

import java.util.Locale;
import java.util.function.Consumer;

/**
 * Flight: the fastest option, but take-off and landing cost a fixed amount
 * of time, so over short distances flying loses to riding — the demo makes
 * that visible.
 */
public final class Flying implements MovementStrategy {

    private static final double SPEED_KM_H = 120.0;
    private static final double TAKEOFF_LANDING_H = 0.25;   // 15 minutes

    @Override
    public String name() {
        return "flying";
    }

    @Override
    public double speedKmH() {
        return SPEED_KM_H;
    }

    @Override
    public void move(Point from, Point to, Consumer<String> out) {
        double distance = from.distanceTo(to);
        double flight = hours(distance);
        double total = flight + TAKEOFF_LANDING_H;

        out.accept("The hero flies from %s to %s.".formatted(from, to));
        out.accept(String.format(Locale.ROOT,
                "  distance: %.1f km, airborne: %s, take-off and landing: %s",
                distance,
                MovementStrategy.formatDuration(flight),
                MovementStrategy.formatDuration(TAKEOFF_LANDING_H)));
        out.accept("  total travel time: %s".formatted(MovementStrategy.formatDuration(total)));
    }
}
