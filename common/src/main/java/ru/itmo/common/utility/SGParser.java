package ru.itmo.common.utility;

import ru.itmo.common.interfaces.Printer;
import ru.itmo.common.models.*;
import ru.itmo.common.parser.DefaultTypeParser;

import java.util.Arrays;
import java.util.Scanner;

public class SGParser extends DefaultTypeParser {

    private boolean printValue = false;

    public SGParser(Scanner scanner, Printer printer) {
        super(scanner, printer);
    }

    public SGParser(Scanner scanner, Printer printer, boolean printValue) {
        super(scanner, printer);
        this.printValue = printValue;
    }

    public Person parsePerson() {
        print("PERSON:");
        String name;
        while (!Person.validateName(name = parseString("Name", "not null"))) {
            print("Invalid Name.");
        }
        if (printValue) {
            print(name);
        }
        String passportID;
        while (!Person.validatePID(passportID = parseString("passportID", "not null"))) {
            print("Invalid passportID");
        }
        if (printValue) {
            print(passportID);
        }
        Color hairColor;
        String colorValues = Arrays.asList(Color.values()).toString();
        while (!Person.validateColor(hairColor = parseEnum(Color.class, "Color " + colorValues, "not null"))) {
            print("Invalid Color " + colorValues);
        }
        if (printValue) {
            print(hairColor.name());
        }
        Location location = parseLocation();

        return Person.builder()
                .name(name)
                .passportID(passportID)
                .hairColor(hairColor)
                .location(location)
                .build();
    }

    public Location parseLocation() {
        print("LOCATION:");
        Integer x;
        while (!Location.validateX(x = parseInt("X", "not null"))) {
            print("Invalid X.");
        }
        if (printValue) {
            print("" + x);
        }
        Integer y;
        while (!Location.validateY(y = parseInt("Y", "not null, Integer"))) {
            print("Invalid Y.");
        }
        if (printValue) {
            print("" + y);
        }
        String name;
        while (!Location.validateName(name = parseString("Name", "not null"))) {
            print("Invalid Name.");
        }
        return Location.builder()
                .x(x)
                .y(y)
                .name(name)
                .build();
    }

    public Coordinates parseCoordinates() {
        print("COORDINATES:");
        int x;
        while (!Coordinates.validateX(x = parseInt("X", "not null, min -951"))) {
            print("Invalid X.");
        }
        if (printValue) {
            print("" + x);
        }
        long y;
        while (!Coordinates.validateY(y = parseLong("Y", "not null, max 779"))) {
            print("Invalid Y.");
        }
        return Coordinates.builder()
                .x(x)
                .y(y)
                .build();
    }

    public StudyGroup parseStudyGroup() {
        print("STUDY_GROUP:");
        String name;
        while (!StudyGroup.validateName(name = parseString("Name", "not null, not empty"))) {
            print("Invalid Name.");
        }
        if (printValue) {
            print(name);
        }
        Coordinates coordinates = parseCoordinates();
        int studentsCount;
        while (!StudyGroup.validateStudentsCount(studentsCount = parseInt("StudentsCount", "int, min 1"))) {
            print("Invalid StudentsCount");
        }
        if (printValue) {
            print("" + studentsCount);
        }
        int expelledStudents;
        while (!StudyGroup.validateExpelledStudents(expelledStudents = parseInt("ExpelledStudents", "int, min 1"))) {
            print("Invalid ExpelledStudents");
        }
        if (printValue) {
            print("" + expelledStudents);
        }
        int shouldBeExpelled;
        while (!StudyGroup.validateShouldBeExpelled(shouldBeExpelled = parseInt("ShouldBeExpelled", "long, min 1"))) {
            print("Invalid ShouldBeExpelled");
        }
        if (printValue) {
            print("" + shouldBeExpelled);
        }
        FormOfEducation formOfEducation;
        String formOfEducationValues = Arrays.asList(FormOfEducation.values()).toString();
        while (!StudyGroup.validateFormOfEducation(formOfEducation = parseEnum(FormOfEducation.class, "FormOfEducation " + formOfEducationValues, "not null"))) {
            print("Invalid FormOfEducation " + formOfEducationValues);
        }
        if (printValue) {
            print(formOfEducation.name());
        }
        Person groupAdmin = parsePerson();

        return StudyGroup.builder()
                .name(name)
                .coordinates(coordinates)
                .studentsCount(studentsCount)
                .expelledStudents(expelledStudents)
                .shouldBeExpelled(shouldBeExpelled)
                .formOfEducation(formOfEducation)
                .groupAdmin(groupAdmin)
                .build();
    }
}
