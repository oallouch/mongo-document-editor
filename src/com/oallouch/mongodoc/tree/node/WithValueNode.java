package com.oallouch.mongodoc.tree.node;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public abstract class WithValueNode extends AbstractNode {
	public static enum SpecialValue { properties, array };
	
	private ObjectProperty value = new SimpleObjectProperty();

	public WithValueNode(Object value) {
		setValue(value);
	}
	
	public boolean isValuePrimitive() {
		return !(value.get() instanceof SpecialValue);
	}
	
	public Object getValue() {
		return value.get();
	}
	public void setValue(Object value) {
		this.value.set(value);
	}
	public ObjectProperty valueProperty() {
		return value;
	}
}