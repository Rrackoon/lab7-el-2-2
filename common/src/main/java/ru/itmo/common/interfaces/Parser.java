package ru.itmo.common.interfaces;

public interface Parser {
    String parseString(String name, String descr);

    Integer parseInt(String name, String descr);

    Float parseFloat(String name, String descr);

    Double parseDouble(String name, String descr);

    Long parseLong(String name, String descr);

    <T extends Enum<T>> T parseEnum(Class<T> enumType, String name, String descr) throws InterruptedException;
}
