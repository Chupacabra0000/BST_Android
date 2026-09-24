package com.example.kitpo_l1;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.*;


public class BinaryTree implements Iterable<Object> {
    private Node root;
    private final Comparator cmp;


    public BinaryTree(Comparator comparator) {
        this.cmp = comparator;
        this.root = null;
    }


    public int size() {
        return root == null ? 0 : root.size;
    }


    public void add(Object value) {
        Node z = new Node(value);
        if (root == null) {
            root = z;
            return;
        }
        Node x = root;
        Node y = null;
        while (x != null) {
            y = x;
            int c = cmp.compare(value, x.value);
            if (c < 0) x = x.left;
            else x = x.right;
        }
        z.parent = y;
        if (cmp.compare(value, y.value) < 0) y.left = z;
        else y.right = z;
        updateSizeUpwards(y);
    }


    public void insert(Object value) {
        add(value);
    }


    public Object get(int index) {
        Node n = select(index);
        return n.value;
    }


    public void remove(int index) {
        Node z = select(index);
        deleteNode(z);
    }


    public void forEach(DoWith action) {
        inorderForEach(root, action);
    }


    public List<Object> toList() {
        List<Object> list = new ArrayList<>();
        inorderToList(root, list);
        return list;
    }


    public void balance() {
        List<Object> list = toList();
        root = buildBalancedFromList(list, 0, list.size() - 1, null);
    }

//    public void balance() {
//    }



    public Node getRoot() {
        return root;
    }


    public Object firstThat(TestIt test) {
        return firstThatRec(root, test);
    }

    // ---------- сериализация/десериализация (текстовая) ----------


    public void saveTo(Writer writer) throws IOException {
        List<Object> list = toList();
        for (Object o : list) {
            writer.write(o == null ? "null" : o.toString());
            writer.write(System.lineSeparator());
        }
        writer.flush();
    }


    public static BinaryTree loadFrom(BufferedReader reader, UserType type) throws IOException {
        BinaryTree tree = new BinaryTree(type.getTypeComparator());
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) continue;
            try {
                Object val = type.parseValue(line.trim());
                tree.add(val);
            } catch (IllegalArgumentException ex) {
                // пропустить некорректную строку (или можно бросить)
                System.err.println("Warning: skip unparsable line: " + line + " -> " + ex.getMessage());
            }
        }
        return tree;
    }

    // ---------- Итератор (in-order) ----------


    @Override
    public Iterator<Object> iterator() {
        return new InOrderIterator(root);
    }

    private static class InOrderIterator implements Iterator<Object> {
        private final Deque<Node> stack = new ArrayDeque<>();


        InOrderIterator(Node root) {
            pushLeft(root);
        }


        private void pushLeft(Node n) {
            Node cur = n;
            while (cur != null) {
                stack.push(cur);
                cur = cur.left;
            }
        }


        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }


        @Override
        public Object next() {
            if (!hasNext()) throw new NoSuchElementException();
            Node n = stack.pop();
            Object val = n.value;
            if (n.right != null) pushLeft(n.right);
            return val;
        }
    }

    // ---------- Внутренние вспомогательные методы ----------


    private Object firstThatRec(Node n, TestIt test) {
        if (n == null) return null;
        Object leftRes = firstThatRec(n.left, test);
        if (leftRes != null) return leftRes;
        if (test.testIt(n.value)) return n.value;
        return firstThatRec(n.right, test);
    }


    private void inorderForEach(Node n, DoWith action) {
        if (n == null) return;
        inorderForEach(n.left, action);
        action.doWith(n.value);
        inorderForEach(n.right, action);
    }


    private void inorderToList(Node n, List<Object> out) {
        if (n == null) return;
        inorderToList(n.left, out);
        out.add(n.value);
        inorderToList(n.right, out);
    }


    private Node buildBalancedFromList(List<Object> list, int l, int r, Node parent) {
        if (l > r) return null;
        int m = (l + r) >>> 1;
        //int m = ((l + r) >>> 1) + 1;   // DEFECT: неверный индекс середины

        Node node = new Node(list.get(m));
        node.parent = parent;
        node.left = buildBalancedFromList(list, l, m - 1, node); // DEFECT: пропускаем левое поддерево
        node.right = buildBalancedFromList(list, m + 1, r, node);
        node.recalc();
        return node;
    }


    private Node select(int index) {
        int n = size();
        if (index < 0 || index >= n)
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds, size=" + n);
        Node x = root;
        int idx = index;
        while (x != null) {
            int leftSize = (x.left == null) ? 0 : x.left.size;
            if (idx == leftSize) return x;
            else if (idx < leftSize) x = x.left;
            else {
                idx = idx - (leftSize + 1);
                x = x.right;
            }
        }
        throw new IllegalStateException("Select failed for index: " + index);
    }


    private void deleteNode(Node z) {
        if (z == null) return;
        Node startUpdateFrom = null;

        if (z.left == null) {
            startUpdateFrom = (z.parent == null) ? z.right : z.parent;
            transplant(z, z.right);
        } else if (z.right == null) {
            startUpdateFrom = (z.parent == null) ? z.left : z.parent;
            transplant(z, z.left);
        } else {
            Node y = minimum(z.right);
            Node yParentBefore = y.parent;
            if (y.parent != z) {
                transplant(y, y.right);
                y.right = z.right;
                if (y.right != null) y.right.parent = y;
            }
            transplant(z, y);
            y.left = z.left;
            if (y.left != null) y.left.parent = y;
            y.recalc();
            startUpdateFrom = (yParentBefore != null && yParentBefore != y) ? yParentBefore : y.parent;
        }

        updateSizeUpwards(startUpdateFrom);
    }


    private void transplant(Node u, Node v) {
        if (u.parent == null) root = v;
        else if (u == u.parent.left) u.parent.left = v;
        else u.parent.right = v;
        if (v != null) v.parent = u.parent;
    }


    private Node minimum(Node x) {
        while (x.left != null) x = x.left;
        return x;
    }


    private void updateSizeUpwards(Node node) {
        Node cur = node;
        while (cur != null) {
            cur.recalc();
            cur = cur.parent;
        }
    }


    @Override
    public String toString() {
        List<Object> l = toList();
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < l.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(l.get(i));
        }
        sb.append("]");
        return sb.toString();
    }
}
