package ru.itmo.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import ru.itmo.client.main.MainApp;
import ru.itmo.client.main.Runner;

import java.util.Locale;
import java.util.ResourceBundle;

public class RootLayoutController {
    private MainApp mainApp;
    private Runner runner;
    private ResourceBundle bundle;

    @FXML
    private MenuItem menuItemSerbian;
    @FXML
    private MenuItem menuItemRussian;
    @FXML
    private MenuItem menuItemSpanish;
    @FXML
    private MenuItem menuItemCroatian;

    public void setMainApp(MainApp mainApp, ResourceBundle bundle) {
        this.mainApp = mainApp;
        this.bundle = bundle;
    }

    @FXML
    private void initialize() {
        menuItemCroatian.setOnAction(event -> mainApp.changeLocale(new Locale("hr")));
        menuItemRussian.setOnAction(event -> mainApp.changeLocale(new Locale("ru")));
        menuItemSerbian.setOnAction(event -> mainApp.changeLocale(new Locale("sr")));
        menuItemSpanish.setOnAction(event -> mainApp.changeLocale(new Locale("es")));
    }

    public void setRunner(Runner runner) {
        this.runner = runner;
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }

    @FXML
    private void handleAbout() {
        if (runner.getCurrentUsername() == null) {
            MainApp.showAlert(bundle.getString("user.not.registered.title"), null, bundle.getString("user.not.registered.message"));
            return;
        }

        Object[] infoData = runner.getInfo();
        String collectionType = (String) infoData[0];
        int collectionSize = (int) infoData[1];
        String lastSaveTime = (String) infoData[2];

        String infoMessage = String.format(bundle.getString("collection.info.message"),
                collectionType, collectionSize, lastSaveTime);

        MainApp.showAlert(bundle.getString("collection.info.title"), null, infoMessage);
    }
}
