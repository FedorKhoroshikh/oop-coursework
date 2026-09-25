package ru.spbstu.horoshikh.coursework.app;

/**
 * A plain entry point, separate from {@link CourseworkApp}.
 * <p>
 * The JVM refuses to start a JavaFX app from the classpath (as opposed to
 * the module path) when the class holding {@code main} directly extends
 * {@link javafx.application.Application} — it fails with "JavaFX runtime
 * components are missing" even though the JavaFX jars are right there on
 * the classpath. Routing through a class that does not extend Application
 * sidesteps that check, so IDEA's own Run button works without extra
 * {@code --module-path}/{@code --add-modules} VM options.
 */
public final class Launcher {

    public static void main(String[] args) {
        CourseworkApp.main(args);
    }
}
