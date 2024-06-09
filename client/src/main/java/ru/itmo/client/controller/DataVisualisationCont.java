package ru.itmo.client.controller;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import ru.itmo.client.main.Main;
import ru.itmo.client.main.Runner;

public class DataVisualisationCont {
    private Main mainApp;
    private Runner runner;

    @FXML
    private Canvas canvas;

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    public void setRunner(Runner runner) {
        this.runner = runner;
    }

    @FXML
    private void initialize() {
        // Реализуйте логику визуализации данных
    }
}
