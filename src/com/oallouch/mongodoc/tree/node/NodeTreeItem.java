package com.oallouch.mongodoc.tree.node;

import javafx.scene.control.TreeItem;

public class NodeTreeItem extends TreeItem<AbstractNode> {
	public NodeTreeItem(AbstractNode node) {
		super(node);
		node.setTreeItem(this);
	}
}
