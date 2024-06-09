package ru.itmo.common.models;

import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.Collectors;

public enum Color implements Serializable {
    RED,
    BLACK,
    BLUE,
    YELLOW,
    BROWN;

    public static String names() {
        return Arrays.stream(values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }
}
