package com.oallouch.mongodoc.tree;

import com.google.common.collect.Maps;
import com.oallouch.mongodoc.node.AbstractNode;
import com.oallouch.mongodoc.node.WithValueNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableCell;
import javafx.scene.input.KeyCode;

public class TypeColumnCell extends TreeTableCell<AbstractNode, AbstractNode> {
	public static enum DataType {
		NULL("Null", null),
		STRING("String", String.class),
		DOUBLE("Double/Float", Double.class),
		LONG("Long/Integer", Integer.class),
		BOOLEAN("Boolean", Boolean.class),
		DATE("Date", Date.class),
		PATTERN("Pattern", Pattern.class);
		
		private String text;
		private Class valueClass;
		
		DataType(String text, Class valueClass) {
			this.text = text;
			this.valueClass = valueClass;
		}
		
		@Override
		public String toString() {
			return text;
		}
		
		public String getText() { return text; }
		public Class getValueClass() { return valueClass; }
	}
	
	public static final Map<Class, DataType> DATA_TYPE_BY_VALUE_CLASS = Maps.newHashMapWithExpectedSize(8);
	
	static {
		for (DataType dataType : DataType.values()) {
			Class valueClass = dataType.getValueClass();
			if (valueClass != null) {
				DATA_TYPE_BY_VALUE_CLASS.put(valueClass, dataType);
			}
		}
	}
	
	public static DataType getDataType(Object value) {
		if (value == null) {
			return DataType.NULL;
		}
		DataType dataType = DATA_TYPE_BY_VALUE_CLASS.get(value.getClass());
		if (dataType != null) {
			return dataType;
		}
		if (value instanceof Integer) {
			return DataType.LONG;
		}
		if (value instanceof Float) {
			return DataType.DOUBLE;
		}
		return null;
	}
	
	private ComboBox combo;
	private WithValueNode withValueNode;

	@Override
	protected void updateItem(AbstractNode node, boolean empty) {
        super.updateItem(node, empty);

		this.withValueNode = null;
		
		if (node == null) {
            setText(null);
            setGraphic(null);
			setEditable(false);
			return;
		}

		boolean editable = false;
		if (node instanceof WithValueNode) {
			withValueNode = (WithValueNode) node;
			editable = withValueNode.isValuePrimitive();
		}
        setEditable(editable);

		DataType dataType = withValueNode != null ? getDataType(withValueNode.getValue()) : null;
		
		if (isEditing()) {
			if (combo != null) {
				combo.setValue(dataType);
			}
			setText(null);
			setGraphic(combo);
		} else {
			setText(dataType != null ? dataType.getText() : null);
			setGraphic(null);
		}
	}
	
	@Override
    public void startEdit() {
		if (!isEditable()) {
			return;
		}
		//-- editable ? --//
		//-- graphic lazy init --//
		if (combo == null) {
			ObservableList<DataType> dataTypeList = FXCollections.observableArrayList(Arrays.asList(DataType.values()));
			combo = new ComboBox(dataTypeList);
		}
		combo.getSelectionModel().select(getDataType(withValueNode.getValue()));

        super.startEdit();
        setText(null);
        setGraphic(combo);
		combo.requestFocus();
    }

	@Override
	public void cancelEdit() {
        super.cancelEdit();
		
		//String text = textField.getText();
		//withValueNode.setValue(text);
		
		setText(getDataType(withValueNode.getValue()).getText());
        setGraphic(null);
    }
}
