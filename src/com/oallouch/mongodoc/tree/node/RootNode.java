package com.oallouch.mongodoc.tree.node;

import javafx.scene.control.TreeTableView;


public class RootNode extends AbstractNode {
	private TreeTableView<AbstractNode> treeTable;
	
	public RootNode(TreeTableView<AbstractNode> treeTable) {
		this.treeTable = treeTable;
	}
	
	public TreeTableView<AbstractNode> getTreeTable() {
		return treeTable;
	}
}
