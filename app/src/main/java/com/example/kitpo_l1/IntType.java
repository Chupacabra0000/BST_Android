package com.example.kitpo_l1;


public class IntType implements UserType {
    @Override
    public String typeName() {
        return "Integer";
    }

    @Override
    public Object create() {
        return Integer.valueOf(0);
    }

    @Override
    public Object clone(Object obj) {
        if (!(obj instanceof Integer)) throw new IllegalArgumentException("Expected Integer");
        return Integer.valueOf((Integer) obj);
    }

    @Override
    public Object parseValue(String s) {
        if (s == null) throw new IllegalArgumentException("Empty input");
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid integer: " + ex.getMessage(), ex);
        }
    }

    @Override
    public Comparator getTypeComparator() {
        return (o1, o2) -> Integer.compare(((Integer) o1), ((Integer) o2));
    }
}
