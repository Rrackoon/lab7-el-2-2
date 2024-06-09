package ru.itmo.common.models;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@RequiredArgsConstructor
public class Person implements Serializable, Comparable<Person> {
    @Serial
    private static final long serialVersionUID = 1L;
    @NonNull
    private String name; // Поле не может быть null, Строка не может быть пустой
    @NonNull
    private String passportID; // Значение этого поля должно быть уникальным, Поле не может быть null
    private Color hairColor; // Поле может быть null
    private Location location; // Поле может быть null

    public Person(@NonNull String name, @NonNull String passportID, Color hairColor, Location location) {
        this.name = name;
        this.passportID = passportID;
        this.hairColor = hairColor;
        this.location = location;
    }

    public static boolean validateName(String name) {
        return name != null && !name.isEmpty();
    }

    public static boolean validatePID(String passportID) {
        return passportID != null && !passportID.isEmpty();
    }

    public static boolean validateColor(Color hairColor) {
        return hairColor != null;
    }

    public static boolean validateLocation(Location location) {
        return location != null;
    }

    public boolean validate() {
        return validateName(this.name) && validatePID(this.passportID) &&
                (this.hairColor == null || validateColor(this.hairColor));
    }

    @Override
    public int compareTo(Person person) {
        return this.name.compareTo(person.getName());
    }
}
