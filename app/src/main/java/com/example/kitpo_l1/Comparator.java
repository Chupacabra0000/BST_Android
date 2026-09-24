package com.example.kitpo_l1;


@FunctionalInterface
public interface Comparator {
    /**
     * Сравнить два объекта.
     * Возвращает отрицательное, если o1 < o2, 0 если равны, положительное если o1 > o2.
     */
    int compare(Object o1, Object o2);
}
