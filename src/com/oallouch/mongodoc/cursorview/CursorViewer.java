package com.oallouch.mongodoc.cursorview;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.oallouch.mongodoc.tree.DocumentTree;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener.Change;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import static javafx.scene.layout.Region.USE_PREF_SIZE;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class CursorViewer extends Pane {
	private static final int EXPAND_BUTTON_WIDTH = 25;
	private VBox linesContainer;
	private Pane linesContainerScroll;
	
	public CursorViewer(DBCursor cursor) {
		
		//------------ DBCursor => List -------------//
		int remaining = 60;
		List<DBObject> documents = new ArrayList<>(remaining);
		while (cursor.hasNext() && remaining-- > 0) {
			documents.add(cursor.next());
		}
		
		//------------- PropertyNames ---------------//
		LinkedHashSet<String> propertyNames = Sets.newLinkedHashSetWithExpectedSize(60);
		for (DBObject document : documents) {
			for (String propertyName : document.keySet()) {
				propertyNames.add(propertyName);
			}
			if (propertyNames.size() >= 50) {
				// enough columns !
				break;
			}
		}
		
		//------- Table header with columns --------//
		Map<String, TableColumn> columnByPropertyName = Maps.newHashMapWithExpectedSize(propertyNames.size());
		TableView table = new TableView();
		table.setFocusTraversable(false);
		table.setEditable(false);
		getChildren().add(table);
		
		for (String propertyName : propertyNames) {
			TableColumn column = new TableColumn(propertyName);
			column.setSortable(false);
			table.getColumns().add(column);
			columnByPropertyName.put(propertyName, column);
		}
		table.getChildrenUnmodifiable().addListener((Change<? extends Node> c) -> {
			while (c.next()) {
				List<? extends Node> addedNodes = c.getAddedSubList();
				for (Node node : addedNodes) {
					if (node instanceof TableHeaderRow) {
						TableHeaderRow tableHeaderRow = (TableHeaderRow) node;
						linesContainerScroll.layoutYProperty().bind(tableHeaderRow.heightProperty());
						linesContainerScroll.prefHeightProperty().bind(
							Bindings.subtract(heightProperty(), tableHeaderRow.heightProperty()));
					}
				}
			}
		});
		table.setLayoutX(EXPAND_BUTTON_WIDTH);
		table.prefWidthProperty().bind(Bindings.subtract(widthProperty(), EXPAND_BUTTON_WIDTH));
		table.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
		
		//----------------- Lines -------------------//
		this.linesContainer = new VBox();
		linesContainer.getStyleClass().add("cursorViewerLinesContainer");
		this.linesContainerScroll = new HBox(linesContainer);//ScrollPane(linesContainer);
		//linesContainerScroll.setFocusTraversable(false);
		linesContainerScroll.prefWidthProperty().bind(widthProperty());
		getChildren().add(linesContainerScroll);
		for (DBObject document : documents) {
			HBox line = new HBox();
			DocumentInTable documentInTable = new DocumentInTable();
			documentInTable.document = document;
			documentInTable.tableLine = line;
			documentInTable.expanded = false;
			
			//-- expand button --//
			ToggleButton plus = new ToggleButton("+");
			plus.getStyleClass().add("cursorViewerExpandButton");
			//plus.setTranslateY(1);
			plus.setPrefSize(EXPAND_BUTTON_WIDTH, EXPAND_BUTTON_WIDTH);
			plus.setOnMouseClicked(mouseEvent -> documentInTable.toggleExpanded());
			StackPane plusContainer = new StackPane(plus);
			plusContainer.getStyleClass().add("cursorViewerExpandButtonContainer");
			plusContainer.setPrefSize(EXPAND_BUTTON_WIDTH, EXPAND_BUTTON_WIDTH);
			line.getChildren().add(plusContainer);
			
			//-- labels --//
			for (String propertyName : propertyNames) {
				// can't be a label because of the bug https://javafx-jira.kenai.com/browse/RT-33656#comment-369359
				Label label = new Label();
				label.setFocusTraversable(true);
				label.setOnMouseClicked(mouseEvent -> {
					// we don't use label to make the lambda faster (shared instance)
					((Label) mouseEvent.getSource()).requestFocus();
				});
				//TextField label = new TextField();
				//label.setEditable(false); // doesn't work when set in the css
				label.getStyleClass().add("cursorViewerCell");
				Object value = document.get(propertyName);
				if (value != null) {
					label.setText(value.toString());
				}
				line.getChildren().add(label);
				label.prefWidthProperty().bind(columnByPropertyName.get(propertyName).widthProperty());
			}
			linesContainer.getChildren().add(line);
		}
	}
	
	private class DocumentInTable {
		DBObject document;
		HBox tableLine;
		DocumentTree documentTree;
		boolean expanded;
		
		public void toggleExpanded() {
			if (!expanded) {
				this.documentTree = new DocumentTree(false);
				documentTree.setMinHeight(USE_PREF_SIZE);
				documentTree.setMaxHeight(USE_PREF_SIZE);
				documentTree.heightWhenAllVisibleProperty().addListener((observable, oldValue, newValue) -> {
					documentTree.setPrefHeight(newValue.doubleValue());
					Platform.runLater(linesContainer::requestLayout); // must be a JavaFX bug
				});
				documentTree.setRootJsonObject((BasicDBObject) document);
				int lineIndex = linesContainer.getChildren().indexOf(tableLine);
				linesContainer.getChildren().add(lineIndex + 1, documentTree);
				expanded = true;
			} else {
				linesContainer.getChildren().remove(documentTree);
				documentTree = null;
				expanded = false;
			}
		}
	}
}
