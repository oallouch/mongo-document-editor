package com.oallouch.mongodoc.tree;

import com.oallouch.mongodoc.node.AbstractNode;
import com.oallouch.mongodoc.node.PropertyNode;
import com.oallouch.mongodoc.node.WithValueNode;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableCell;
import javafx.scene.input.KeyCode;

public class ValueColumnCell extends TreeTableCell<AbstractNode, AbstractNode> {

	private TextField textField;
	private WithValueNode withValueNode;

	@Override
	protected void updateItem(AbstractNode node, boolean empty) {
        super.updateItem(node, empty);

		if (node == null) {
            setText(null);
            setGraphic(null);
			setEditable(false);
			return;
		}

		boolean editable = false;
		this.withValueNode = null;
		if (node instanceof WithValueNode) {
			withValueNode = (WithValueNode) node;
			editable = withValueNode.isValuePrimitive();
			if (!editable) {
				withValueNode = null;
			}
		}
        setEditable(editable);

		String text = withValueNode != null ? withValueNode.getValue().toString() : null;
		
		if (isEditing()) {
			if (textField != null) {
				textField.setText(text);
			}
			setText(null);
			setGraphic(textField);
		} else {
			setText(text);
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
		if (textField == null) {
			// inspired by CellUtils.createTextField
			textField = new TextField();
			textField.setOnKeyReleased(t -> {
				if (t.getCode() == KeyCode.ESCAPE) {
					textField.setText(withValueNode.getValue().toString());
					cancelEdit();
				} else if (t.getCode() == KeyCode.ENTER) {
					cancelEdit();
				}
			});
			//textField.focusedProperty().addListener(focusListener);
		}
		textField.setText(withValueNode.getValue().toString());

        super.startEdit();
        setText(null);
        setGraphic(textField);
		textField.requestFocus();
    }

	@Override
	public void cancelEdit() {
        super.cancelEdit();
		
		String text = textField.getText();
		withValueNode.setValue(text);
		
		setText(text);
        setGraphic(null);
    }
}
