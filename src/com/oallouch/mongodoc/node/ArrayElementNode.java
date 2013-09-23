package com.oallouch.mongodoc.node;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/*
 * This is managed by the ArrayNode class.
 */
public class ArrayItemNode extends WithSingleChildNode {
	private IntegerProperty index = new SimpleIntegerProperty();

	public ArrayItemNode() {
	}

	public ArrayItemNode(int indexValue) {
		setIndex(indexValue);
	}

    /*
     * Generate an error because these DBO are build by the parent of the property
     */
    @Override
    public Object getJsonElement() {
        throw new IllegalArgumentException("getDBO can't be called directly. The DBO is built by PropertiesNode");
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
