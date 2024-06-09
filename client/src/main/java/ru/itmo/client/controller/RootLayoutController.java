package ru.itmo.client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import ru.itmo.client.main.MainApp;
import ru.itmo.client.main.Runner;

import java.util.ResourceBundle;

public class RootLayoutController {
    private MainApp mainApp;
    private Runner runner;
    private ResourceBundle bundle;

    public void setMainApp(MainApp mainApp, ResourceBundle bundle) {
        this.mainApp = mainApp;
        this.bundle = bundle;
    }

    public void setRunner(Runner runner) {
        this.runner = runner;
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }


    public void handleAbout(ActionEvent actionEvent) {
    }
}