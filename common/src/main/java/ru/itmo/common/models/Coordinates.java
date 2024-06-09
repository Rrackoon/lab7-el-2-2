package ru.itmo.common.models;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@RequiredArgsConstructor
@ToString
public class Coordinates implements Serializable {
    @NonNull
    private int x; // Значение поля должно быть больше -151, Поле не может быть null
    @NonNull
    private long y; // Значение поля должно быть больше -436

    public static boolean validateX(int x) {
        return x > -151;
    }

    public static boolean validateY(long y) {
        return y > -436;
    }

    public boolean validate() {
        return validateX(this.x) && validateY(this.y);
    }

    public Coordinates update(Coordinates coordinates) {
        this.x = coordinates.getX();
        this.y = coordinates.getY();
        return this;
    }

    @Override
    public String toString() {
        return String.format("---- X: %d\n---- Y: %d", x, y);
    }
}
