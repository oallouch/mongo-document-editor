package com.oallouch.mongodoc.node;

import java.util.Collections;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/*
 * A simple wrapper used to handle a property (like a key)
 * 
 * This is managed by the PropertiesNode class.
 */
public class EqualsValueNode extends AbstractNode {
	private static int counter;
	private ObjectProperty value = new SimpleObjectProperty();

    public EqualsValueNode() {
		this("value" + (counter++));
    }

	public EqualsValueNode(Object value) {
		setValue(value);
	}

    /*
     * Generate an error because these DBO are build by the parent of the property
     */
    @Override
    public Object getDBOValue() {
		return value;
    }

	public Object getValue() {
		return value.get();
	}
	public void setValue(Object val) {
		value.set(val);
	}
	public ObjectProperty valueProperty() {
		return value;
	}

	@Override
	public List<Class<? extends AbstractNode>> getAcceptedChildrenTypes() {
		return Collections.emptyList();
	}

	public String getEditingString() {
		Object value = getValue();
		if (value instanceof String) {
			return "\"" + value + "\"";
		} else if (value instanceof Number) {
			return value.toString();
		} else if (value instanceof Boolean) {
			return ((Boolean) value).toString();
		} else {
			return null;
		}
	}

	public void setEditingString(String str) {
		if (str.length() >= 2 && str.startsWith("\"") && str.endsWith("\"")) {
			setValue(str.substring(1, str.length() - 1));
		} else if ("true".equals(str)) {
			setValue(Boolean.TRUE);
		} else if ("true".equals(str)) {
			setValue(Boolean.FALSE);
		} else {
			try {
				Double.parseDouble(str);
			} catch (Throwable e) {
				setValue(str.toString());
			}
		}
	}

    @Override
    public String toString() {
        return value.toString();
    }
}
