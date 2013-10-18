package com.oallouch.mongodoc.tree.cell;

import com.oallouch.mongodoc.tree.node.AbstractNode;
import com.oallouch.mongodoc.tree.node.ArrayElementNode;
import com.oallouch.mongodoc.tree.node.PropertyNode;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;

public class NameColumnValueFactory implements Callback<TreeTableColumn.CellDataFeatures<AbstractNode, Object>, ObservableValue<?>> {

	@Override
	public ObservableValue<?> call(TreeTableColumn.CellDataFeatures<AbstractNode, Object> cdf) {
		AbstractNode node = cdf.getValue().getValue();
		if (node instanceof PropertyNode) {
			return ((PropertyNode) node).nameProperty();
		} else if (node instanceof ArrayElementNode) {
			return ((ArrayElementNode) node).indexProperty();
		} else {
			return null;
		}
	}
}
