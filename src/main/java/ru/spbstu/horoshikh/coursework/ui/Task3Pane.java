package ru.spbstu.horoshikh.coursework.ui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import ru.spbstu.horoshikh.coursework.task3.Dictionary;
import ru.spbstu.horoshikh.coursework.task3.FileReadException;
import ru.spbstu.horoshikh.coursework.task3.InvalidFileFormatException;
import ru.spbstu.horoshikh.coursework.task3.Translator;

/**
 * Task 3 (translator): loads a dictionary from a file chosen through a
 * {@link FileChooser} and translates text that is either typed by hand or
 * loaded from a second file and then edited — both are asked for by the
 * task statement. Everything below the file choosers reuses {@link Dictionary}
 * and {@link Translator} from the lab unchanged.
 */
public final class Task3Pane extends VBox {

    private final TextField dictionaryPathField = new TextField();
    private final TextArea inputText = new TextArea();
    private final TextArea output = new TextArea();
    private Translator translator;

    public Task3Pane() {
        super(10);
        setPadding(new Insets(12));

        dictionaryPathField.setEditable(false);
        dictionaryPathField.setPromptText("No dictionary loaded yet");
        HBox.setHgrow(dictionaryPathField, Priority.ALWAYS);

        Button chooseDictionary = new Button("Choose dictionary file...");
        chooseDictionary.setOnAction(e -> chooseDictionary());

        Button chooseTextFile = new Button("Load text from file...");
        chooseTextFile.setOnAction(e -> loadTextFile());

        Button translate = new Button("Translate");
        translate.setOnAction(e -> translate());

        Button clear = new Button("Clear output");
        clear.setOnAction(e -> output.clear());

        inputText.setPromptText("Type text to translate, or load it from a file above.");
        inputText.setWrapText(true);
        inputText.setPrefRowCount(4);

        output.setEditable(false);
        output.setWrapText(true);
        VBox.setVgrow(output, Priority.ALWAYS);

        getChildren().addAll(
                new HBox(8, new Label("Dictionary:"), dictionaryPathField, chooseDictionary),
                new HBox(8, new Label("Text to translate:"), chooseTextFile),
                inputText,
                new HBox(8, translate, clear),
                output);
    }

    private void chooseDictionary() {
        Path path = pickFile("Choose a dictionary file");
        if (path == null) {
            return;
        }
        try {
            translator = new Translator(Dictionary.loadFrom(path));
            dictionaryPathField.setText(path.toString());
            log("Dictionary loaded from " + path);
        } catch (FileReadException | InvalidFileFormatException e) {
            translator = null;
            log("Could not load the dictionary: " + e.getMessage());
        }
    }

    private void loadTextFile() {
        Path path = pickFile("Choose a text file to translate");
        if (path == null) {
            return;
        }
        try {
            inputText.setText(Files.readString(path));
            log("Text loaded from " + path + " — feel free to edit it before translating.");
        } catch (IOException e) {
            log("Could not read the text file: " + e.getMessage());
        }
    }

    private void translate() {
        if (translator == null) {
            log("Load a dictionary first.");
            return;
        }
        log(translator.translate(inputText.getText()));
    }

    private Path pickFile(String title) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text files", "*.txt"));
        Window window = getScene() != null ? getScene().getWindow() : null;
        File file = chooser.showOpenDialog(window);
        return file == null ? null : file.toPath();
    }

    private void log(String line) {
        output.appendText(String.format(Locale.ROOT, "%s%n", line));
    }
}
