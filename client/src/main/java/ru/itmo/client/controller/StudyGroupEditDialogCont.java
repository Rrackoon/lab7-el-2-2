package ru.itmo.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ru.itmo.common.models.*;

public class StudyGroupEditDialogCont {
    @FXML
    private TextField nameField;  // Поле для ввода названия группы
    @FXML
    private TextField coordinatesField;  // Поле для ввода координат
    @FXML
    private TextField studentsCountField;  // Поле для ввода количества студентов
    @FXML
    private TextField expelledStudentsField;  // Поле для ввода количества отчисленных студентов
    @FXML
    private TextField shouldBeExpelledField;  // Поле для ввода количества студентов, которые должны быть отчислены
    @FXML
    private ComboBox<FormOfEducation> formOfEducationField;  // Поле для выбора формы образования
    @FXML
    private TextField adminNameField;  // Поле для ввода имени администратора группы
    @FXML
    private TextField passportIDField;  // Поле для ввода паспортного ID администратора
    @FXML
    private ComboBox<Color> hairColorField;  // Поле для выбора цвета волос администратора
    @FXML
    private TextField locationXField;  // Поле для ввода координаты X местоположения
    @FXML
    private TextField locationYField;  // Поле для ввода координаты Y местоположения
    @FXML
    private TextField locationNameField;  // Поле для ввода названия местоположения

    private Stage dialogStage;  // Ссылка на текущий диалог
    private StudyGroup.StudyGroupBuilder studyGroupBuilder;  // Билдер для создания или изменения объекта StudyGroup
    private StudyGroup selectedGroup;  // Выбранная группа для редактирования
    private boolean okClicked = false;  // Флаг для определения, нажата ли кнопка OK

    @FXML
    private void initialize() {
        // Инициализация выпадающих списков
        formOfEducationField.getItems().addAll(FormOfEducation.values());
        hairColorField.getItems().addAll(Color.values());
    }

    // Устанавливает текущий диалог
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    // Устанавливает билдер для группы и заполняет поля, если группа выбрана для редактирования
    public void setStudyGroupBuilder(StudyGroup.StudyGroupBuilder builder, StudyGroup selectedGroup) {
        this.studyGroupBuilder = builder;
        this.selectedGroup = selectedGroup;
        if (selectedGroup != null) {
            // Заполняем поля данными выбранной группы
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

    // Возвращает true, если нажата кнопка OK
    public boolean isOkClicked() {
        return okClicked;
    }

    // Обработчик нажатия кнопки OK
    @FXML
    private void handleOk() {
        if (isInputValid()) {
            // Установка значений для билдера
            studyGroupBuilder.name(nameField.getText());

            // Обработка координат
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

            // Установка остальных полей
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

            // Устанавливаем флаг и закрываем диалог
            okClicked = true;
            dialogStage.close();
        }
    }

    // Обработчик нажатия кнопки Cancel
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    // Проверка правильности введенных данных
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

        // Если нет ошибок, возвращаем true
        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Показываем сообщение об ошибках
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);

            alert.showAndWait();

            return false;
        }
    }

    // Метод для показа сообщения об ошибке
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(dialogStage);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
