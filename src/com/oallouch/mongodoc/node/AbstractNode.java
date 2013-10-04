package com.oallouch.mongodoc.node;

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
}
