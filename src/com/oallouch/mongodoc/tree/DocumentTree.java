package com.oallouch.mongodoc.tree;

import com.oallouch.mongodoc.tree.cell.TypeColumnCell;
import com.oallouch.mongodoc.tree.cell.ValueColumnCell;
import com.oallouch.mongodoc.tree.cell.NameColumnCell;
import com.oallouch.mongodoc.tree.cell.NameColumnValueFactory;
import com.oallouch.mongodoc.tree.node.AbstractNode;
import com.oallouch.mongodoc.util.FXUtils;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.Pane;


public class DocumentTree extends Pane {
	/**
	 * very important to be able to quickly calculate the preferred height
	 */
	public static final double FIXED_CELL_HEIGHT = 30;
	
	private TreeTableView<AbstractNode> treeTable;
	private TreeItem hiddenRootItem;
	private ReadOnlyDoubleWrapper heightWhenAllVisible = new ReadOnlyDoubleWrapper();;
    
    public DocumentTree(boolean editable) {
		treeTable = new TreeTableView<>();
		treeTable.setEditable(editable);
		
		TreeTableColumn nameCol = new TreeTableColumn<>("Name"); // generics can't be used here
		nameCol.setCellValueFactory(new NameColumnValueFactory());
		nameCol.setCellFactory(treeTableColumn -> new NameColumnCell());
		nameCol.setSortable(false);

		TreeTableColumn<AbstractNode, Object> valueCol = new TreeTableColumn<>("Value");
		valueCol.setCellValueFactory(new TreeItemPropertyValueFactory("value"));
		valueCol.setCellFactory(treeTableColumn -> new ValueColumnCell());
		valueCol.setSortable(false);

		TreeTableColumn<AbstractNode, Object> typeCol = new TreeTableColumn<>("Type");
		typeCol.setCellValueFactory(new TreeItemPropertyValueFactory("value"));
		typeCol.setCellFactory(treeTableColumn -> new TypeColumnCell());
		typeCol.setSortable(false);
		typeCol.setMinWidth(130);
		typeCol.setMaxWidth(130);

		treeTable.getColumns().setAll(nameCol, valueCol, typeCol);
		
		treeTable.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
		treeTable.getSelectionModel().setCellSelectionEnabled(true);
		treeTable.setFixedCellSize(FIXED_CELL_HEIGHT);
		
		FXUtils.whenExists(treeTable.skinProperty(), (skin) -> {
			heightWhenAllVisible.bind(
				treeTable.expandedItemCountProperty().multiply(FIXED_CELL_HEIGHT)
				.add(FXUtils.getTableHeaderRow(treeTable).heightProperty())
				.add(4)); // 4 should be replaced by insets calculations
		});
		
		hiddenRootItem = new TreeItem();
		treeTable.setRoot(hiddenRootItem);
		treeTable.setShowRoot(false);
		
		this.getChildren().add(treeTable);
		
		//minHeightProperty().bind(treeTable.heightProperty());
		//prefHeightProperty().bind(treeTable.heightProperty());
		//maxHeightProperty().bind(treeTable.heightProperty());
		
		if (editable) {
			new FloatingButtonBars(this, treeTable);
		}
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
		if (jsonObject == null) {
			jsonObject = Collections.emptyMap();
		}
		hiddenRootItem.getChildren().clear();
		TreeItemFactory.createRootTreeItem(treeTable, jsonObject, hiddenRootItem);
		expandAll(treeTable.getRoot());
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
	
	public double getHeightWhenAllVisible() {
		return heightWhenAllVisible.get();
	}
	public ReadOnlyDoubleProperty heightWhenAllVisibleProperty() {
		return heightWhenAllVisible.getReadOnlyProperty();
	}
}
