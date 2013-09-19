package com.oallouch.mongodoc.node;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/*
 * A simple wrapper used to handle a property (like a key)
 * 
 * This is managed by the PropertiesNode class.
 */
public class PropertyNode extends WithSingleChildNode {
	private static int counter;

	private StringProperty name = new SimpleStringProperty("name" + counter++);

	public PropertyNode() {
	}

	public PropertyNode(String name) {
		setName(name);
	}

    /*
     * Generate an error because these DBO are build by the parent of the property
     */
    @Override
    public Object getJsonElement() {
        throw new IllegalArgumentException("getDBO can't be called directly. The DBO is built by PropertiesNode");
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
