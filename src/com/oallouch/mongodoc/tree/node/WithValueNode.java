package com.oallouch.mongodoc.tree.node;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TreeItem;

public abstract class WithValueNode extends AbstractNode {
	public static enum SpecialValue { properties, array };
	
	private ObjectProperty value = new SimpleObjectProperty();

	public WithValueNode(Object value) {
		setValue(value);
	}
	
	public void remove() {
		AbstractNode closingNode = getEndingNode();
		// we have to use removeAll because a removal changes the selectedItem
		// so, to avoid trouble, it's simpler to it all at once
		TreeItem<AbstractNode> treeItem = getTreeItem();
		TreeItem<AbstractNode> parent = treeItem.getParent();
		if (closingNode != null) {
			parent.getChildren().removeAll(treeItem, closingNode.getTreeItem());
		} else {
			parent.getChildren().remove(treeItem);
		}
		if (this instanceof ArrayElementNode) {
			parent.getValue().reindexArray();
		}
	}
	
	public AbstractNode getEndingNode() {
		boolean thisFound = false;
		TreeItem<AbstractNode> treeItem = getTreeItem();
		for (TreeItem<AbstractNode> child : treeItem.getParent().getChildren()) {
			if (!thisFound) {
				if (child.equals(treeItem)) {
					thisFound = true;
				}
			} else {
				AbstractNode childNode = child.getValue();
				if (childNode instanceof PropertiesEndNode || childNode instanceof ArrayEndNode) {
					return childNode;
				}
			}
		}
		return null;
	}
	
	public boolean isValuePrimitive() {
		return !(value.get() instanceof SpecialValue);
	}
	
	public Object getValue() {
		return value.get();
	}
	public void setValue(Object value) {
		this.value.set(value);
	}
	public ObjectProperty valueProperty() {
		return value;
	}
}