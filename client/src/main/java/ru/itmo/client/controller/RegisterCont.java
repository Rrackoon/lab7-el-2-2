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
    private MainApp mainApp;
    private ResourceBundle bundle;
    private Runner runner;

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Button registerButton;
    @FXML
    private TextArea messageOutput;

    public void setRunner(Runner runner) {
        this.runner = runner;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void initialize() {
        registerButton.setOnAction(event -> handleRegister());
    }

    @FXML
    private void handleRegister() {
        System.out.println("Register button clicked");

        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (!password.equals(confirmPassword)) {
            messageOutput.appendText(bundle.getString("register.password.mismatch") + "\n");
            return;
        }

        if (runner == null || runner.getConnector() == null) {
            System.out.println("Runner or Connector is null");
            messageOutput.appendText("Ошибка подключения к серверу.\n");
            return;
        }

        CommandShallow shallow = new CommandShallow("register", null, username, password);

        Response response = null;
        try {
            response = runner.sendShallow(shallow);
        } catch (IOException e) {
            System.out.println("not registered");
        }
        if (response.isSuccess()) {

            mainApp.showLoginScreen();
            messageOutput.appendText(bundle.getString("register.success") + ": " + response.getMessage() + "\n");
        } else {
            messageOutput.appendText(bundle.getString("register.failure") + ": " + response.getMessage() + "\n");
        }

        registerButton.setDisable(false);
    }
    @FXML
    private void handleBackToLogin() {
        mainApp.showLoginScreen();
    }

    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }
}
