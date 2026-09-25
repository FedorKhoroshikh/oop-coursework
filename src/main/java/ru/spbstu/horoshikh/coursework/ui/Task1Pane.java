package ru.spbstu.horoshikh.coursework.ui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import ru.spbstu.horoshikh.coursework.task1.Hero;
import ru.spbstu.horoshikh.coursework.task1.Point;
import ru.spbstu.horoshikh.coursework.task1.movement.Flying;
import ru.spbstu.horoshikh.coursework.task1.movement.HorseRiding;
import ru.spbstu.horoshikh.coursework.task1.movement.MovementStrategy;
import ru.spbstu.horoshikh.coursework.task1.movement.Walking;

/**
 * Task 1 (Strategy pattern): a hero moves between points, and the way of
 * moving can be swapped at runtime, exactly like in the lab's console demo —
 * only {@link Hero} and the strategies are reused here, the whole rest is
 * a GUI shell around them.
 * <p>
 * Movement is shown two ways at once: as a text log (the log line from the
 * lab, unchanged) and as a drawing on a canvas — click anywhere on the
 * field to send the hero there, or type exact coordinates. Every trip is
 * drawn as a coloured segment, the colour matching the way of moving used
 * for that trip.
 */
public final class Task1Pane extends VBox {

    private static final double CANVAS_W = 460;
    private static final double CANVAS_H = 320;
    private static final double SCALE = 4.0;          // pixels per world unit
    private static final double ORIGIN_X = CANVAS_W / 2;
    private static final double ORIGIN_Y = CANVAS_H / 2;

    private record Segment(Point from, Point to, Color color, int step) {
    }

    private final Hero hero;
    private final Map<String, MovementStrategy> strategiesByName = new LinkedHashMap<>();
    private final Map<String, Color> colorByName = new LinkedHashMap<>();
    private final List<Segment> history = new ArrayList<>();
    private int stepCount;
    private final Canvas canvas = new Canvas(CANVAS_W, CANVAS_H);
    private final TextArea output = new TextArea();
    private final TextField destX = new TextField("30");
    private final TextField destY = new TextField("40");

    public Task1Pane() {
        super(10);
        setPadding(new Insets(12));

        MovementStrategy[] strategies = {new Walking(), new HorseRiding(), new Flying()};
        Color[] colors = {Color.web("#8a5a2b"), Color.web("#a03a3a"), Color.web("#2f6fb0")};
        for (int i = 0; i < strategies.length; i++) {
            strategiesByName.put(strategies[i].name(), strategies[i]);
            colorByName.put(strategies[i].name(), colors[i]);
        }
        hero = new Hero("Arkady", new Point(0, 0), strategiesByName.values().iterator().next());

        ComboBox<String> strategyBox = new ComboBox<>();
        strategyBox.getItems().addAll(strategiesByName.keySet());
        strategyBox.getSelectionModel().selectFirst();
        strategyBox.valueProperty().addListener((obs, old, selected) -> {
            hero.setMovement(strategiesByName.get(selected));
            log("Now moving " + selected + ".");
        });

        destX.setPrefColumnCount(5);
        destY.setPrefColumnCount(5);

        Button move = new Button("Move");
        move.setOnAction(e -> moveTo(readPoint()));

        Button compare = new Button("Compare all three ways");
        compare.setOnAction(e -> compareAll(strategyBox));

        Button clear = new Button("Clear output");
        clear.setOnAction(e -> {
            output.clear();
            history.clear();
            stepCount = 0;
            redrawCanvas();
        });

        FlowPane controls = new FlowPane(8, 8,
                new Label("Way of moving:"), strategyBox,
                new Label("Destination x, y:"), destX, destY,
                move, compare, clear);

        canvas.setOnMouseClicked(e -> {
            Point clicked = toWorld(e.getX(), e.getY());
            destX.setText(format(clicked.x()));
            destY.setText(format(clicked.y()));
            moveTo(clicked);
        });
        canvas.setCursor(javafx.scene.Cursor.CROSSHAIR);
        VBox canvasBox = new VBox(4,
                new Label("Click the field to send the hero there:"), bordered(canvas));

        output.setEditable(false);
        output.setWrapText(true);
        HBox.setHgrow(output, Priority.ALWAYS);
        VBox.setVgrow(output, Priority.ALWAYS);

        HBox mainRow = new HBox(12, canvasBox, legend(), output);
        VBox.setVgrow(mainRow, Priority.ALWAYS);

        log(hero.toString());
        redrawCanvas();
        getChildren().addAll(controls, mainRow);
    }

    private void moveTo(Point destination) {
        if (destination == null) {
            return;
        }
        Point from = hero.position();
        Color color = colorByName.get(hero.movement().name());
        stepCount++;
        log("Step " + stepCount + ":");
        hero.move(destination, this::log);
        history.add(new Segment(from, destination, color, stepCount));
        redrawCanvas();
    }

    /** Same idea as the lab's console demo: one route, every strategy, hero unchanged. */
    private void compareAll(ComboBox<String> strategyBox) {
        Point destination = readPoint();
        if (destination == null) {
            return;
        }
        MovementStrategy original = hero.movement();
        Point start = hero.position();

        for (MovementStrategy strategy : strategiesByName.values()) {
            hero.setMovement(strategy);
            moveTo(destination);
            moveTo(start);
        }
        hero.setMovement(original);
        log("Way of moving is restored: " + original.name() + ".");
    }

