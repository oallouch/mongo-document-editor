package com.oallouch.mongodoc.tree;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.oallouch.mongodoc.DocumentEditor;
import com.oallouch.mongodoc.JsonArea;
import com.oallouch.mongodoc.node.AbstractNode;
import com.oallouch.mongodoc.node.WithValueNode;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TreeTableCell;
import javafx.scene.input.InputEvent;

public class TypeColumnCell extends TreeTableCell<AbstractNode, AbstractNode> {
	public static enum DataType {
		NULL("Null", null, v -> null, null),
		STRING("String", String.class, v -> String.valueOf(v), ""),
		DOUBLE("Double/Float", Double.class, v -> {
			if (v instanceof Number) {
				return ((Number) v).doubleValue();
			} else if (v instanceof String) {
				return Double.valueOf((String) v);
			} else {
				throw new IllegalArgumentException("Not a Double: " + v);
			}
		}, new Double(0)),
		LONG("Long/Integer", Long.class, v -> {
			if (v instanceof Number) {
				return ((Number) v).longValue();
			} else if (v instanceof String) {
				return Long.valueOf((String) v);
			} else {
				throw new IllegalArgumentException("Not a Long: " + v);
			}
		}, new Long(0)),
		BOOLEAN("Boolean", Boolean.class, v -> {
			if (v instanceof Boolean) {
				return (Boolean) v;
			} else if (v instanceof String) {
				return Boolean.valueOf((String) v);
			} else {
				throw new IllegalArgumentException("Not a Boolean: " + v);
			}
		}, Boolean.TRUE),
		DATE("Date", Date.class, v -> new Date(), null),
		PATTERN("Pattern", Pattern.class, v -> {
			if (v instanceof Pattern) {
				return (Pattern) v;
			} else if (v instanceof String) {
				return Pattern.compile((String) v);
			} else {
				throw new IllegalArgumentException("Not a Pattern: " + v);
			}
		}, Pattern.compile(""));
		
		private String text;
		private Class valueClass;
		private Function<Object, Object> converter;
		private Object defaultValue;
		
		DataType(String text, Class valueClass, Function<Object, Object> converter, Object defaultValue) {
			this.text = text;
			this.valueClass = valueClass;
			this.converter = converter;
			this.defaultValue = defaultValue;
		}
		
		public Object toValueOfType(Object value) {
			try {
				return converter.apply(value);
			} catch (Throwable e) {
				Logger.getLogger(TypeColumnCell.class.getName()).log(Level.INFO, "Bad value for this type: " + value);
				return defaultValue;
			}
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
	
	private ComboBox<DataType> combo;
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
			combo = new ComboBox<>(dataTypeList);
		}
		combo.getSelectionModel().select(getDataType(withValueNode.getValue()));
		combo.setOnAction(t -> cancelEdit());

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

		DataType newDataType = combo.getValue();
		if (newDataType != getDataType(withValueNode.getValue())) {
			System.out.println("type changed");
			withValueNode.setValue(newDataType.toValueOfType(withValueNode.getValue()));
			getTreeTableRow().updateTreeItem(null);
			fireEvent(new InputEvent(DocumentEditor.MODIFIED));
		}
		
		setText(newDataType.getText());
        setGraphic(null);
    }
}
