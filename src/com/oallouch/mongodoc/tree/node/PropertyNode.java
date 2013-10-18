package com.oallouch.mongodoc.tree.node;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/*
 * A simple wrapper used to handle a property (like a key)
 * 
 * This is managed by the PropertiesNode class.
 */
public class PropertyNode extends WithValueNode {
	private static int counter;

	private StringProperty name = new SimpleStringProperty("name" + counter++);

	public PropertyNode(Object value) {
		super(value);
	}
	public PropertyNode(String name, Object value) {
		super(value);
		setName(name);
	}

    public String getName() {
        return name.get();
    }
    public void setName(String value) {
        name.set(value);
    }
	public StringProperty nameProperty() {
		return name;
	}

    @Override
    public String toString() {
        return name.get();
    }
}
