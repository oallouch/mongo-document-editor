package com.oallouch.mongodoc.tree;

import com.oallouch.mongodoc.DocumentEditor;
import com.oallouch.mongodoc.tree.cell.NameColumnCell;
import com.oallouch.mongodoc.tree.node.AbstractNode;
import com.oallouch.mongodoc.tree.node.WithValueNode;
import com.oallouch.mongodoc.tree.node.WithValueNode.SpecialValue;
import com.oallouch.mongodoc.util.FXUtils;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener.Change;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeTableView.TreeTableViewSelectionModel;
import javafx.scene.input.InputEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class FloatingButtonBars {
	/** manually found */
	private DocumentTree documentTree;
	private TreeTableView<AbstractNode> treeTable;
	private TreeItem<AbstractNode> selectedItem;
	private TreeItem<AbstractNode> selectedContainerItem;
	private HBox containerButtonBar;
	/** even if there's 1 button, lineButtonBar is usefull to center it */
	private HBox lineButtonBar;
	
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
			addValue("");
		});
		//-- add properties --//
		Button addProperties = createButton("+{}", "Add a Document", e -> {
			if (selectedContainerItem == null) {
				return;
			}
			addValue(SpecialValue.properties);
		});
		//-- add array --//
		Button addArray = createButton("+[]", "Add a List", e -> {
			if (selectedContainerItem == null) {
				return;
			}
			addValue(SpecialValue.array);
		});
		
		containerButtonBar = new HBox(addPrimitive, addProperties, addArray);
		containerButtonBar.setAlignment(Pos.CENTER);
		containerButtonBar.getStyleClass().add("treeTableFloatingButtonBarHBox");
		containerButtonBar.setVisible(false);
		documentTree.getChildren().add(containerButtonBar);
		
		//--------------------------------------------------------------------//
		//-------------------------- Remove Button ---------------------------//
		//--------------------------------------------------------------------//
		Button removeNodeButton = createButton("X", "Remove", e -> {
			WithValueNode selectedNode = (WithValueNode) selectedItem.getValue();
			selectedNode.remove();
			treeTable.requestFocus(); // the button stole it
			treeTable.fireEvent(new InputEvent(DocumentEditor.MODIFIED));
		});
		lineButtonBar = new HBox(removeNodeButton);
		lineButtonBar.setAlignment(Pos.CENTER);
		lineButtonBar.setLayoutX(3);
		lineButtonBar.setVisible(false);
		documentTree.getChildren().add(lineButtonBar);
		
		//--------------------------------------------------------------------//
		//---------------------------- Listeners -----------------------------//
		//--------------------------------------------------------------------//
		treeTable.getSelectionModel().selectedItemProperty().addListener(this::selectionChanged);
		// hides the button bar when editing
		treeTable.editingCellProperty().addListener((observableValue, oldPosition, newPosition) -> {
			if (newPosition != null) {
				containerButtonBar.setVisible(false);
			}
		});
		// layout listener (for the scroll) on the VirtualFlow, which isn't created yet
		treeTable.getChildrenUnmodifiable().addListener((Change<? extends Node> change) -> {
			while (change.next()) {
				for (Node node : change.getAddedSubList()) {
					if (node instanceof VirtualFlow) {
						((VirtualFlow) node).needsLayoutProperty().addListener((ov, oldValue, newValue) -> {
							if (!newValue) {
								layoutButtons();
							}
						});
						// if we listen to the VirtualScrollBar, we get the event too early and the rest of the TreeTableView hasn't scrolled yet
					}
				}
			}
		});
	}
	
	private Button createButton(String text, String tooltipText, EventHandler<ActionEvent> actionListener) {
		Button button = new Button(text);
		button.setOnAction(actionListener);
		button.setTooltip(new Tooltip(tooltipText));
		button.getStyleClass().add("floatingButton");
		return button;
	}
	
	private void selectionChanged(ObservableValue<? extends TreeItem<AbstractNode>> observable, TreeItem<AbstractNode> oldValue, TreeItem<AbstractNode> newValue) {
		if (newValue == null) {
			selectedItem = null;
			selectedContainerItem = null;
			hideToolBars();
		} else {
			
			//----------------- visibility ----------------------//
			AbstractNode selectedNode = newValue.getValue();
			selectedItem = selectedNode.getTreeItem();
			if (!(selectedNode instanceof WithValueNode)) {
				lineButtonBar.setVisible(false);
			} else {
				lineButtonBar.setVisible(true);
			}
			
			containerButtonBar.setVisible(true);
			
			layoutButtons();
		}
	}
	
	private void hideToolBars() {
		containerButtonBar.setVisible(false);
		lineButtonBar.setVisible(false);
	}
	
	private void layoutButtons() {
		if (selectedItem == null) {
			return;
		}
		double headerHeight = FXUtils.getTableHeaderRow(treeTable).getHeight();
		AbstractNode selectedNode = selectedItem.getValue();
		if (lineButtonBar.isVisible()) {
			//-- y --//
			TreeTableRow<AbstractNode> selectedRow = documentTree.getTreeTableRow(selectedItem);
			lineButtonBar.setPrefHeight(selectedRow.getHeight());
			lineButtonBar.setLayoutY(
				headerHeight
				+ selectedRow.getBoundsInParent().getMinY()
				+ selectedRow.getBaselineOffset());
		}

		//---------------- horizontal bar position -----------------------//
		if (containerButtonBar.isVisible()) {
			selectedContainerItem = selectedNode.findPropertiesOrArray().getTreeItem();
			containerButtonBar.setVisible(true);
			TreeTableRow<AbstractNode> containerRow = documentTree.getTreeTableRow(selectedContainerItem);
			NameColumnCell nameCell = FXUtils.getCellOfType(containerRow, NameColumnCell.class);
			//---------------- x ---------------//
			Text nameText = FXUtils.getChildOfType(nameCell, Text.class); // there can also be an arrow (the disclosure node)
			containerButtonBar.setLayoutX(nameText.getBoundsInParent().getMaxX()
				+ 10); // maybe a treeTable insert (plus a gap)
			//---------------- y ---------------//
			containerButtonBar.setPrefHeight(containerRow.getHeight());
			containerButtonBar.setLayoutY(
				headerHeight
				+ containerRow.getBoundsInParent().getMinY()
				+ containerRow.getBaselineOffset());
		}
	}
	
	private void addValue(Object value) {
		int index;
		if (selectedContainerItem == selectedItem) {
			index = -1;
		} else {
			index = selectedContainerItem.getChildren().indexOf(selectedItem) + 1;
		}
		AbstractNode addedNode = selectedContainerItem.getValue().insert(value, index);
		addedNode.select();
		treeTable.requestFocus(); // the button bar stole it
		treeTable.fireEvent(new InputEvent(DocumentEditor.MODIFIED));
	}
}
