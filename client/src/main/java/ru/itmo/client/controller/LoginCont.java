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
    private MainApp mainApp;
    private ResourceBundle bundle;
    private Runner runner;

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private TextArea messageOutput;
    @FXML
    public Button registerButton;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    public void setRunner(Runner runner) {
        this.runner = runner;
    }

    @FXML
    private void initialize() {
        registerButton.setOnAction(event -> handleRegister());
        loginButton.setOnAction(event -> handleLogin());
    }

    @FXML
    private void handleRegister() {
        mainApp.showRegisterView();
    }

    @FXML
    private void handleLogin() {
//        mainApp.showMainView();
//        if(true) return;
        System.out.println("Login button clicked");

        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            messageOutput.appendText(bundle.getString("login.missing.credentials") + "\n");
            return;
        }

        if (runner == null || runner.getConnector() == null) {
            System.out.println("Runner or Connector is null");
            messageOutput.appendText("Ошибка подключения к серверу.\n");
            return;
        }

        CommandShallow shallow = new CommandShallow("login", null, username, password);

        Response response = null;
        try {
            response = runner.sendShallow(shallow);
        } catch (IOException polniy_ignor) {
        }

        if (response != null && response.isSuccess()) {
            messageOutput.appendText(bundle.getString("login.success") + ": " + response.getMessage() + "\n");
            runner.setLogin(username);
            runner.setPassword(password);
            mainApp.showMainView();
        } else {
            messageOutput.appendText(bundle.getString("login.failure") + ": " + response.getMessage() + "\n");
        }
    }
}
