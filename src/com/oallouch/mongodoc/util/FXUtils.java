package com.oallouch.mongodoc.util;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import com.sun.javafx.scene.control.skin.TreeTableViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;

public class FXUtils {
	public static <T> VirtualFlow<TreeTableRow> getVirtualFlow(TreeTableView<T> treeTable) {
		TreeTableViewSkin<T> treeTableSkin = (TreeTableViewSkin<T>) treeTable.getSkin();
		return (VirtualFlow<TreeTableRow>) treeTableSkin.getChildren().get(1);
	}
	
	public static <T> TableHeaderRow getTableHeaderRow(TreeTableView<T> treeTable) {
		TreeTableViewSkin<T> treeTableSkin = (TreeTableViewSkin<T>) treeTable.getSkin();
		return (TableHeaderRow) treeTableSkin.getChildren().get(0);
	}
	
	public static <T> TreeTableRow<T> getTreeTableRow(TreeTableView<T> treeTable, TreeItem<T> treeItem) {
		int index = treeTable.getRow(treeItem);
		return getVirtualFlow(treeTable).getCell(index);
	}
	
	public static <T extends TreeTableCell> T getCellOfType(TreeTableRow row, Class<T> cellClass) {
		return getChildOfType(row, cellClass);
	}
	
	public static <T extends Node> T getChildOfType(Parent parent, Class<T> cellClass) {
		for (Node node : parent.getChildrenUnmodifiable()) {
			if (cellClass.isInstance(node)) {
				return (T) node;
			}
		}
		return null;
	}
	
	public static <T> void whenExists(ObservableValue<T> observable, Consumer<T> consumer) {
		when(observable, t -> t != null, consumer);
	}
	public static <T> void when(ObservableValue<T> observable, Predicate<T> predicate, Consumer<T> consumer) {
		if (predicate.test(observable.getValue())) {
			consumer.accept(observable.getValue());
		} else {
			ChangeListener[] listenerHolder = new ChangeListener[1];
			final ChangeListener<T> listener = (observable1, oldValue, newValue) -> {
				if (predicate.test(newValue)) {
					consumer.accept(newValue);
					observable.removeListener(listenerHolder[0]);
				}
			};
			listenerHolder[0] = listener;
			observable.addListener(listener);
		}
	}
	
	/**
	 * 
	 * @param parent
	 * @param child
	 * @param index
	 * @return true if the index is used. false if it's been added at the end.
	 */
	public static boolean addChild(TreeItem parent, TreeItem child, int index) {
		ObservableList children = parent.getChildren();
		if (index < 0 || index >= children.size()) {
			children.add(child);
			return false;
		} else {
			children.add(index, child);
			return true;
		}
	}
	
	public static int getIndexInParent(TreeItem item) {
		return item.getParent().getChildren().indexOf(item);
	}
}
