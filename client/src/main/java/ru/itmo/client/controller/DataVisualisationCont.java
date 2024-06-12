package ru.itmo.client.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import lombok.Setter;
import ru.itmo.client.main.MainApp;
import ru.itmo.client.main.Runner;
import ru.itmo.common.models.StudyGroup;

import java.util.*;

public class DataVisualisationCont {
    public AnchorPane dataVisualization;
    private MainApp mainApp;
    private Runner runner;
    private MainCont mainController;

    @FXML
    private Canvas canvas;
    private List<StudyGroup> studyGroups = new ArrayList<>();
    private Map<String, Color> userColors = new HashMap<>();
    private double offsetX = 0;
    private double offsetY = 0;
    private double startX;
    private double startY;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setRunner(Runner runner) {
        this.runner = runner;
    }

    public void setMainController(MainCont mainController) {
        this.mainController = mainController;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
        canvas.setOnMouseClicked(this::handleMouseClick);
        canvas.setOnMousePressed(this::handleMousePress);
        canvas.setOnMouseDragged(this::handleMouseDrag);
    }

    @FXML
    private void initialize() {
        studyGroups = new ArrayList<>();
        userColors = new HashMap<>();
    }

    public void addObject(StudyGroup studyGroup) {
        studyGroups.add(studyGroup);
        if (!userColors.containsKey(studyGroup.getLogin())) {
            userColors.put(studyGroup.getLogin(), getRandomColor(studyGroup.getLogin()));
        }
        drawObjects();
    }
    public void setStudyGroups(List<StudyGroup> groups){
        for(StudyGroup group : groups){
            addObject(group);
        }
    }
    public void removeObject(StudyGroup studyGroup) {
        studyGroups.remove(studyGroup);
        drawObjects();
    }

    public void updateObject(StudyGroup studyGroup) {
        for (int i = 0; i < studyGroups.size(); i++) {
            if (studyGroups.get(i).getId() == studyGroup.getId()) {
                studyGroups.set(i, studyGroup);
                break;
            }
        }
        drawObjects();
    }

    public void drawObjects() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (StudyGroup studyGroup : studyGroups) {
            animateObject(gc, studyGroup);
        }
    }

    private Color getRandomColor(String login) {
        Random random = new Random(login.hashCode());
        return Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble());
    }

    private void animateObject(GraphicsContext gc, StudyGroup studyGroup) {
        Color color = userColors.get(studyGroup.getLogin());
        gc.setFill(color);

        int x = studyGroup.getCoordinates().getX() + (int) offsetX;
        long y = studyGroup.getCoordinates().getY() + (int) offsetY;
        // int size = 12; // Example size
        int size=studyGroup.getStudentsCount()/10; // Отражает численность группы
        gc.fillOval(x, y, size, size);

    }

    private void handleMouseClick(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();

        for (StudyGroup studyGroup : studyGroups) {
            int objX = studyGroup.getCoordinates().getX() + (int) offsetX;
            long objY = studyGroup.getCoordinates().getY() + (int) offsetY;
            //int size = 10; // Example size
            int size = studyGroup.getStudentsCount()/10;
            if (x >= objX && x <= objX + size && y >= objY && y <= objY + size) {
                showObjectInfo(studyGroup);
                break;
            }
        }
    }


    private void handleMousePress(MouseEvent event) {
        startX = event.getX();
        startY = event.getY();
    }

    private void handleMouseDrag(MouseEvent event) {
        double deltaX = event.getX() - startX;
        double deltaY = event.getY() - startY;

        offsetX += deltaX;
        offsetY += deltaY;

        startX = event.getX();
        startY = event.getY();

        drawObjects();
    }

    private void showObjectInfo(StudyGroup studyGroup) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Object Information");
        alert.setHeaderText(null);
        alert.setContentText("Study Group Info:\n" + studyGroup);
        alert.showAndWait();
    }
}
