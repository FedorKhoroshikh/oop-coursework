package ru.spbstu.horoshikh.coursework.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import ru.spbstu.horoshikh.coursework.task4.StreamTasks;

/**
 * Task 4 (Stream API): every {@link StreamTasks} method behind its own
 * button, fed from two text fields. The parsing here is the only thing that
 * is not Stream-API-only — the six methods themselves are unchanged from
 * the lab.
 */
public final class Task4Pane extends VBox {

    private final TextField numbersField = new TextField("4, 8, 15, 16, 23, 42, 15");
    private final TextField wordsField = new TextField("cat, dog, elephant");
    private final TextArea output = new TextArea();

    public Task4Pane() {
        super(10);
        setPadding(new Insets(12));

        Button average = new Button("Average");
        average.setOnAction(e -> withNumbers(numbers ->
                log("average(" + numbers + ") = " + StreamTasks.average(numbers))));

        Button squares = new Button("Squares of unique");
        squares.setOnAction(e -> withNumbers(numbers ->
                log("squaresOfUnique(" + numbers + ") = " + StreamTasks.squaresOfUnique(numbers))));

        Button sumOfEven = new Button("Sum of even (as array)");
        sumOfEven.setOnAction(e -> withNumbers(numbers -> {
            int[] array = numbers.stream().mapToInt(Integer::intValue).toArray();
            log("sumOfEven(" + Arrays.toString(array) + ") = "
                    + StreamTasks.sumOfEven(array));
        }));

        Button upperCased = new Button("Upper-cased with prefix");
        upperCased.setOnAction(e -> withWords(words ->
                log("upperCasedWithPrefix(" + words + ") = "
                        + StreamTasks.upperCasedWithPrefix(words))));

        Button byFirstChar = new Button("Group by first character");
        byFirstChar.setOnAction(e -> withWords(words ->
                log("byFirstCharacter(" + words + ") = "
                        + StreamTasks.byFirstCharacter(words))));

        Button last = new Button("Last element");
        last.setOnAction(e -> withWords(words ->
                log("lastElement(" + words + ") = " + StreamTasks.lastElement(words))));

        Button clear = new Button("Clear output");
        clear.setOnAction(e -> output.clear());

        numbersField.setPrefColumnCount(28);
        wordsField.setPrefColumnCount(28);

        output.setEditable(false);
        output.setWrapText(true);
        VBox.setVgrow(output, Priority.ALWAYS);

        getChildren().addAll(
                new HBox(8, new Label("Numbers:"), numbersField),
                new FlowPane(8, 8, average, squares, sumOfEven),
                new HBox(8, new Label("Words:"), wordsField),
                new FlowPane(8, 8, upperCased, byFirstChar, last, clear),
                output);
    }

    private void withNumbers(Consumer<List<Integer>> action) {
        try {
            action.accept(parseInts(numbersField.getText()));
        } catch (RuntimeException e) {
            log("Error: " + e.getMessage());
        }
    }

    private void withWords(Consumer<List<String>> action) {
        try {
            action.accept(parseWords(wordsField.getText()));
        } catch (RuntimeException e) {
            log("Error: " + e.getMessage());
        }
    }

    /** Blank field -> an empty list, so the "empty input" branches are reachable from the UI. */
    private static List<Integer> parseInts(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        List<Integer> numbers = new ArrayList<>();
        for (String part : text.split(",")) {
            numbers.add(Integer.parseInt(part.trim()));
        }
        return numbers;
    }

    /**
     * Blank field -> an empty list; a field like "cat,,car" is kept as-is,
     * with the empty word in the middle, so byFirstCharacter's rejection of
     * empty words is reachable from the UI too.
     */
    private static List<String> parseWords(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        List<String> words = new ArrayList<>();
        for (String part : text.split(",", -1)) {
            words.add(part.trim());
        }
        return words;
    }

    private void log(String line) {
        output.appendText(String.format(Locale.ROOT, "%s%n", line));
    }
}
