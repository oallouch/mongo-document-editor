package com.oallouch.mongodoc.tree.node;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public class ArrayElementNode extends WithValueNode {
	private IntegerProperty index = new SimpleIntegerProperty();

	public ArrayElementNode(Object value) {
		super(value);
	}
	
	public void setIndexFromPrecedingSibling() {
		ArrayElementNode precedingSibling = (ArrayElementNode) getPrecedingNonEndNodeSibling();
		setIndex(precedingSibling == null ? 0 : precedingSibling.getIndex() + 1);
	}

    public int getIndex() {
        return index.get();
    }
    public void setIndex(int value) {
        index.set(value);
    }
	public IntegerProperty indexProperty() {
		return index;
	}

    @Override
    public String toString() {
        return "" + index.get();
    }
}
