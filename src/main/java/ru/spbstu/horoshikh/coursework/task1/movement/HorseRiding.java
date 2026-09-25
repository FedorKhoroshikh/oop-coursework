package ru.spbstu.horoshikh.coursework.task1.movement;

import ru.spbstu.horoshikh.coursework.task1.Point;

import java.util.Locale;
import java.util.function.Consumer;

/**
 * On horseback: faster than walking, but the horse needs a rest — every
 * {@value #RIDE_BEFORE_REST_H} hours of riding adds a stop. This private
 * logic inside move() is the point of the pattern: Hero knows nothing
 * about horse rests.
 */
public final class HorseRiding implements MovementStrategy {

    private static final double SPEED_KM_H = 30.0;
    private static final double RIDE_BEFORE_REST_H = 2.0;
    private static final double REST_H = 1.0 / 3;   // 20 minutes

    @Override
    public String name() {
        return "on horseback";
    }

    @Override
    public double speedKmH() {
        return SPEED_KM_H;
    }

    @Override
    public void move(Point from, Point to, Consumer<String> out) {
        double distance = from.distanceTo(to);
        double ride = hours(distance);
        int rests = (int) (ride / RIDE_BEFORE_REST_H);
        double total = ride + rests * REST_H;

        out.accept("The hero rides from %s to %s.".formatted(from, to));
        out.accept(String.format(Locale.ROOT, "  distance: %.1f km, in the saddle: %s, rests: %d",
                distance, MovementStrategy.formatDuration(ride), rests));
        out.accept("  total travel time: %s".formatted(MovementStrategy.formatDuration(total)));
    }
}
