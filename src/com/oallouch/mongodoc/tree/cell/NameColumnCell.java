package com.oallouch.mongodoc.tree.cell;

import com.oallouch.mongodoc.DocumentEditor;
import com.oallouch.mongodoc.tree.node.AbstractNode;
import com.oallouch.mongodoc.tree.node.ArrayElementNode;
import com.oallouch.mongodoc.tree.node.ArrayEndNode;
import com.oallouch.mongodoc.tree.node.PropertiesEndNode;
import com.oallouch.mongodoc.tree.node.PropertyNode;
import com.oallouch.mongodoc.tree.node.RootNode;
import com.oallouch.mongodoc.tree.node.WithValueNode;
import com.oallouch.mongodoc.tree.node.WithValueNode.SpecialValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;

/**
 * shows names, but also ": {", ": [", "}" or "]" signs, which are, of course, not  editable
 */
public class NameColumnCell extends AbstractCell {
	private static ObservableList<String> QUERY_OPERATORS = FXCollections.observableArrayList(
		"$gt", "$gte", "$in", "$lt", "$lte", "$ne", "$nin"
	);
	private ComboBox combo;

	@Override
	protected void updateItem(Object cellValue, boolean empty) {
        super.updateItem(cellValue, empty);
		AbstractNode node = getAbstractNode();
        setEditable(node instanceof PropertyNode);
		
		if (node == null) {
            setText(null);
            setGraphic(null);
			setEditable(false);
			return;
		}
		
		//System.out.println("index: " + getIndex() + ", node type: " + node.getClass().getSimpleName());

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
		final AbstractNode node = getAbstractNode();
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

		PropertyNode propertyNode = (PropertyNode) getAbstractNode();
		
		String newName = (String) combo.getValue();
		if (!newName.equals(propertyNode.getName())) {
			propertyNode.setName(newName);
			fireEvent(new InputEvent(DocumentEditor.MODIFIED));
		}
		
		setText(getReadValue(propertyNode));
        setGraphic(null);
    }
}
