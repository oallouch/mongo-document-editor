package com.oallouch.mongodoc.cursorview;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.oallouch.mongodoc.tree.DocumentTree;
import com.oallouch.mongodoc.util.ClippedContainer;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener.Change;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import static javafx.scene.layout.Region.USE_PREF_SIZE;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class CursorViewer extends Pane {
	private static final int EXPAND_BUTTON_WIDTH = 25;
	private VBox lineList;
	private ScrollBar scrollBar;
	private BorderPane bottomPart;
	
	public CursorViewer(DBCursor cursor, int limit) {
		
		//------------ DBCursor => List -------------//
		int remaining = Math.min(limit, 100);
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
		table.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
		
		//----------------- Lines -------------------//
		this.lineList = new VBox();
		lineList.getStyleClass().add("cursorViewerLinesContainer");
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
			lineList.getChildren().add(line);
		}
		
		//--------------- ScrollBar -----------------//
		scrollBar = new ScrollBar();
		scrollBar.setOrientation(Orientation.VERTICAL);
		scrollBar.setMin(0);
		lineList.layoutYProperty().bind(scrollBar.valueProperty().multiply(-1));
		getChildren().add(scrollBar);
		
		//------------------------- layout --------------------------//
		table.setLayoutX(EXPAND_BUTTON_WIDTH);
		table.prefWidthProperty().bind(Bindings
			.subtract(widthProperty(), EXPAND_BUTTON_WIDTH)
			.subtract(scrollBar.widthProperty()));
		
		this.bottomPart = new BorderPane();
		//-- ClippedContainer --//
		ClippedContainer lineListContainer = new ClippedContainer(lineList);
		lineListContainer.prefWidthProperty().bind(lineList.widthProperty());
		lineListContainer.prefHeightProperty().bind(bottomPart.heightProperty());
		
		scrollBar.maxProperty().bind(lineList.heightProperty().subtract(lineListContainer.heightProperty()));
		// JavaFX bug
		Platform.runLater(() -> {
			scrollBar.maxProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue.doubleValue() < oldValue.doubleValue() && scrollBar.getValue() > newValue.doubleValue()) {
					scrollBar.setValue(newValue.doubleValue());
				}
			});
		});
		/*scrollBar.valueProperty().addListener((observable, oldValue, newValue) -> {
			System.out.println("new Value: " + newValue);
		});*/
		
		bottomPart.setCenter(lineListContainer);
		bottomPart.setRight(scrollBar);
		
		BorderPane.setAlignment(lineListContainer, Pos.TOP_CENTER);
		
		bottomPart.prefWidthProperty().bind(widthProperty());
		bottomPart.setMinHeight(USE_PREF_SIZE);
		bottomPart.setMaxHeight(USE_PREF_SIZE);
		table.getChildrenUnmodifiable().addListener((Change<? extends Node> c) -> {
			while (c.next()) {
				List<? extends Node> addedNodes = c.getAddedSubList();
				for (Node node : addedNodes) {
					if (node instanceof TableHeaderRow) {
						TableHeaderRow tableHeaderRow = (TableHeaderRow) node;
						bottomPart.layoutYProperty().bind(tableHeaderRow.heightProperty());
						bottomPart.prefHeightProperty().bind(
							Bindings.subtract(heightProperty(), tableHeaderRow.heightProperty()));
					}
				}
			}
		});
		getChildren().add(bottomPart);
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
					Platform.runLater(lineList::requestLayout); // must be a JavaFX bug
				});
				documentTree.setRootJsonObject((BasicDBObject) document);
				int lineIndex = lineList.getChildren().indexOf(tableLine);
				lineList.getChildren().add(lineIndex + 1, documentTree);
				expanded = true;
			} else {
				lineList.getChildren().remove(documentTree);
				documentTree = null;
				expanded = false;
			}
		}
	}
}
