package com.oallouch.mongodoc.tree;

import com.oallouch.mongodoc.DocumentEditor;
import com.oallouch.mongodoc.node.AbstractNode;
import com.oallouch.mongodoc.node.ArrayElementNode;
import com.oallouch.mongodoc.node.ArrayEndNode;
import com.oallouch.mongodoc.node.PropertiesEndNode;
import com.oallouch.mongodoc.node.PropertyNode;
import com.oallouch.mongodoc.node.RootNode;
import com.oallouch.mongodoc.node.WithValueNode;
import com.oallouch.mongodoc.node.WithValueNode.SpecialValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TreeTableCell;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;

public class NameColumnCell extends TreeTableCell<AbstractNode, AbstractNode> {
	private static ObservableList<String> QUERY_OPERATORS = FXCollections.observableArrayList(
		"$gt", "$gte", "$in", "$lt", "$lte", "$ne", "$nin"
	);
	private ComboBox combo;

	@Override
	protected void updateItem(AbstractNode node, boolean empty) {
        setEditable(node instanceof PropertyNode);
        super.updateItem(node, empty);
		
		if (node == null) {
            setText(null);
            setGraphic(null);
			setEditable(false);
			return;
		}

		if (isEditing()) {
			if (combo != null) {
				combo.setValue(node.toString());
			}
			setText(null);
			setGraphic(combo);
		} else {
			String text = getReadValue(node);
			setText(text);
			setGraphic(null);
		}
	}
	
	private static String getReadValue(AbstractNode node) {
		String text = "";
		if (node instanceof PropertyNode) {
			PropertyNode propertyNode = (PropertyNode) node;
			text = propertyNode.getName();
		} else if (node instanceof ArrayElementNode) {
			ArrayElementNode arrayElementNode = (ArrayElementNode) node;
			text = Integer.toString(arrayElementNode.getIndex());
		} else if (node instanceof RootNode) {
			text = "{";
		} else if (node instanceof PropertiesEndNode) {
			text = "}";
		} else if (node instanceof ArrayEndNode) {
			text = "]";
		}
		if (node instanceof WithValueNode) {
			text += " :";
			WithValueNode withValueNode = (WithValueNode) node;
			Object nodeValue = withValueNode.getValue();
			if (nodeValue == SpecialValue.properties) {
				text += " {";
			} else if (nodeValue == SpecialValue.array) {
				text += " [";
			}
		}
		return text;
	}
	
	@Override
    public void startEdit() {
		if (!isEditable()) {
			return;
		}
		//-- editable ? --//
		final AbstractNode node = getItem();
		//-- graphic lazy init --//
		if (combo == null) {
			// inspired by CellUtils
			combo = new ComboBox();
			combo.setItems(QUERY_OPERATORS);
			combo.setEditable(true);
			combo.setOnKeyReleased(t -> {
				if (t.getCode() == KeyCode.ESCAPE) {
					String text = ((PropertyNode) node).getName();
					combo.setValue(text);
					cancelEdit();
				} else if (t.getCode() == KeyCode.ENTER) {
					cancelEdit();
				}
			});
			combo.setOnAction(t -> cancelEdit());
			//textField.focusedProperty().addListener(focusListener);
		}
		combo.setValue(node.toString());

        super.startEdit();
        setText(null);
        setGraphic(combo);
		combo.requestFocus();
    }

	/**
	 * acts as a cancel, put also as a commmit method
	 */
	@Override
	public void cancelEdit() {
        super.cancelEdit();

		PropertyNode propertyNode = (PropertyNode) getItem();
		
		String newName = (String) combo.getValue();
		if (!newName.equals(propertyNode.getName())) {
			propertyNode.setName(newName);
			fireEvent(new InputEvent(DocumentEditor.MODIFIED));
		}
		
		setText(getReadValue(propertyNode));
        setGraphic(null);
    }
}
