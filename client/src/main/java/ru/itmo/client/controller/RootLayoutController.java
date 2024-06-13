package ru.itmo.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import ru.itmo.client.main.MainApp;
import ru.itmo.client.main.Runner;

import java.util.Locale;
import java.util.ResourceBundle;

public class RootLayoutController {
    private MainApp mainApp;  // Главный класс приложения для управления сценами и другими аспектами
    private Runner runner;  // Класс для взаимодействия с сервером
    private ResourceBundle bundle;  // Ресурсный бандл для локализации

    @FXML
    private MenuItem menuItemSerbian;  // Пункт меню для смены языка на сербский
    @FXML
    private MenuItem menuItemRussian;  // Пункт меню для смены языка на русский
    @FXML
    private MenuItem menuItemSpanish;  // Пункт меню для смены языка на испанский
    @FXML
    private MenuItem menuItemCroatian;  // Пункт меню для смены языка на хорватский

    // Метод для установки главного приложения и ресурсного бандла
    public void setMainApp(MainApp mainApp, ResourceBundle bundle) {
        this.mainApp = mainApp;
        this.bundle = bundle;
    }

    // Инициализация контроллера
    @FXML
    private void initialize() {
        // Назначение обработчиков для смены локали при выборе пунктов меню
        menuItemCroatian.setOnAction(event -> mainApp.changeLocale(new Locale("hr")));
        menuItemRussian.setOnAction(event -> mainApp.changeLocale(new Locale("ru")));
        menuItemSerbian.setOnAction(event -> mainApp.changeLocale(new Locale("sr")));
        menuItemSpanish.setOnAction(event -> mainApp.changeLocale(new Locale("es")));
    }

    // Метод для установки объекта Runner
    public void setRunner(Runner runner) {
        this.runner = runner;
    }

    // Метод для выхода из приложения
    @FXML
    private void handleExit() {
        System.exit(0);
    }

    // Метод для отображения информации "О программе"
    @FXML
    private void handleAbout() {
        // Проверка, зарегистрирован ли пользователь
        if (runner.getCurrentUsername() == null) {
            MainApp.showAlert(bundle.getString("user.not.registered.title"), null, bundle.getString("user.not.registered.message"));
            return;
        }

        // Получение информации о коллекции с сервера
        Object[] infoData = runner.getInfo();
        String collectionType = (String) infoData[0];
        int collectionSize = (int) infoData[1];
        String lastSaveTime = (String) infoData[2];

        // Формирование сообщения с информацией о коллекции
        String infoMessage = String.format(bundle.getString("collection.info.message"),
                collectionType, collectionSize, lastSaveTime);

        // Отображение сообщения
        MainApp.showAlert(bundle.getString("collection.info.title"), null, infoMessage);
    }
}
