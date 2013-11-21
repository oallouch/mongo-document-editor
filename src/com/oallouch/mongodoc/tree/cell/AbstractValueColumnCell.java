package com.oallouch.mongodoc.tree.cell;

import com.oallouch.mongodoc.tree.DataType;
import com.oallouch.mongodoc.tree.node.AbstractNode;
import com.oallouch.mongodoc.tree.node.WithValueNode;
import com.oallouch.mongodoc.tree.node.WithValueNode.SpecialValue;
import javafx.event.EventHandler;
import javafx.scene.control.TreeTableCell;
import javafx.scene.input.MouseEvent;

public abstract class AbstractValueColumnCell extends AbstractCell {
	private DataType dataType;
	

	@Override
	protected void updateItem(Object value, boolean empty) {
		if (!empty) {
			if (value instanceof SpecialValue) {
				empty = true;
			} else {
				AbstractNode node = getTreeTableRow().getItem();
				if (!(node instanceof WithValueNode)) {
					empty = true;
				}
			}
		}
		super.updateItem(value, empty);
		if (empty) {
			setText(null);
			setGraphic(null);
			setEditable(false);
			return;
		}
		
		this.dataType = DataType.getDataType(getItem());
		if (dataType == null) {
			System.out.println("NULL DATA_TYPE");
		}
		updateItem(value, dataType);
	}
	
	protected abstract void updateItem(Object value, DataType dataType) ;
	
	protected WithValueNode getWithValueNode() {
		return (WithValueNode) getTreeTableRow().getItem();
	}
	
	protected DataType getDataType() {
		return dataType;
	}
	
	protected Object getValue() {
		return getItem();
	}
	protected void setValue(Object value) {
		getWithValueNode().setValue(value);
	}
}
