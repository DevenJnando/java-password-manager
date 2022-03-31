package com.jamesd.passwordmanager.Utils;

import javafx.scene.control.TreeItem;

import java.util.Iterator;
import java.util.Stack;

/**
 * Utility class which takes a TreeItem of objects. Iterates through each object in the TreeItem root node.
 * @param <T>
 */
public class TreeViewIteratorUtil<T> implements Iterator<TreeItem<T>> {

    private final Stack<TreeItem<T>> treeStack = new Stack<>();

    /**
     * Default constructor which takes a TreeItem root node as a parameter and pushes it to the stack
     * @param rootNode Root node of the TreeView
     */
    public TreeViewIteratorUtil(TreeItem<T> rootNode) {
        treeStack.push(rootNode);
    }

    /**
     * Checks if there are any objects left in the stack
     * @return Boolean true if more objects are in the stack, else false
     */
    @Override
    public boolean hasNext() {
        return !treeStack.isEmpty();
    }

    /**
     * Pops the next node off the stack and pushes each one of the node's children onto the stack, if it has any
     * @return The next node to be returned from the stack
     */
    @Override
    public TreeItem<T> next() {
        TreeItem<T> node = treeStack.pop();
        node.getChildren().forEach(treeStack::push);
        return node;
    }
}
