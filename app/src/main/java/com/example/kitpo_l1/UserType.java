package com.example.kitpo_l1;

public interface UserType {
    /**
     * Читаемое имя типа (используется в ComboBox и фабрике).
     */
    String typeName();

    /**
     * Создать пустой/дефолтный объект данного типа.
     */
    Object create();

    /**
     * Клонировать объект (возвращает новый объект, эквивалентный переданному).
     */
    Object clone(Object obj);

    /**
     * Разобрать значение из строки (например, из текстового поля).
     * Должно бросать IllegalArgumentException при неверном формате.
     */
    Object parseValue(String s);

    /**
     * Вернуть Comparator для сравнения объектов этого типа.
     */
    Comparator getTypeComparator();
}
