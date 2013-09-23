package com.oallouch.mongodoc.tree;

import com.oallouch.mongodoc.node.AbstractNode;
import com.oallouch.mongodoc.node.PropertyNode;
import com.oallouch.mongodoc.node.WithValueNode;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableCell;

public class SecondColumnTreeTableCell extends TreeTableCell<AbstractNode, AbstractNode> {

	private TextField textField;

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
		WithValueNode withValueNode = null;
		if (node instanceof WithValueNode) {
			withValueNode = (WithValueNode) node;
			editable = withValueNode.isValuePrimitive();
		}
        setEditable(editable);

        //Node graphic = null; // treeItem.getGraphic();
		/*if (node instanceof OperatorNode) {
			OperatorNode operatorNode = (OperatorNode) node;
			currentStringConverter = OPERATOR_STRING_CONVERTER;
            if (isEditing()) {
				if (operatorComboBox != null) {
					operatorComboBox.getSelectionModel().select(operatorNode.getOperator());
				}
				setText(null);
				setGraphic(operatorComboBox);
            } else {
				setText(ensureEndingColor(currentStringConverter.toString(operatorNode.getOperator())));
                setGraphic(graphic);
            }
		} else {*/
		String text;
		if (withValueNode != null && withValueNode.isValuePrimitive()) {
			text = withValueNode.getValue().toString();
		} else {
			text = null;
		}
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
}
