package com.oallouch.mongodoc.tree;

import com.oallouch.mongodoc.DocumentEditor;
import com.oallouch.mongodoc.node.AbstractNode;
import com.oallouch.mongodoc.node.WithValueNode;
import com.oallouch.mongodoc.node.WithValueNode.SpecialValue;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableCell;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import jidefx.scene.control.field.DateField;
import jidefx.scene.control.field.DoubleField;
import jidefx.scene.control.field.IntegerField;

public class ValueColumnCell extends AbstractValueColumnCell {
	private TextField textField;
	private CheckBox checkBox;
	private DateField dateField;
	private DoubleField doubleField;
	private IntegerField integerField;
	
	@Override
	protected void updateItem(Object value, DataType dataType) {
		
        setEditable(dataType != DataType.NULL);

		String text = null;
		Node readGraphics = null;
		if (dataType == DataType.BOOLEAN) {
			readGraphics = getCheckBox((Boolean) value);
		} else {
			text = value == null ? "null" : value.toString();
		}
		
		if (isEditing()) {
			Node graphics = getEditNode(value);
			setText(null);
			setGraphic(graphics);
		} else {
			setText(text);
			setGraphic(readGraphics);
		}
	}
	
	private CheckBox getCheckBox(boolean value) {
		if (checkBox == null) {
			checkBox = new CheckBox();
			checkBox.setOnAction(e -> {
				setValue(checkBox.isSelected());
				fireEvent(new InputEvent(DocumentEditor.MODIFIED));
			});
		}
		checkBox.setSelected(value);
		return checkBox;
	}
	
	private TextField getTextField(String text) {
		if (textField == null) {
			textField = new TextField();
			textField.setOnKeyReleased(t -> {
				if (t.getCode() == KeyCode.ESCAPE) {
					textField.setText(getItem().toString());
					cancelEdit();
				} else if (t.getCode() == KeyCode.ENTER) {
					cancelEdit();
				}
			});
		}
		textField.setText(text);
		return textField;
	}
	
	private Node getDateField(Date value) {
		if (dateField == null) {
			dateField = new DateField(SimpleDateFormat.getDateTimeInstance());
			//dateFieldContainer = new DecorationPane(dateField);
		}
		dateField.setValue(value);
		return dateField;//dateFieldContainer;
	}
	
	private DoubleField getDoubleField(Double value) {
		if (doubleField == null) {
			doubleField = new DoubleField();
		}
		doubleField.setValue(value);
		return doubleField;
	}
	
	private IntegerField getIntegerField(Integer integer) {
		if (integerField == null) {
			integerField = new IntegerField();
		}
		integerField.setValue(integer);
		return integerField;
	}
	
	private Node getEditNode(Object value) {
		DataType dataType = getDataType();
		if (dataType == DataType.BOOLEAN) {
			return getCheckBox((Boolean) value);
		} else if (dataType == DataType.DATE) {
			return getDateField((Date) value);
		} else if (dataType == DataType.DOUBLE) {
			return getDoubleField((Double) DataType.DOUBLE.toValueOfType(value));
		} else if (dataType == DataType.LONG) {
			return getIntegerField(((Number) value).intValue());
		} else {
			return getTextField(value.toString());
		}
	}
	
	@Override
    public void startEdit() {
		if (!isEditable()) {
			return;
		}
        super.startEdit();
		
		Node graphics = getEditNode(getItem());

        setText(null);
        setGraphic(graphics);
		graphics.requestFocus();
    }

	@Override
	public void cancelEdit() {
        super.cancelEdit();
		
		DataType dataType = getDataType();
		if (dataType != DataType.BOOLEAN) {
			Object oldValue = getItem();
			Object newValue;
			if (dataType == DataType.DATE) {
				newValue = dateField.getValue();
			} else if (dataType == DataType.DOUBLE) {
				newValue = doubleField.getValue();
			} else if (dataType == DataType.LONG) {
				newValue = integerField.getValue();
			} else {
				newValue = textField.getText();
			}
			if (!Objects.equals(oldValue, newValue)) {
				getWithValueNode().setValue(newValue);
				fireEvent(new InputEvent(DocumentEditor.MODIFIED));
			}
			updateItem(newValue, false);
		}
		
    }
}
