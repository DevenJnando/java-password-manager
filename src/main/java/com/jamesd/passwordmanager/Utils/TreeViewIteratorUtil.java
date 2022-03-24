package com.jamesd.passwordmanager.Utils;

import javafx.scene.control.TreeItem;

import java.util.Iterator;
import java.util.Stack;

public class TreeViewIteratorUtil<T> implements Iterator<TreeItem<T>> {

    private final Stack<TreeItem<T>> treeStack = new Stack<>();

    public TreeViewIteratorUtil(TreeItem<T> rootNode) {
        treeStack.push(rootNode);
    }

    @Override
    public boolean hasNext() {
        return !treeStack.isEmpty();
    }

    @Override
    public TreeItem<T> next() {
        TreeItem<T> node = treeStack.pop();
        node.getChildren().forEach(treeStack::push);
        return node;
    }
}
