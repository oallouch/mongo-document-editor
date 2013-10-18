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