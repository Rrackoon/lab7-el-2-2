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
    public AnchorPane dataVisualization;  // Основной контейнер для визуализации данных
    private MainApp mainApp;  // Главный класс приложения
    private Runner runner;  // Класс для взаимодействия с сервером
    private MainCont mainController;  // Контроллер основного окна

    @FXML
    private Canvas canvas;  // Канва для рисования объектов
    private List<StudyGroup> studyGroups = new ArrayList<>();  // Список групп для визуализации
    private Map<String, Color> userColors = new HashMap<>();  // Карта для хранения цветов пользователей
    private double offsetX = 0;  // Сдвиг по оси X для перемещения объектов
    private double offsetY = 0;  // Сдвиг по оси Y для перемещения объектов
    private double startX;  // Начальная позиция X для перемещения
    private double startY;  // Начальная позиция Y для перемещения

    // Установка главного приложения
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    // Установка объекта Runner
    public void setRunner(Runner runner) {
        this.runner = runner;
    }

    // Установка основного контроллера
    public void setMainController(MainCont mainController) {
        this.mainController = mainController;
    }

    // Установка канвы и обработчиков событий мыши
    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
        canvas.setOnMouseClicked(this::handleMouseClick);
        canvas.setOnMousePressed(this::handleMousePress);
        canvas.setOnMouseDragged(this::handleMouseDrag);
    }

    // Инициализация контроллера
    @FXML
    private void initialize() {
        studyGroups = new ArrayList<>();
        userColors = new HashMap<>();
    }

    // Добавление нового объекта StudyGroup
    public void addObject(StudyGroup studyGroup) {
        studyGroups.add(studyGroup);
        if (!userColors.containsKey(studyGroup.getLogin())) {
            userColors.put(studyGroup.getLogin(), getRandomColor(studyGroup.getLogin()));
        }
        drawObjects();
    }

    // Установка списка групп для визуализации
    public void setStudyGroups(List<StudyGroup> groups){
        for(StudyGroup group : groups){
            addObject(group);
        }
    }

    // Удаление объекта StudyGroup
    public void removeObject(StudyGroup studyGroup) {
        studyGroups.remove(studyGroup);
        drawObjects();
    }

    // Обновление объекта StudyGroup
    public void updateObject(StudyGroup studyGroup) {
        for (int i = 0; i < studyGroups.size(); i++) {
            if (studyGroups.get(i).getId() == studyGroup.getId()) {
                studyGroups.set(i, studyGroup);
                break;
            }
        }
        drawObjects();
    }

    // Отрисовка всех объектов
    public void drawObjects() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (StudyGroup studyGroup : studyGroups) {
            animateObject(gc, studyGroup);
        }
    }

    // Генерация случайного цвета для пользователя
    private Color getRandomColor(String login) {
        Random random = new Random(login.hashCode());
        return Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble());
    }

    // Анимация объекта StudyGroup
    private void animateObject(GraphicsContext gc, StudyGroup studyGroup) {
        Color color = userColors.get(studyGroup.getLogin());
        gc.setFill(color);

        int x = studyGroup.getCoordinates().getX() + (int) offsetX;
        long y = studyGroup.getCoordinates().getY() + (int) offsetY;
        int size = studyGroup.getStudentsCount() / 10;  // Размер круга зависит от количества студентов
        gc.fillOval(x, y, size, size);
    }

    // Обработчик клика мыши
    private void handleMouseClick(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();

        for (StudyGroup studyGroup : studyGroups) {
            int objX = studyGroup.getCoordinates().getX() + (int) offsetX;
            long objY = studyGroup.getCoordinates().getY() + (int) offsetY;
            int size = studyGroup.getStudentsCount() / 10;
            if (x >= objX && x <= objX + size && y >= objY && y <= objY + size) {
                showObjectInfo(studyGroup);
                break;
            }
        }
    }

    // Обработчик нажатия мыши
    private void handleMousePress(MouseEvent event) {
        startX = event.getX();
        startY = event.getY();
    }

    // Обработчик перемещения мыши
    private void handleMouseDrag(MouseEvent event) {
        double deltaX = event.getX() - startX;
        double deltaY = event.getY() - startY;

        offsetX += deltaX;
        offsetY += deltaY;

        startX = event.getX();
        startY = event.getY();

        drawObjects();
    }

    // Показ информации об объекте StudyGroup
    private void showObjectInfo(StudyGroup studyGroup) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Object Information");
        alert.setHeaderText(null);
        alert.setContentText("Study Group Info:\n" + studyGroup);
        alert.showAndWait();
    }
}
