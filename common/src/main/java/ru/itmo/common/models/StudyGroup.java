package ru.itmo.common.models;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@Setter
@Getter
public class StudyGroup implements Serializable, Comparable<StudyGroup> {
    @Serial
    private static final long serialVersionUID = 1L;
    private static int ID = 0; // Присваивание максимального ID

    private long id; // Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    @NonNull
    private String name; // Поле не может быть null, Строка не может быть пустой
    @NonNull
    private Coordinates coordinates; // Поле не может быть null
    @Builder.Default
    @NonNull
    private LocalDateTime creationDate = LocalDateTime.now(); // Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private int studentsCount; // Значение поля должно быть больше 0
    private int expelledStudents; // Значение поля должно быть больше 0
    private int shouldBeExpelled; // Значение поля должно быть больше 0, Поле может быть null
    @NonNull
    private FormOfEducation formOfEducation; // Поле не может быть null
    @NonNull
    private Person groupAdmin; // Поле не может быть null
    private String login;

    public static void setID(int newID) {
        ID = newID;
    }

    public static boolean validateName(String name) {
        return name != null && !name.isEmpty();
    }

    public static boolean validateStudentsCount(int studentsCount) {
        return studentsCount > 0;
    }

    public static boolean validateExpelledStudents(int expelledStudents) {
        return expelledStudents > 0;
    }

    public static boolean validateShouldBeExpelled(int shouldBeExpelled) {
        return shouldBeExpelled > 0;
    }

    public static boolean validateFormOfEducation(FormOfEducation formOfEducation) {
        return formOfEducation != null;
    }

    public boolean validate() {
        return  validateName(name) && coordinates != null && coordinates.validate() && creationDate != null &&
                validateStudentsCount(studentsCount) && validateExpelledStudents(expelledStudents) &&
                (shouldBeExpelled == 0 || validateShouldBeExpelled(shouldBeExpelled)) &&
                validateFormOfEducation(formOfEducation) && groupAdmin != null && groupAdmin.validate();
    }

    public void update(StudyGroup studyGroup) {
        this.name = studyGroup.getName();
        this.coordinates = studyGroup.getCoordinates();
        this.studentsCount = studyGroup.getStudentsCount();
        this.expelledStudents = studyGroup.getExpelledStudents();
        this.shouldBeExpelled = studyGroup.getShouldBeExpelled();
        this.formOfEducation = studyGroup.getFormOfEducation();
        this.groupAdmin = studyGroup.getGroupAdmin();
    }

    public void setId(long id) { // Добавлен метод setId
        this.id = id;
    }

    public static int getId() {
        return ID;
    }

    @Override
    public int compareTo(StudyGroup other) {
        return Integer.compare(this.studentsCount, other.getStudentsCount());
    }
}
