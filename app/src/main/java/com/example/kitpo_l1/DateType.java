package com.example.kitpo_l1;


import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class DateType implements UserType {

    @Override
    public String typeName() {
        return "Date";
    }

    @Override
    public Object create() {
        return LocalDate.now();
    }

    @Override
    public Object clone(Object obj) {
        if (!(obj instanceof LocalDate)) throw new IllegalArgumentException("Expected LocalDate");
        return LocalDate.parse(obj.toString());
    }

    /**
     * Формат ввода: ISO yyyy-MM-dd (например, 2025-10-10)
     */
    @Override
    public Object parseValue(String s) {
        if (s == null) throw new IllegalArgumentException("Empty input");
        String trimmed = s.trim();
        try {
            return LocalDate.parse(trimmed);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Invalid date format (expect YYYY-MM-DD): " + ex.getMessage(), ex);
        }
    }

    @Override
    public Comparator getTypeComparator() {
        return (o1, o2) -> ((LocalDate) o1).compareTo((LocalDate) o2);
    }
}
