package com.oallouch.mongodoc.tree;

import com.oallouch.mongodoc.tree.cell.NameColumnCell;
import com.oallouch.mongodoc.tree.node.AbstractNode;
import com.oallouch.mongodoc.tree.node.ArrayElementNode;
import com.oallouch.mongodoc.tree.node.ArrayEndNode;
import com.oallouch.mongodoc.tree.node.NodeTreeItem;
import com.oallouch.mongodoc.tree.node.PropertiesEndNode;
import com.oallouch.mongodoc.tree.node.PropertyNode;
import com.oallouch.mongodoc.tree.node.WithValueNode.SpecialValue;
import com.oallouch.mongodoc.util.TreeTableUtils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTablePosition;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class FloatingButtonBars {
	private DocumentTree documentTree;
	private TreeTableView<AbstractNode> treeTable;
	private TreeItem<AbstractNode> selectedContainerItem;
	private HBox horizontalButtonBar;
	
	public FloatingButtonBars(DocumentTree documentTree, TreeTableView<AbstractNode> treeTable) {
		this.documentTree = documentTree;
		this.treeTable    = treeTable;
		
		//--------------------------------------------------------------------//
		//---------------------- Horizontal Button Bar -----------------------//
		//--------------------------------------------------------------------//
		//-- add primitive --//
		Button addPrimitive = createButton("+", "Add a primitive", e -> {
			if (selectedContainerItem == null) {
				return;
			}
			AbstractNode selectedNode = selectedContainerItem.getValue();
			if (selectedNode.isContainsProperties()) {
				addProperty(""); // a String by default
			} else if (selectedNode.isContainsArrayElements()) {
				addArrayElement("");
			}
		});
		//-- add properties --//
		Button addProperties = createButton("+{}", "Add a Document", e -> {
			if (selectedContainerItem == null) {
				return;
			}
			AbstractNode selectedNode = selectedContainerItem.getValue();
			if (selectedNode.isContainsProperties()) {
				addProperty(SpecialValue.properties);
			} else if (selectedNode.isContainsArrayElements()) {
				addArrayElement(SpecialValue.properties);
			}
		});
		//-- add array --//
		Button addArray = createButton("+[]", "Add a List", e -> {
			if (selectedContainerItem == null) {
				return;
			}
			AbstractNode selectedNode = selectedContainerItem.getValue();
			if (selectedNode.isContainsProperties()) {
				addProperty(SpecialValue.array);
			} else if (selectedNode.isContainsArrayElements()) {
				addArrayElement(SpecialValue.array);
			}
		});
		
		horizontalButtonBar = new HBox(addPrimitive, addProperties, addArray);
		horizontalButtonBar.getStyleClass().add("treeTableFloatingButtonBarHBox");
		horizontalButtonBar.setVisible(false);
		documentTree.getChildren().add(horizontalButtonBar);
		
		//--------------------------------------------------------------------//
		//---------------------------- Listeners -----------------------------//
		//--------------------------------------------------------------------//
		treeTable.getSelectionModel().selectedItemProperty().addListener(this::selectionChanged);
		// hides the button bar when editing
		treeTable.editingCellProperty().addListener((observableValue, oldPosition, newPosition) -> {
			if (newPosition != null) {
				horizontalButtonBar.setVisible(false);
			}
		});
	}
	
	private Button createButton(String text, String tooltipText, EventHandler<ActionEvent> actionListener) {
		Button button = new Button(text);
		button.setOnAction(actionListener);
		button.setTooltip(new Tooltip(tooltipText));
		return button;
	}
	
	private void selectionChanged(ObservableValue<? extends TreeItem<AbstractNode>> observable, TreeItem<AbstractNode> oldValue, TreeItem<AbstractNode> newValue) {
		if (newValue == null) {
			selectedContainerItem = null;
			horizontalButtonBar.setVisible(false);
		} else {
			AbstractNode node = newValue.getValue();
			System.out.println("selected node: " + node);
			selectedContainerItem = node.findPropertiesOrArray().getTreeItem();
			horizontalButtonBar.setVisible(true);
			
			//---------------------------- position --------------------------//
			TreeTableRow<AbstractNode> row = documentTree.getTreeTableRow(selectedContainerItem);
			NameColumnCell nameCell = TreeTableUtils.getCellOfType(row, NameColumnCell.class);
			//-------------------- x -------------------//
			Text nameText = TreeTableUtils.getChildOfType(nameCell, Text.class); // there can also be an arrow (the disclosure node)
			horizontalButtonBar.setLayoutX(nameText.getBoundsInParent().getMaxX()
				+ 10); // maybe a treeTable insert (plus a gap)
			//-------------------- y -------------------//
			double headerHeight = TreeTableUtils.getTableHeaderRow(treeTable).getHeight();
			Bounds rowBounds = row.getBoundsInParent();
			horizontalButtonBar.setLayoutY(
				headerHeight
				+ rowBounds.getMinY()
				+ 15); // ?
		}
	}
	
	private void addProperty(Object value) {
		NodeTreeItem propertyItem     = new NodeTreeItem(new PropertyNode(value));
		NodeTreeItem propertiesEndItem = new NodeTreeItem(new PropertiesEndNode());
		selectedContainerItem.getChildren().addAll(propertyItem, propertiesEndItem);
		//documentTree.editName(propertyItem);
	}
	private void addArrayElement(Object value) {
		NodeTreeItem arrayElementItem = new NodeTreeItem(new ArrayElementNode(value));
		NodeTreeItem arrayEndItem     = new NodeTreeItem(new ArrayEndNode());
		selectedContainerItem.getChildren().addAll(arrayElementItem, arrayEndItem);
		//documentTree.editName(selectedContainerItem);
	}
}
