package com.example.kitpo_l1;


import java.util.Locale;

public class Point2D {
    public final double x;
    public final double y;

    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double distance() {
        return Math.hypot(x, y);
    }

    @Override
    public String toString() {

        return String.format(Locale.US, "(%.3f,%.3f)", x, y);
    }
}
