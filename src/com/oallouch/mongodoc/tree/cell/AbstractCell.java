package com.oallouch.mongodoc.tree.cell;

import com.oallouch.mongodoc.tree.node.AbstractNode;
import javafx.scene.control.TreeTableCell;

public abstract class AbstractCell extends TreeTableCell<AbstractNode, Object> {
	public AbstractCell() {
		setOnDragDetected(mouseEvent -> {
			System.out.println("drag detected");
		});
	}
}
