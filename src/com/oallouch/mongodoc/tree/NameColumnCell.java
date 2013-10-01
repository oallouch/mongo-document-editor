package com.oallouch.mongodoc.tree;

import com.oallouch.mongodoc.node.AbstractNode;
import com.oallouch.mongodoc.node.ArrayElementNode;
import com.oallouch.mongodoc.node.PropertyNode;
import com.oallouch.mongodoc.node.RootNode;
import com.oallouch.mongodoc.node.WithValueNode;
import com.oallouch.mongodoc.node.WithValueNode.SpecialValue;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableCell;
import javafx.scene.input.KeyCode;

public class NameColumnCell extends TreeTableCell<AbstractNode, AbstractNode> {
	
	private TextField textField;

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
			if (textField != null) {
				textField.setText(node.toString());
			}
			setText(null);
			setGraphic(textField);
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
		if (textField == null) {
			// inspired by CellUtils.createTextField
			textField = new TextField();
			textField.setOnKeyReleased(t -> {
				if (t.getCode() == KeyCode.ESCAPE) {
					String text = ((PropertyNode) node).getName();
					textField.setText(text);
					cancelEdit();
				} else if (t.getCode() == KeyCode.ENTER) {
					cancelEdit();
				}
			});
			//textField.focusedProperty().addListener(focusListener);
		}
		textField.setText(node.toString());

        super.startEdit();
        setText(null);
        setGraphic(textField);
		textField.requestFocus();
    }

	@Override
	public void cancelEdit() {
        super.cancelEdit();

		AbstractNode node = getItem();
		String text = textField.getText();
		((PropertyNode) node).setName(text);
		
		setText(getReadValue(node));
        setGraphic(null);
    }
}
