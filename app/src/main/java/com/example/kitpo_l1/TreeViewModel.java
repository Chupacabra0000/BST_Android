package com.example.kitpo_l1;

import androidx.lifecycle.ViewModel;

public class TreeViewModel extends ViewModel {
    public BinaryTree tree = null;
    public UserType currentType = null;
    public int selectedIndex = 0;

    public void initIfNeeded(UserType type) {
        if (tree == null) {
            currentType = type;
            tree = new BinaryTree(type.getTypeComparator());
        }
    }
}
