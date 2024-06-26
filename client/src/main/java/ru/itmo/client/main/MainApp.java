package ru.itmo.client.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.itmo.client.controller.*;
import ru.itmo.common.models.StudyGroup;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainApp extends Application {
    private Stage primaryStage;  // Основная сцена приложения
    private BorderPane rootLayout;  // Корневой макет приложения
    private Runner runner;  // Класс для взаимодействия с сервером
    private ResourceBundle bundle;  // Ресурсный бандл для локализации

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Study Group Manager");

        // Установка локали по умолчанию на русский
        Locale.setDefault(new Locale("ru"));
        bundle = ResourceBundle.getBundle("messages", Locale.getDefault());

        runner = new Runner();  // Инициализация объекта Runner

        initRootLayout();  // Инициализация корневого макета
        showLoginScreen();  // Отображение экрана входа
    }

    // Метод для отображения экрана входа
    public void showLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/view/LoginScreen.fxml"));
            loader.setResources(bundle);
            GridPane loginScreen = loader.load();

            rootLayout.setCenter(loginScreen);
            LoginCont controller = loader.getController();
            controller.setMainApp(this);
            controller.setRunner(runner);
            controller.setBundle(bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Метод для отображения главного экрана приложения
    public void showMainView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL resourceUrl = MainApp.class.getResource("/view/MainScreen.fxml");
            if (resourceUrl == null) {
                throw new IOException("Resource not found: /view/MainScreen.fxml");
            }
            loader.setLocation(resourceUrl);//устновка местоположения ресурса fxml, кот нужно загрузить.
            loader.setResources(bundle);//установка ресурсноого бандла для локализации
            BorderPane mainScreen = loader.load();//загружает и парсит fxml

            rootLayout.setCenter(mainScreen);

            MainCont controller = loader.getController();
            controller.setMainApp(this);
            controller.setRunner(runner);
            controller.setBundle(bundle);
            controller.setPrimaryStage(primaryStage);

            controller.fetchStudyGroup();
            controller.setUserInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Метод для инициализации корневого макета
    public void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/view/RootLayout.fxml"));
            loader.setResources(bundle);
            rootLayout = loader.load();

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();

            RootLayoutController controller = loader.getController();
            controller.setMainApp(this, bundle);
            controller.setRunner(runner);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод для отображения экрана регистрации
    public void showRegisterView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/view/RegisterScreen.fxml"));
            loader.setResources(bundle);
            BorderPane registerScreen = loader.load();
            rootLayout.setCenter(registerScreen);
            RegisterCont controller = loader.getController();
            controller.setMainApp(this);
            controller.setRunner(runner);
            controller.setBundle(bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Метод для отображения диалога редактирования StudyGroup
    public boolean showStudyGroupEditDialog(StudyGroup.StudyGroupBuilder builder, StudyGroup selectedGroup) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/view/StudyGroupEditDialog.fxml"));
            loader.setResources(bundle);
            VBox page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit StudyGroup");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            StudyGroupEditDialogCont controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setStudyGroupBuilder(builder, selectedGroup);

            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Метод для показа сообщения об ошибке или информации
    public static void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Метод для смены локали и перезагрузки сцен
    public void changeLocale(Locale locale) {
        Locale.setDefault(locale);
        bundle = ResourceBundle.getBundle("messages", Locale.getDefault());
        reloadScenes();
    }

    // Метод для перезагрузки сцен при смене локали
    private void reloadScenes() {
        initRootLayout();
        showLoginScreen();
    }

    // Возвращает основную сцену приложения
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    // Точка входа в приложение
    public static void main(String[] args) {
        launch(args);
    }
}
