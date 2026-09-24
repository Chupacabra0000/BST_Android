package com.example.kitpo_l1;


import java.util.ArrayList;
import java.util.List;

public class UserFactory {
    private final List<UserType> types = new ArrayList<>();

    public UserFactory() {
        // порядок регистрации — любой. Добавляем основные типы + тестовый Int
        types.add(new PointType());
        types.add(new DateType());
        types.add(new IntType());
    }

    /**
     * Список имен типов для отображения в ComboBox.
     */
    public List<String> getTypeNameList() {
        List<String> names = new ArrayList<>();
        for (UserType t : types) names.add(t.typeName());
        return names;
    }

    /**
     * Получить прототип/билдер по имени.
     */
    public UserType getBuilderByName(String name) {
        if (name == null) return null;
        for (UserType t : types) {
            if (name.equals(t.typeName())) return t;
        }
        return null;
    }
}
