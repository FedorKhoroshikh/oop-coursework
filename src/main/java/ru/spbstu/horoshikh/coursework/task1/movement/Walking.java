package ru.spbstu.horoshikh.coursework.task1.movement;

import ru.spbstu.horoshikh.coursework.task1.Point;

import java.util.Locale;
import java.util.function.Consumer;

/** On foot: slow, but gets anywhere and needs nothing. */
public final class Walking implements MovementStrategy {

    private static final double SPEED_KM_H = 5.0;
    private static final double STEP_KM = 0.0007;   // a step is about 70 cm

    @Override
    public String name() {
        return "on foot";
    }

    @Override
    public double speedKmH() {
        return SPEED_KM_H;
    }

    @Override
    public void move(Point from, Point to, Consumer<String> out) {
        double distance = from.distanceTo(to);
        long steps = Math.round(distance / STEP_KM);

        out.accept("The hero walks from %s to %s.".formatted(from, to));
        out.accept(String.format(Locale.ROOT, "  distance: %.1f km, time: %s, steps taken: %d",
                distance, MovementStrategy.formatDuration(hours(distance)), steps));
    }
}
