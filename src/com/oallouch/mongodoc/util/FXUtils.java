package com.oallouch.mongodoc.util;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import com.sun.javafx.scene.control.skin.TreeTableRowSkin;
import com.sun.javafx.scene.control.skin.TreeTableViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Labeled;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.text.Text;

public class TreeTableUtils {
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
}
