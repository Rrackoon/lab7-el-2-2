package ru.itmo.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ru.itmo.common.models.*;

public class StudyGroupEditDialogCont {
    @FXML
    private TextField nameField;
    @FXML
    private TextField coordinatesField;
    @FXML
    private TextField studentsCountField;
    @FXML
    private TextField expelledStudentsField;
    @FXML
    private TextField shouldBeExpelledField;
    @FXML
    private ComboBox<FormOfEducation> formOfEducationField;
    @FXML
    private TextField adminNameField;
    @FXML
    private TextField passportIDField;
    @FXML
    private ComboBox<Color> hairColorField;
    @FXML
    private TextField locationXField;
    @FXML
    private TextField locationYField;
    @FXML
    private TextField locationNameField;
    private Stage dialogStage;
    private StudyGroup.StudyGroupBuilder studyGroupBuilder;
    private StudyGroup selectedGroup;
    private boolean okClicked = false;

    @FXML
    private void initialize() {
        formOfEducationField.getItems().addAll(FormOfEducation.values());
        hairColorField.getItems().addAll(Color.values());
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setStudyGroupBuilder(StudyGroup.StudyGroupBuilder builder, StudyGroup selectedGroup) {
        this.studyGroupBuilder = builder;
        this.selectedGroup = selectedGroup;
        if (selectedGroup != null) {
            nameField.setText(selectedGroup.getName());
            coordinatesField.setText(selectedGroup.getCoordinates().getX() + ";" + selectedGroup.getCoordinates().getY());
            studentsCountField.setText("" + selectedGroup.getStudentsCount());
            expelledStudentsField.setText("" + selectedGroup.getExpelledStudents());
            shouldBeExpelledField.setText("" + selectedGroup.getShouldBeExpelled());
            formOfEducationField.setValue(selectedGroup.getFormOfEducation());
            adminNameField.setText(selectedGroup.getGroupAdmin().getName());
            passportIDField.setText(selectedGroup.getGroupAdmin().getPassportID());
            hairColorField.setValue(selectedGroup.getGroupAdmin().getHairColor());
            locationXField.setText("" + selectedGroup.getGroupAdmin().getLocation().getX());
            locationYField.setText("" + selectedGroup.getGroupAdmin().getLocation().getY());
            locationNameField.setText(selectedGroup.getGroupAdmin().getLocation().getName());
        }
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            studyGroupBuilder.name(nameField.getText());

            String[] coordinates = coordinatesField.getText().split(";");
            if (coordinates.length == 2) {
                try {
                    int x = Integer.parseInt(coordinates[0]);
                    long y = Long.parseLong(coordinates[1]);
                    studyGroupBuilder.coordinates(new Coordinates(x, y));
                } catch (NumberFormatException e) {
                    showError("Invalid coordinates format. Please use 'x;y' format.");
                    return;
                }
            } else {
                showError("Invalid coordinates format. Please use 'x;y' format.");
                return;
            }

            studyGroupBuilder.studentsCount(Integer.parseInt(studentsCountField.getText()));
            studyGroupBuilder.expelledStudents(Integer.parseInt(expelledStudentsField.getText()));
            studyGroupBuilder.shouldBeExpelled(Integer.parseInt(shouldBeExpelledField.getText()));
            studyGroupBuilder.formOfEducation(formOfEducationField.getValue());
            studyGroupBuilder.groupAdmin(
                    Person.builder()
                            .name(adminNameField.getText())
                            .passportID(passportIDField.getText())
                            .hairColor(hairColorField.getValue())
                            .location(Location.builder().name(locationNameField.getText())
                                    .x(Integer.parseInt(locationXField.getText()))
                                    .y(Integer.parseInt(locationYField.getText()))
                                    .build())
                            .build()
            );

            okClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (nameField.getText() == null || nameField.getText().length() == 0) {
            errorMessage += "No valid name!\n";
        }
        if (coordinatesField.getText() == null || coordinatesField.getText().length() == 0) {
            errorMessage += "No valid coordinates!\n";
        } else {
            String[] coordinates = coordinatesField.getText().split(";");
            if (coordinates.length != 2) {
                errorMessage += "Invalid coordinates format. Please use 'x;y' format.\n";
            } else {
                try {
                    Integer.parseInt(coordinates[0]);
                    Long.parseLong(coordinates[1]);
                } catch (NumberFormatException e) {
                    errorMessage += "Invalid coordinates values. Coordinates should be numbers.\n";
                }
            }
        }
        if (studentsCountField.getText() == null || studentsCountField.getText().length() == 0) {
            errorMessage += "No valid students count!\n";
        } else {
            try {
                Integer.parseInt(studentsCountField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "Invalid students count value. It should be an integer.\n";
            }
        }
        if (expelledStudentsField.getText() == null || expelledStudentsField.getText().length() == 0) {
            errorMessage += "No valid expelled students count!\n";
        } else {
            try {
                Integer.parseInt(expelledStudentsField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "Invalid expelled students count value. It should be an integer.\n";
            }
        }
        if (shouldBeExpelledField.getText() == null || shouldBeExpelledField.getText().length() == 0) {
            errorMessage += "No valid should be expelled count!\n";
        } else {
            try {
                Integer.parseInt(shouldBeExpelledField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "Invalid should be expelled count value. It should be an integer.\n";
            }
        }
        if (formOfEducationField.getValue() == null) {
            errorMessage += "No valid form of education!\n";
        }
        if (adminNameField.getText() == null || adminNameField.getText().length() == 0) {
            errorMessage += "No valid admin name!\n";
        }
        if (passportIDField.getText() == null || passportIDField.getText().length() == 0) {
            errorMessage += "No valid passport ID!\n";
        }
        if (hairColorField.getValue() == null) {
            errorMessage += "No valid hair color!\n";
        }
        if (locationNameField.getText() == null || locationNameField.getText().length() == 0) {
            errorMessage += "No valid location name!\n";
        }
        if (locationXField.getText() == null || locationXField.getText().length() == 0) {
            errorMessage += "No valid location X coordinate!\n";
        } else {
            try {
                Integer.parseInt(locationXField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "Invalid location X coordinate value. It should be an integer.\n";
            }
        }
        if (locationYField.getText() == null || locationYField.getText().length() == 0) {
            errorMessage += "No valid location Y coordinate!\n";
        } else {
            try {
                Integer.parseInt(locationYField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "Invalid location Y coordinate value. It should be a float.\n";
            }
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);

            alert.showAndWait();

            return false;
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(dialogStage);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
