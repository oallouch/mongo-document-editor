package com.oallouch.mongodoc.node;

import java.util.List;
import java.util.Map;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public abstract class WithValueNode extends AbstractNode {
	public static enum SpecialValue { properties, array };
	
	private ObjectProperty value = new SimpleObjectProperty();

	public WithValueNode(Object value) {
		setValue(value);
	}
	
	public boolean isValueProperties() {
		return value.get() == SpecialValue.properties;
	}
	public boolean isValueArray() {
		return value.get() == SpecialValue.array;
	}
	public boolean isValuePrimitive() {
		return !(value.get() instanceof SpecialValue);
	}
	
	public Object getValue() {
		return value.get();
	}
	public void setValue(Object valueArg) {
		Object valueOrEnum;
		if (valueArg instanceof Map) {
			valueOrEnum = SpecialValue.properties;
		} else if (valueArg instanceof List) {
			valueOrEnum = SpecialValue.array;
		} else { // primitive type
			valueOrEnum = valueArg;
		}
		this.value.set(valueOrEnum);
	}
}