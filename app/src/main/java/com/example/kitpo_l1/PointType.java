package com.example.kitpo_l1;


public class PointType implements UserType {

    @Override
    public String typeName() {
        return "Point2D";
    }

    @Override
    public Object create() {
        return new Point2D(0.0, 0.0);
    }

    @Override
    public Object clone(Object obj) {
        if (!(obj instanceof Point2D)) throw new IllegalArgumentException("Expected Point2D");
        Point2D p = (Point2D) obj;
        return new Point2D(p.x, p.y);
    }

    /**
     * Разбор строки: "x,y" или "x y" или "x,y" с пробелами. x и y — double.
     */
    @Override
    public Object parseValue(String s) {
        if (s == null) throw new IllegalArgumentException("Empty input");
        String trimmed = s.trim();
        // remove surrounding parentheses if present: "(x,y)" -> "x,y"
        if (trimmed.startsWith("(") && trimmed.endsWith(")")) {
            trimmed = trimmed.substring(1, trimmed.length() - 1).trim();
        }
        // allow separators: comma and/or whitespace
        String[] parts = trimmed.split("[,\\s]+");
        if (parts.length < 2) throw new IllegalArgumentException("Point must have two numbers: \"x y\" or \"x,y\"");
        try {
            double x = Double.parseDouble(parts[0].trim());
            double y = Double.parseDouble(parts[1].trim());
            return new Point2D(x, y);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid number format for Point2D: " + ex.getMessage(), ex);
        }
    }


    @Override
    public Comparator getTypeComparator() {
        return (o1, o2) -> {
            double d1 = ((Point2D) o1).distance();
            double d2 = ((Point2D) o2).distance();
            return Double.compare(d1, d2);
        };
    }
}
