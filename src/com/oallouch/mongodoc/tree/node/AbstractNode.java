package com.oallouch.mongodoc.tree.node;

import com.oallouch.mongodoc.tree.node.WithValueNode.SpecialValue;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TreeItem;

public abstract class AbstractNode {
	/**
	 * . contains the error message
	 * . null is no error
	 */
	private StringProperty error = new SimpleStringProperty();
	private TreeItem<AbstractNode> treeItem;

    public String getError() {
        return error.get();
    }
    public AbstractNode setError(String value) {
		error.set(value);
        return this;
    }
	public StringProperty errorProperty() {
		return error;
	}
	
	public TreeItem getTreeItem() {
		return treeItem;
	}
	public void setTreeItem(TreeItem treeItem) {
		this.treeItem = treeItem;
	}

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
	
	public AbstractNode getParent() {
		TreeItem<AbstractNode> parentItem = getTreeItem().getParent();
		return parentItem == null ? null : parentItem.getValue();
	}
	
	
	public boolean isContainsProperties() {
		if (this instanceof RootNode) {
			return true;
		}
		if (this instanceof WithValueNode) {
			WithValueNode withValueNode = (WithValueNode) this;
			return withValueNode.getValue() == SpecialValue.properties;
		}
		return false;
	}
	public boolean isContainsArrayElements() {
		if (this instanceof WithValueNode) {
			WithValueNode withValueNode = (WithValueNode) this;
			return withValueNode.getValue() == SpecialValue.array;
		}
		return false;
	}
	/**
	 * more complicated than isLeaf, but more node type may come in the future (like ObjectId)
	 * @return 
	 */
	public boolean isPropertiesOrArray() {
		return isContainsProperties() || isContainsArrayElements();
	}
	
	public AbstractNode findPropertiesOrArray() {
		AbstractNode currentNode = this;
		while (currentNode != null) {
			if (currentNode.isPropertiesOrArray()) {
				return currentNode;
			}
			currentNode = currentNode.getParent();
		}
		return null;
	}
}
