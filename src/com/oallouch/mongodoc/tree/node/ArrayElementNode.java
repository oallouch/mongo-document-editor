package com.oallouch.mongodoc.tree.node;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class ArrayElementNode extends WithValueNode {
	private IntegerProperty index = new SimpleIntegerProperty();

	public ArrayElementNode(Object value) {
		super(value);
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
