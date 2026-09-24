package com.example.kitpo_l1;

public class Node {
    public static int createdCount = 0;
    public Object value;
    public Node left;
    public Node right;
    public Node parent;
    public int size; // количество узлов в поддереве, включая этот

    public Node(Object value) {
        this.value = value;
        this.left = null;
        this.right = null;
        this.parent = null;
        this.size = 1;
        createdCount++;
    }
    public void recalc() {
        int s = 1;
        if (left != null) s += left.size;
        if (right != null) s += right.size;
        this.size = s;
    }

    @Override
    public String toString() {
        return value == null ? "null" : value.toString();
    }
}
