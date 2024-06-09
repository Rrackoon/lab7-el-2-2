package ru.itmo.common.models;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@RequiredArgsConstructor
@ToString
public class Location implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @NonNull
    private Integer x; // Поле не может быть null
    @NonNull
    private Integer y; // Поле не может быть null
    @NonNull
    private String name; // Поле не может быть null

    public static boolean validateX(Integer x) {
        return x != null;
    }

    public static boolean validateY(Integer y) {
        return y != null;
    }

    public static boolean validateName(String name) {
        return name != null && !name.isEmpty();
    }

    public boolean validate() {
        return validateX(this.x) && validateY(this.y) && validateName(this.name);
    }

    public Location update(Location location) {
        this.x = location.getX();
        this.y = location.getY();
        this.name = location.getName();
        return this;
    }
}