    private Point readPoint() {
        try {
            double x = Double.parseDouble(destX.getText().trim().replace(',', '.'));
            double y = Double.parseDouble(destY.getText().trim().replace(',', '.'));
            return new Point(x, y);
        } catch (NumberFormatException e) {
            log("x and y must both be numbers.");
            return null;
        }
    }

    // ---------------------------------------------------------------- canvas

    private static final double GRID_STEP = 10;   // world units between gridlines

    private void redrawCanvas() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.web("#fbfbfb"));
        gc.fillRect(0, 0, CANVAS_W, CANVAS_H);
        drawGrid(gc);

        gc.setLineWidth(2.5);
        for (Segment s : history) {
            gc.setStroke(s.color());
            double[] a = toPixel(s.from());
            double[] b = toPixel(s.to());
            gc.strokeLine(a[0], a[1], b[0], b[1]);
        }
        for (Segment s : history) {
            drawStepLabel(gc, s);
        }

        double[] h = toPixel(hero.position());
        gc.setFill(Color.web("#222222"));
        gc.fillOval(h[0] - 5, h[1] - 5, 10, 10);
    }

    /** Coordinate grid: light lines every {@link #GRID_STEP} units, with the
     * value labelled where each gridline crosses the axes — otherwise the
     * field is just an unlabelled crosshair. */
    private void drawGrid(GraphicsContext gc) {
        gc.setStroke(Color.web("#ededed"));
        gc.setLineWidth(1);
        double pxStep = GRID_STEP * SCALE;
        for (double x = ORIGIN_X % pxStep; x <= CANVAS_W; x += pxStep) {
            gc.strokeLine(x, 0, x, CANVAS_H);
        }
        for (double y = ORIGIN_Y % pxStep; y <= CANVAS_H; y += pxStep) {
            gc.strokeLine(0, y, CANVAS_W, y);
        }

        gc.setStroke(Color.web("#bbbbbb"));
        gc.strokeLine(0, ORIGIN_Y, CANVAS_W, ORIGIN_Y);
        gc.strokeLine(ORIGIN_X, 0, ORIGIN_X, CANVAS_H);

        gc.setFill(Color.web("#888888"));
        gc.setFont(javafx.scene.text.Font.font(9));
        gc.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
        double maxWx = (CANVAS_W - ORIGIN_X) / SCALE;
        double minWx = -ORIGIN_X / SCALE;
        for (double wx = 0; wx <= maxWx; wx += GRID_STEP) {
            gc.fillText(fmtInt(wx), ORIGIN_X + wx * SCALE, ORIGIN_Y + 12);
        }
        for (double wx = -GRID_STEP; wx >= minWx; wx -= GRID_STEP) {
            gc.fillText(fmtInt(wx), ORIGIN_X + wx * SCALE, ORIGIN_Y + 12);
        }
        double maxWy = ORIGIN_Y / SCALE;
        double minWy = -(CANVAS_H - ORIGIN_Y) / SCALE;
        gc.setTextAlign(javafx.scene.text.TextAlignment.RIGHT);
        for (double wy = GRID_STEP; wy <= maxWy; wy += GRID_STEP) {
            gc.fillText(fmtInt(wy), ORIGIN_X - 4, ORIGIN_Y - wy * SCALE + 3);
        }
        for (double wy = -GRID_STEP; wy >= minWy; wy -= GRID_STEP) {
            gc.fillText(fmtInt(wy), ORIGIN_X - 4, ORIGIN_Y - wy * SCALE + 3);
        }
    }

    /** A small numbered marker at the midpoint of a trip, matching the "Step N" log entry. */
    private void drawStepLabel(GraphicsContext gc, Segment s) {
        double[] a = toPixel(s.from());
        double[] b = toPixel(s.to());
        double mx = (a[0] + b[0]) / 2;
        double my = (a[1] + b[1]) / 2;
        gc.setFill(Color.web("#ffffffcc"));
        gc.fillOval(mx - 8, my - 8, 16, 16);
        gc.setStroke(s.color());
        gc.setLineWidth(1);
        gc.strokeOval(mx - 8, my - 8, 16, 16);
        gc.setFill(Color.web("#222222"));
        gc.setFont(javafx.scene.text.Font.font(10));
        gc.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
        gc.fillText(String.valueOf(s.step()), mx, my + 3);
    }

    private static String fmtInt(double v) {
        return String.valueOf(Math.round(v));
    }

    private double[] toPixel(Point p) {
        return new double[]{ORIGIN_X + p.x() * SCALE, ORIGIN_Y - p.y() * SCALE};
    }

    private Point toWorld(double px, double py) {
        return new Point((px - ORIGIN_X) / SCALE, (ORIGIN_Y - py) / SCALE);
    }

    private static String format(double v) {
        return String.format(Locale.ROOT, "%.1f", v);
    }

    private static javafx.scene.layout.StackPane bordered(Canvas c) {
        javafx.scene.layout.StackPane box = new javafx.scene.layout.StackPane(c);
        box.setBorder(new Border(new BorderStroke(Color.web("#bbbbbb"),
                BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
        return box;
    }

    private VBox legend() {
        VBox box = new VBox(4);
        box.setMinWidth(150);
        box.setPrefWidth(150);
        box.getChildren().add(new Label("Legend:"));
        for (Map.Entry<String, Color> e : colorByName.entrySet()) {
            Rectangle swatch = new Rectangle(12, 12, e.getValue());
            Label name = new Label(e.getKey());
            name.setMinWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
            box.getChildren().add(new HBox(6, swatch, name));
        }
        return box;
    }

    private void log(String line) {
        output.appendText(String.format(Locale.ROOT, "%s%n", line));
    }
}
