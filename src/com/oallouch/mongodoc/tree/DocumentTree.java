package com.oallouch.mongodoc.tree;

import com.oallouch.mongodoc.tree.cell.TypeColumnCell;
import com.oallouch.mongodoc.tree.cell.ValueColumnCell;
import com.oallouch.mongodoc.tree.cell.NameColumnCell;
import static com.oallouch.mongodoc.DocumentEditor.MODIFIED;
import com.oallouch.mongodoc.tree.cell.NameColumnValueFactory;
import com.oallouch.mongodoc.tree.node.AbstractNode;
import com.oallouch.mongodoc.util.FXUtils;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.InputEvent;
import javafx.scene.layout.Pane;


public class DocumentTree extends Pane {
	private TreeTableView<AbstractNode> treeTable;
	private TreeItem hiddenRootItem;
    
    public DocumentTree() {
		treeTable = new TreeTableView<>();
		TreeTableColumn nameCol = new TreeTableColumn<>("Name"); // generics can't be used here
		nameCol.setCellValueFactory(new NameColumnValueFactory());
		nameCol.setCellFactory(treeTableColumn -> new NameColumnCell());
		nameCol.setSortable(false);
		nameCol.addEventHandler(MODIFIED, e -> fireModified());

		TreeTableColumn<AbstractNode, Object> valueCol = new TreeTableColumn<>("Value");
		valueCol.setCellValueFactory(new TreeItemPropertyValueFactory("value"));
		valueCol.setCellFactory(treeTableColumn -> new ValueColumnCell());
		valueCol.setSortable(false);
		valueCol.addEventHandler(MODIFIED, e -> fireModified());

		TreeTableColumn<AbstractNode, Object> typeCol = new TreeTableColumn<>("Type");
		typeCol.setCellValueFactory(new TreeItemPropertyValueFactory("value"));
		typeCol.setCellFactory(treeTableColumn -> new TypeColumnCell());
		typeCol.setSortable(false);
		typeCol.addEventHandler(MODIFIED, e -> fireModified());
		typeCol.setMinWidth(130);
		typeCol.setMaxWidth(130);

		treeTable.getColumns().setAll(nameCol, valueCol, typeCol);
		
		treeTable.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
		treeTable.setEditable(true);
		treeTable.getSelectionModel().setCellSelectionEnabled(true);
		
		
		hiddenRootItem = new TreeItem();
		treeTable.setRoot(hiddenRootItem);
		treeTable.setShowRoot(false);
		
		this.getChildren().add(treeTable);
		
		new FloatingButtonBars(this, treeTable);
    }

	@Override
	protected void layoutChildren() {
		super.layoutChildren(); // to autosize children
		treeTable.resize(getWidth(), getHeight());
	}
	
    
    public void reset() {
        setRootJsonObject(new HashMap<>());
    }

	public Map<String, Object> getRootJsonObject() {
		if (hiddenRootItem.getChildren().isEmpty()) {
			return Collections.emptyMap();
		}
		return (Map<String, Object>) TreeItemFactory.toJsonObject((TreeItem) hiddenRootItem.getChildren().get(0));
	}
	public void setRootJsonObject(Map<String, Object> jsonObject) {
		hiddenRootItem.getChildren().clear();
		TreeItemFactory.createRootTreeItem(jsonObject, hiddenRootItem);
		expandAll(treeTable.getRoot());
	}
	
	private void fireModified() {
		fireEvent(new InputEvent(MODIFIED));
	}
	
	private void expandAll(TreeItem<?> item) {
		item.setExpanded(true);
		for (TreeItem<? extends Object> childItem : item.getChildren()) {
			expandAll(childItem);
		}
	}
	
	public void editName(TreeItem<AbstractNode> treeItem) {
		treeTable.edit(treeTable.getRow(treeItem), treeTable.getColumns().get(0));
	}
	public void editValue(TreeItem<AbstractNode> treeItem) {
		treeTable.edit(treeTable.getRow(treeItem), treeTable.getColumns().get(1));
	}
	
	public TreeTableRow<AbstractNode> getTreeTableRow(TreeItem<AbstractNode> treeItem) {
		return FXUtils.getTreeTableRow(treeTable, treeItem);
	}
}
