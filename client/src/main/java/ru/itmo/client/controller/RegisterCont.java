package ru.itmo.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ru.itmo.client.main.MainApp;
import ru.itmo.client.main.Runner;
import ru.itmo.common.commands.Register;
import ru.itmo.common.commands.base.CommandContext;
import ru.itmo.common.commands.base.CommandShallow;
import ru.itmo.common.commands.base.Response;
import ru.itmo.common.interfaces.Accessible;

import java.io.IOException;
import java.util.ResourceBundle;

public class RegisterCont {
    private MainApp mainApp;  // Главный класс приложения
    private ResourceBundle bundle;  // Ресурсный бандл для локализации
    private Runner runner;  // Класс для взаимодействия с сервером

    @FXML
    private TextField usernameField;  // Поле для ввода имени пользователя
    @FXML
    private PasswordField passwordField;  // Поле для ввода пароля
    @FXML
    private PasswordField confirmPasswordField;  // Поле для подтверждения пароля
    @FXML
    private Button registerButton;  // Кнопка для регистрации
    @FXML
    private TextArea messageOutput;  // Поле для вывода сообщений

    // Метод для установки объекта Runner
    public void setRunner(Runner runner) {
        this.runner = runner;
    }

    // Метод для установки главного приложения
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    // Инициализация контроллера, назначение обработчика для кнопки регистрации
    @FXML
    private void initialize() {
        registerButton.setOnAction(event -> handleRegister());
    }

    // Метод для обработки нажатия кнопки регистрации
    @FXML
    private void handleRegister() {
        System.out.println("Register button clicked");

        // Получение введенных данных
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Проверка совпадения паролей
        if (!password.equals(confirmPassword)) {
            messageOutput.appendText(bundle.getString("register.password.mismatch") + "\n");
            return;
        }

        // Проверка наличия подключения к серверу
        if (runner == null || runner.getConnector() == null) {
            System.out.println("Runner or Connector is null");
            messageOutput.appendText("Ошибка подключения к серверу.\n");
            return;
        }

        // Создание команды для регистрации
        CommandShallow shallow = new CommandShallow("register", null, username, password);

        Response response = null;
        try {
            // Отправка команды на сервер
            response = runner.sendShallow(shallow);
        } catch (IOException e) {
            System.out.println("not registered");
        }

        // Обработка ответа сервера
        if (response.isSuccess()) {
            // Если регистрация успешна, переходим на экран входа
            mainApp.showLoginScreen();
            messageOutput.appendText(bundle.getString("register.success") + ": " + response.getMessage() + "\n");
        } else {
            // Если регистрация не удалась, выводим сообщение об ошибке
            messageOutput.appendText(bundle.getString("register.failure") + ": " + response.getMessage() + "\n");
        }

        // Включаем кнопку регистрации
        registerButton.setDisable(false);
    }

    // Метод для перехода на экран входа
    @FXML
    private void handleBackToLogin() {
        mainApp.showLoginScreen();
    }

    // Метод для установки ресурсного бандла
    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }
}
