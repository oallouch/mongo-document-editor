package com.oallouch.mongodoc.ui.module;

import com.oallouch.mongodoc.node.AbstractNode;
import com.oallouch.mongodoc.node.WithSingleChildNode;

public class SecondColumnTreeTableCell extends AbstractTreeTableCell {

	@Override
	protected AbstractNode getNode() {
		AbstractNode node = super.getNode();
		if (node instanceof WithSingleChildNode) {
			return node.getChild(0);
		}
		return null;
	}
}
