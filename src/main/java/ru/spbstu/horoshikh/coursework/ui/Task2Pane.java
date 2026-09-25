package ru.spbstu.horoshikh.coursework.ui;

import java.util.Locale;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import ru.spbstu.horoshikh.coursework.task2.AnnotatedCallRunner;
import ru.spbstu.horoshikh.coursework.task2.TextTools;

/**
 * Task 2 (annotations + reflection): runs {@link AnnotatedCallRunner} over a
 * fresh {@link TextTools} instance, exactly like the lab's console demo.
 * There is nothing for the user to type here — the point of the task is the
 * reflective call, not its input — so the only control is the button that
 * triggers it.
 */
public final class Task2Pane extends VBox {

    private final TextArea output = new TextArea();

    public Task2Pane() {
        super(10);
        setPadding(new Insets(12));

        Button run = new Button("Run reflection demo");
        run.setOnAction(e -> new AnnotatedCallRunner(this::log).run(new TextTools()));

        Button clear = new Button("Clear output");
        clear.setOnAction(e -> output.clear());

        output.setEditable(false);
        output.setWrapText(true);
        VBox.setVgrow(output, Priority.ALWAYS);

        getChildren().addAll(
                new HBox(8, run, clear),
                new Label("Calls every @Repeat-annotated protected/private method of TextTools."),
                output);
    }

    private void log(String line) {
        output.appendText(String.format(Locale.ROOT, "%s%n", line));
    }
}
