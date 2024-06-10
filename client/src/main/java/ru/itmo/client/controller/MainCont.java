package ru.itmo.client.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.Setter;
import org.controlsfx.control.Notifications;
import ru.itmo.client.main.MainApp;
import ru.itmo.client.main.Runner;
import ru.itmo.common.commands.base.CommandShallow;
import ru.itmo.common.commands.base.Response;
import ru.itmo.common.models.StudyGroup;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainCont {
    StudyGroup studyGroup;
    @FXML
    public Button helpButton;
    @FXML
    public Label userInfoLabel;
    @Setter
    private MainApp mainApp;
    @Setter
    private Runner runner;
    @Setter
    private ResourceBundle bundle;

    @FXML
    private TableView<StudyGroup> dataTable;
    @FXML
    private TableColumn<StudyGroup, Long> idColumn; // Замените Integer на Long
    @FXML
    private TableColumn<StudyGroup, String> nameColumn;
    @FXML
    private TableColumn<StudyGroup, String> coordinatesColumn;
    @FXML
    private TableColumn<StudyGroup, String> creationDateColumn;
    @FXML
    private TableColumn<StudyGroup, Integer> studentsCountColumn;
    @FXML
    private TableColumn<StudyGroup, Integer> expelledStudentsColumn;
    @FXML
    private TableColumn<StudyGroup, Integer> shouldBeExpelledColumn;
    @FXML
    private TableColumn<StudyGroup, String> formOfEducationColumn;
    @FXML
    private TableColumn<StudyGroup, String> personName;
    @FXML
    private TableColumn<StudyGroup, String> columnPassportID;
    @FXML
    private TableColumn<StudyGroup, String> columnHairColor;
    @FXML
    private TableColumn<StudyGroup, String> locationColumn;
    @FXML
    private TableColumn<StudyGroup, Long> userIdColumn;


    @FXML
    private TextField locationNameField;
    @FXML
    private TextField locationXField;
    @FXML
    private TextField locationYField;

    @FXML
    private TextField nameField;
    @FXML
    private TextField coordinatesField;
    @FXML
    private TextField creationDateField;
    @FXML
    private TextField studentsCountField;
    @FXML
    private TextField expelledStudentsField;
    @FXML
    private TextField shouldBeExpelledField;
    @FXML
    private TextField formOfEducationField;
    @FXML
    private TextField personNameField;
    @FXML
    private TextField passportIDField;
    @FXML
    private TextField coordinatesAdminField;
    @FXML
    private TextField hairColorField;
    @FXML
    private TextField loginField;

    @FXML
    private Button addButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button clearButton;
    @FXML
    private Button addIfMinButton;
    @FXML
    private Button sumOfPriceButton;
    @FXML
    private Button executeScriptButton;
    @FXML
    private TextArea messageOutput;

    @Setter
    private Stage primaryStage;

    private ObservableList<StudyGroup> studyGroupData = FXCollections.observableArrayList();
    private Thread fetchThread;
    private boolean fetchThreadRunning = false;


    @FXML
    private void initialize() {
        // Инициализация столбцов таблицы
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        coordinatesColumn.setCellValueFactory(new PropertyValueFactory<>("coordinates"));
        creationDateColumn.setCellValueFactory(new PropertyValueFactory<>("creationDate"));
        studentsCountColumn.setCellValueFactory(new PropertyValueFactory<>("studentsCount"));
        expelledStudentsColumn.setCellValueFactory(new PropertyValueFactory<>("expelledStudents"));
        shouldBeExpelledColumn.setCellValueFactory(new PropertyValueFactory<>("shouldBeExpelled"));
        formOfEducationColumn.setCellValueFactory(new PropertyValueFactory<>("formOfEducation"));

        personName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGroupAdmin().getName()));
        columnPassportID.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGroupAdmin().getPassportID()));
        columnHairColor.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGroupAdmin().getHairColor().toString()));
        locationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGroupAdmin().getLocation().toString()));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("login"));
        dataTable.setItems(studyGroupData);
    }

    @FXML
    private void handleAdd() {
        StudyGroup.StudyGroupBuilder builder = StudyGroup.builder();
        boolean okClicked = mainApp.showStudyGroupEditDialog(builder);

        if (okClicked) {
            builder.login(runner.getLogin());

            Task<Boolean> task = new Task<>() {
                @Override
                protected Boolean call() {
                    try {
                        StudyGroup studyGroup = builder.build();
                        CommandShallow shallow = new CommandShallow("add", null, studyGroup, runner.getLogin(), runner.getPassword());
                        Response response = runner.sendShallow(shallow);
                        if (response == null || response.getData() == null) return false;
                        studyGroup.setId(Long.parseLong(response.getData().toString()));
                        return response.isSuccess();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                }

                @Override
                protected void succeeded() {
                    Boolean added = getValue();
                    if (added) {
                        Platform.runLater(() -> {
                            StudyGroup newStudyGroup = builder.build();
                            dataTable.getItems().add(newStudyGroup);
                            dataTable.refresh();
                            dataTable.sort();

                            Notifications.create()
                                    .title("StudyGroup Added")
                                    .text("The study group was successfully added.\nAssigned id: " + newStudyGroup.getId())
                                    .hideAfter(Duration.seconds(3))
                                    .position(Pos.BOTTOM_RIGHT)
                                    .showInformation();
                        });
                    } else {
                        Platform.runLater(() -> showAlert("Error", "Failed to add study group", ""));
                    }
                }

                @Override
                protected void failed() {
                    Platform.runLater(() -> showAlert("Error", "Failed to add study group", getException().getMessage()));
                }
            };

            new Thread(task).start();
        }
    }
    public void fetchStudyGroup() {
        Task<ObservableList<StudyGroup>> task = new Task<>() {
            @Override
            protected ObservableList<StudyGroup> call() {
                List<StudyGroup> tickets = runner.fetchStudyGroups();
                if (tickets == null) {
                    return null;
                }
                return FXCollections.observableArrayList(tickets);
            }

            @Override
            protected void succeeded() {
                ObservableList<StudyGroup> tickets = getValue();
                if (tickets != null && !tickets.equals(studyGroupData)) {
                    Platform.runLater(() -> {
                        studyGroupData.setAll(tickets);
                        dataTable.setItems(studyGroupData);
                        dataTable.refresh();
                        dataTable.sort();
                    });
                }
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> showAlert("Error",
                        bundle.getString("ticket.fetch.failed"),
                        getException().getMessage()));
            }
        };

        startFectchStudyGroup(task);
    }

    private void startFectchStudyGroup(Task<ObservableList<StudyGroup>> task) {
        fetchThread = new Thread(task);
        fetchThread.setDaemon(true);
        fetchThread.start();
        fetchThreadRunning = true;

        task.setOnSucceeded(event -> fetchThreadRunning = false);
        task.setOnFailed(event -> fetchThreadRunning = false);
        task.setOnCancelled(event -> fetchThreadRunning = false);
    }

    @FXML
    private void handleUpdate() {
        StudyGroup selectedStudyGroup = dataTable.getSelectionModel().getSelectedItem();
        if (selectedStudyGroup != null) {
            StudyGroup.StudyGroupBuilder builder = selectedStudyGroup.toBuilder();

            boolean okClicked = mainApp.showStudyGroupEditDialog(builder);
            if (okClicked) {
                StudyGroup updatedStudyGroup = builder.build();
                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() {
                        runner.updateStudyGroup(updatedStudyGroup);
                        return null;
                    }

                    @Override
                    protected void succeeded() {
                        Platform.runLater(() -> {
                            showStudyGroupDetails(updatedStudyGroup);
                            dataTable.refresh();
                        });
                    }

                    @Override
                    protected void failed() {
                        Platform.runLater(() -> showAlert("Error", "Failed to update study group", getException().getMessage()));
                    }
                };

                new Thread(task).start();
            }
        } else {
            showAlert("No Selection", "No StudyGroup Selected", "Please select a study group in the table.");
        }
    }

    @FXML
    private void handleDelete() {
        int selectedIndex = dataTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            StudyGroup selectedStudyGroup = dataTable.getItems().get(selectedIndex);
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    runner.deleteStudyGroup(selectedStudyGroup);
                    return null;
                }

                @Override
                protected void succeeded() {
                    Platform.runLater(() -> {
                        dataTable.getItems().remove(selectedIndex);
                        dataTable.refresh();
                    });
                }

                @Override
                protected void failed() {
                    Platform.runLater(() -> showAlert("Error", "Failed to delete study group", getException().getMessage()));
                }
            };

            new Thread(task).start();
        } else {
            showAlert("No Selection", "No StudyGroup Selected", "Please select a study group in the table.");
        }
    }

    @FXML
    private void handleClear() {
        boolean confirmed = showConfirmationDialog("Confirmation", "Clear all StudyGroups?", "This action cannot be undone.");
        if (confirmed) {
            Task<Boolean> task = new Task<>() {
                @Override
                protected Boolean call() {
                    return runner.clearStudyGroups();
                }

                @Override
                protected void succeeded() {
                    Boolean success = getValue();
                    if (success) {
                        Platform.runLater(() -> {
                            dataTable.getItems().clear();
                            dataTable.refresh();
                        });
                    }
                }

                @Override
                protected void failed() {
                    Platform.runLater(() -> showAlert("Error", "Failed to clear study groups", getException().getMessage()));
                }
            };

            new Thread(task).start();
        }
    }

    @FXML
    private void handleAddIfMin() {
        StudyGroup.StudyGroupBuilder builder = StudyGroup.builder();

        boolean okClicked = mainApp.showStudyGroupEditDialog(builder);

        if (okClicked) {
            StudyGroup newStudyGroup = builder.build();

            Task<Boolean> task = new Task<>() {
                @Override
                protected Boolean call() {
                    return runner.addStudyGroupIfMin(newStudyGroup);
                }

                @Override
                protected void succeeded() {
                    Boolean added = getValue();
                    if (added) {
                        Platform.runLater(() -> {
                            dataTable.getItems().add(newStudyGroup);
                            dataTable.refresh();
                            dataTable.sort();

                            Notifications.create()
                                    .title("StudyGroup Added")
                                    .text("The study group was successfully added.\nAssigned id: " + newStudyGroup.getId())
                                    .hideAfter(Duration.seconds(3))
                                    .position(Pos.BOTTOM_RIGHT)
                                    .showInformation();
                        });
                    } else {
                        Platform.runLater(() -> showAlert("Error", "Failed to add study group", ""));
                    }
                }

                @Override
                protected void failed() {
                    Platform.runLater(() -> showAlert("Error", "Failed to add study group", getException().getMessage()));
                }
            };

            new Thread(task).start();
        }
    }


    @FXML
    private void handleExecuteScript() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Script File");
        File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
        if (file != null) {
            Task<Runner.ExitCode> task = new Task<>() {
                @Override
                protected Runner.ExitCode call() {
                    return runner.scriptMode(file);
                }

                @Override
                protected void succeeded() {
                    Runner.ExitCode exitCode = getValue();
                    Platform.runLater(() -> {
                        if (exitCode == Runner.ExitCode.OK) {
                            fetchStudyGroup();
                            showAlert("Script Execution", "The script was executed successfully.", "");
                        } else {
                            showAlert("Script Execution", "The script execution failed.", "");
                        }
                    });
                }

                @Override
                protected void failed() {
                    Platform.runLater(() -> showAlert("Error", "Failed to execute script", getException().getMessage()));
                }
            };

            new Thread(task).start();
        }
    }

    private void showStudyGroupDetails(StudyGroup studyGroup) {
        if (studyGroup != null) {
            Platform.runLater(() -> {
                nameField.setText(studyGroup.getName());
                coordinatesField.setText(studyGroup.getCoordinates().toString());
                creationDateField.setText(studyGroup.getCreationDate().toString());
                studentsCountField.setText(Integer.toString(studyGroup.getStudentsCount()));
                expelledStudentsField.setText(Integer.toString(studyGroup.getExpelledStudents()));
                shouldBeExpelledField.setText(Integer.toString(studyGroup.getShouldBeExpelled()));
                formOfEducationField.setText(String.valueOf(studyGroup.getFormOfEducation()));

                if (studyGroup.getGroupAdmin() != null) {
                    personNameField.setText(studyGroup.getGroupAdmin().getName());
                    passportIDField.setText(studyGroup.getGroupAdmin().getPassportID());
                    hairColorField.setText(studyGroup.getGroupAdmin().getHairColor().toString());
                } else {
                    personNameField.setText("");
                    passportIDField.setText("");
                    hairColorField.setText("");
                }
            });
        } else {
            Platform.runLater(() -> {
                nameField.setText("");
                coordinatesField.setText("");
                creationDateField.setText("");
                studentsCountField.setText("");
                expelledStudentsField.setText("");
                shouldBeExpelledField.setText("");
                formOfEducationField.setText("");
                personNameField.setText("");
                passportIDField.setText("");
                hairColorField.setText("");
                loginField.setText("");
            });
        }
    }

    @FXML
    private void handleHelp() {
        // Display help dialog or message
        String helpMessage = bundle.getString("help.general");

        Stage helpStage = new Stage();
        helpStage.initModality(Modality.APPLICATION_MODAL);
        helpStage.setTitle(bundle.getString("help.title"));

        TextArea helpTextArea = new TextArea();
        helpTextArea.setEditable(false);
        helpTextArea.setWrapText(true);
        helpTextArea.setText(helpMessage);
        Button closeButton = new Button(bundle.getString("help.close.button"));
        closeButton.setOnAction(event -> helpStage.close());

        VBox vbox = new VBox(helpTextArea, closeButton);
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10));

        Scene scene = new Scene(vbox, 400, 300);
        helpStage.setScene(scene);
        helpStage.show();
    }

    private void showHelpDialog(String helpMessage) {
        Stage helpStage = new Stage();
        helpStage.initModality(Modality.APPLICATION_MODAL);
        helpStage.setTitle(bundle.getString("help.title"));

        TextArea helpTextArea = new TextArea();
        helpTextArea.setEditable(false);
        helpTextArea.setWrapText(true);
        helpTextArea.setText(helpMessage);
        Button closeButton = new Button(bundle.getString("help.close.button"));
        closeButton.setOnAction(event -> helpStage.close());

        VBox vbox = new VBox(helpTextArea, closeButton);
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10));

        Scene scene = new Scene(vbox, 400, 300);
        helpStage.setScene(scene);
        helpStage.show();
    }

    private boolean showConfirmationDialog(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        ButtonType buttonTypeYes = new ButtonType("Yes");
        ButtonType buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == buttonTypeYes;
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    public void setUserInfo() {
        String userId = runner.getLogin();
        String username = runner.getCurrentUsername();
        Platform.runLater(() -> userInfoLabel.setText(String.format("%s %s, %s %s",
                bundle.getString("main.user.info"), username,
                bundle.getString("main.user.info.id"), userId)));
    }
}
