package com.oallouch.mongodoc.tree.cell;

import com.oallouch.mongodoc.tree.TreeItemFactory;
import com.oallouch.mongodoc.tree.node.AbstractNode;
import com.oallouch.mongodoc.tree.node.PropertyNode;
import com.oallouch.mongodoc.util.JsonUtils;
import java.util.Collections;
import java.util.Map;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public abstract class AbstractCell extends TreeTableCell<AbstractNode, Object> {
	protected static final DataFormat DRAG_DROP_DATA_FORMAT = new DataFormat("serialized json");
	protected static final DataFormat DRAG_DROP_DATA_FORMAT_WITH_NAME = new DataFormat("serialized json with name");
	protected static final DataFormat DRAG_DROP_DATA_FORMAT_WRAPPED = new DataFormat("serialized json in an Object Literal");
	
	public AbstractCell() {
		setOnDragDetected(mouseEvent -> {
			AbstractNode node = getAbstractNode();
			//-- value -> Map -> (later) json --//
			// jsonValue can be a Map, a List or a primitive (or wrapper)
			Object jsonValue = TreeItemFactory.toJsonValue(node.getTreeItem());
			Map<String, Object> jsonObject;
			DataFormat dataFormat;
			if (node instanceof PropertyNode) {
				dataFormat = DRAG_DROP_DATA_FORMAT_WITH_NAME;
				String name = ((PropertyNode) node).getName();
				jsonObject = Collections.singletonMap(name, jsonValue);
			} else if (jsonValue instanceof Map) {
				dataFormat = DRAG_DROP_DATA_FORMAT;
				jsonObject = (Map) jsonValue;
			} else {
				dataFormat = DRAG_DROP_DATA_FORMAT_WRAPPED;
				jsonObject = Collections.singletonMap("wrappedValue", jsonValue);
			}
			String jsonText = JsonUtils.toJsonText(jsonObject);
			
			//-- in dragboard --//
			Dragboard dragboard = startDragAndDrop(mouseEvent.isControlDown() ? TransferMode.COPY : TransferMode.MOVE);
			dragboard.setContent(Collections.singletonMap(dataFormat, jsonText));
			mouseEvent.consume();
		});
		
		setOnDragOver(dragEvent -> {
			if (!getAbstractNode().isEndNode()) {
				dragEvent.acceptTransferModes(TransferMode.MOVE, TransferMode.COPY);
			}
			dragEvent.consume();
		});
		
		setOnDragDropped(dragEvent -> {
			String propertyName = null;
			Object jsonValue;
			String jsonText = (String) dragEvent.getDragboard().getContent(DRAG_DROP_DATA_FORMAT);
			if (jsonText == null) {
			}
			if (jsonText != null) {
				jsonValue = JsonUtils.toJsonObject(jsonText);
			} else {
				jsonText = (String) dragEvent.getDragboard().getContent(DRAG_DROP_DATA_FORMAT_WITH_NAME);
				if (jsonText != null) {
					Map.Entry<String, Object> jsonEntry = JsonUtils.toJsonObject(jsonText).entrySet().iterator().next();
					propertyName = jsonEntry.getKey();
					jsonValue = jsonEntry.getValue();
				} else {
					jsonText = (String) dragEvent.getDragboard().getContent(DRAG_DROP_DATA_FORMAT_WRAPPED);
					jsonValue = JsonUtils.toJsonObject(jsonText).values().iterator().next();
				}
			}
			AbstractNode node = getAbstractNode();
			if (node instanceof PropertyNode) {
				TreeItem<AbstractNode> treeItem   = getTreeItem();
				TreeItem<AbstractNode> parentItem = treeItem.getParent();
				int index = parentItem.getChildren().indexOf(parentItem);
				parentItem.getValue().insert(jsonValue, index);
				System.out.println("insert at: " + index);
				//node.insert(jsonValue, index);
			}
			dragEvent.consume();
		});
	}
	
	protected AbstractNode getAbstractNode() {
		return getTreeTableRow().getItem();
	}
	protected TreeItem<AbstractNode> getTreeItem() {
		return getAbstractNode().getTreeItem();
	}
}
