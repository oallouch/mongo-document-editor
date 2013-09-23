package com.oallouch.mongodoc.tree;

import com.google.common.collect.Maps;
import com.oallouch.mongodoc.node.AbstractNode;
import com.oallouch.mongodoc.node.PropertyNode;
import java.util.Map;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.util.StringConverter;

public class DocumentTreeCell extends TreeCell<AbstractNode> {
	private static final Map<Class<? extends AbstractNode>, StringConverter<? extends AbstractNode>> STRING_CONVERTERS = Maps.newHashMapWithExpectedSize(6);

	static {
		/*STRING_CONVERTERS.put(PropertiesNode.class, new StringConverter<PropertiesNode>() {
			@Override public String toString(PropertiesNode node) { return "{"; }
			@Override public PropertiesNode fromString(String string) { return null; }
		});
		STRING_CONVERTERS.put(PropertyNode.class, new StringConverter<PropertyNode>() {
			@Override public String toString(PropertyNode node) { return node.getName(); }
			@Override public PropertyNode fromString(String string) { return null; }
		});
		STRING_CONVERTERS.put(PrimitiveValueNode.class, new StringConverter<PrimitiveValueNode>() {
			@Override public String toString(PrimitiveValueNode node) {
				return node == null ? null : node.getEditingString();
			}
			@Override public PrimitiveValueNode fromString(String string) { return null; }
		});*/
	}


    private TextField textField;
	
	private Node currentGraphic;
	private ChangeListener focusListener;

	public DocumentTreeCell() {
		focusListener = new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (!newValue) {
					if (isEditing()) {
						//cancelEdit();
					}
				}
			}
		};
	}

	@Override
    public void startEdit() {
		//-- editable ? --//
		final AbstractNode node = getItem();
		//-- graphic lazy init --//
		if (textField == null) {
			// inspired by CellUtils.createTextField
			textField = new TextField(node.toString());
			textField.setOnKeyReleased(t -> {
				if (t.getCode() == KeyCode.ENTER) {
					String text = textField.getText();
					/*if (node instanceof PrimitiveValueNode) {
						((PrimitiveValueNode) node).setEditingString(text);
					} else {*/
						text = removeEndingColon(text); // in case the user enters ":" or " :" at the end
						((PropertyNode) node).setName(text);
					//}
					cancelEdit();
				} else if (t.getCode() == KeyCode.ESCAPE) {
					cancelEdit();
				}
			});
			textField.focusedProperty().addListener(focusListener);
		}
		currentGraphic = textField;

        super.startEdit();
        setText(null);
        setGraphic(currentGraphic);
		if (currentGraphic != null) {
			currentGraphic.requestFocus();
		}
    }

	private static String removeEndingColon(String text) {
		if (text.endsWith(":")) { // not " :" by safety
			text = text.substring(text.length() - 2).trim(); // trim if it ands by " :", and not ":"
		}
		return text;
	}

	@Override
	public void cancelEdit() {
        super.cancelEdit();

		AbstractNode node = getItem();
		setText(node.toString());
        setGraphic(null);
    }

    @Override
	public void updateItem(AbstractNode item, boolean empty) {
        super.updateItem(item, empty);

        TreeItem<AbstractNode> treeItem = getTreeItem();
        Node graphic = treeItem == null ? null : treeItem.getGraphic();
		if (item == null) {
            setText(null);
            setGraphic(graphic);
		} else {
            if (isEditing()) {
                if (textField != null) {
                    textField.setText(item.toString());
                }
                setText(null);
				setGraphic(textField);
            } else {
				setText(item.toString());
                setGraphic(graphic);
            }
		}
    }
}
