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

public class TreeColumnCell extends TreeTableCell<AbstractNode, AbstractNode> {
	
	private TextField textField;

	@Override
	protected void updateItem(AbstractNode node, boolean empty) {
		System.out.println("updateItem, value: " + node);
        setEditable(node instanceof PropertyNode);
        super.updateItem(node, empty);
		
		if (node == null) {
            setText(null);
            setGraphic(null);
			setEditable(false);
			return;
		}

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
		if (isEditing()) {
			if (textField != null) {
				textField.setText(node.toString());
			}
			setText(null);
			setGraphic(textField);
		} else {
			/*if (node instanceof WithValueNode) {
				text = ensureEndingColor(text);
			}*/
			String text = getReadValue(node);
			System.out.println("text: " + text);
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
			textField = new TextField(node.toString());
			textField.setOnKeyReleased(t -> {
				if (t.getCode() == KeyCode.ESCAPE) {
					String text = ((PropertyNode) node).getName();
					textField.setText(text);
					cancelEdit();
				} else if (t.getCode() == KeyCode.ESCAPE) {
					cancelEdit();
				}
			});
			//textField.focusedProperty().addListener(focusListener);
		}

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
	
	public static String toReadText(PropertyNode propertyNode) {
		return propertyNode.getName();
	}
	
}
