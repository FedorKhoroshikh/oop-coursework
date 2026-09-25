package ru.spbstu.horoshikh.coursework.app;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import ru.spbstu.horoshikh.coursework.ui.Task1Pane;
import ru.spbstu.horoshikh.coursework.ui.Task2Pane;
import ru.spbstu.horoshikh.coursework.ui.Task3Pane;
import ru.spbstu.horoshikh.coursework.ui.Task4Pane;

/**
 * Entry point. A tab per task is the "choose from a list" the assignment
 * asks for: pick a tab, enter input, run it, read the result in a
 * non-editable text area — tasks 1-4 unchanged underneath, only their
 * console-only {@code Main} classes were left behind in the labs.
 */
public final class CourseworkApp extends Application {

    @Override
    public void start(Stage stage) {
        TabPane tabs = new TabPane();
        tabs.getTabs().addAll(
                tab("Task 1 - Strategy", new Task1Pane()),
                tab("Task 2 - Reflection", new Task2Pane()),
                tab("Task 3 - Translator", new Task3Pane()),
                tab("Task 4 - Stream API", new Task4Pane()));

        stage.setTitle("OOP coursework");
        stage.setScene(new Scene(tabs, 860, 560));
        stage.show();
    }

    private static Tab tab(String title, Node content) {
        Tab tab = new Tab(title, content);
        tab.setClosable(false);
        return tab;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
