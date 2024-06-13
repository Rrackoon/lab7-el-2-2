package ru.itmo.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ru.itmo.client.main.MainApp;
import ru.itmo.client.main.Runner;
import ru.itmo.common.commands.LogIn;
import ru.itmo.common.commands.base.CommandContext;
import ru.itmo.common.commands.base.CommandShallow;
import ru.itmo.common.commands.base.Response;

import java.io.IOException;
import java.util.ResourceBundle;

public class LoginCont {
    private MainApp mainApp;  // Главный класс приложения для управления сценами и другими аспектами
    private ResourceBundle bundle;  // Ресурсный бандл для локализации
    private Runner runner;  // Класс для взаимодействия с сервером

    @FXML
    private TextField usernameField;  // Поле для ввода имени пользователя
    @FXML
    private PasswordField passwordField;  // Поле для ввода пароля
    @FXML
    private Button loginButton;  // Кнопка для входа
    @FXML
    private TextArea messageOutput;  // Поле для вывода сообщений
    @FXML
    public Button registerButton;  // Кнопка для перехода к регистрации

    // Метод для установки главного приложения
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    // Метод для установки ресурсного бандла
    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    // Метод для установки объекта Runner
    public void setRunner(Runner runner) {
        this.runner = runner;
    }

    // Инициализация контроллера, назначение обработчиков событий для кнопок
    @FXML
    private void initialize() {
        registerButton.setOnAction(event -> handleRegister());
        loginButton.setOnAction(event -> handleLogin());
    }

    // Метод для обработки нажатия кнопки регистрации
    @FXML
    private void handleRegister() {
        mainApp.showRegisterView();
    }

    // Метод для обработки нажатия кнопки входа
    @FXML
    private void handleLogin() {
        System.out.println("Login button clicked");

        // Получение введенных данных
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Проверка наличия введенных данных
        if (username.isEmpty() || password.isEmpty()) {
            messageOutput.appendText(bundle.getString("login.missing.credentials") + "\n");
            return;
        }

        // Проверка наличия подключения к серверу
        if (runner == null || runner.getConnector() == null) {
            System.out.println("Runner or Connector is null");
            messageOutput.appendText("Ошибка подключения к серверу.\n");
            return;
        }

        // Создание команды для входа
        CommandShallow shallow = new CommandShallow("login", null, username, password);

        Response response = null;
        try {
            // Отправка команды на сервер
            response = runner.sendShallow(shallow);
        } catch (IOException ignored) {
        }

        // Обработка ответа сервера
        if (response != null && response.isSuccess()) {
            // Если вход успешен, выводим сообщение об успехе и переходим на главный экран
            messageOutput.appendText(bundle.getString("login.success") + ": " + response.getMessage() + "\n");
            runner.setLogin(username);
            runner.setPassword(password);
            mainApp.showMainView();
        } else {
            // Если вход не удался, выводим сообщение об ошибке
            messageOutput.appendText(bundle.getString("login.failure") + ": " + response.getMessage() + "\n");
        }
    }
}
