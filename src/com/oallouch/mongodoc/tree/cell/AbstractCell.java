package com.oallouch.mongodoc.tree.cell;

import com.google.common.collect.Maps;
import com.oallouch.mongodoc.DocumentEditor;
import com.oallouch.mongodoc.tree.TreeItemFactory;
import com.oallouch.mongodoc.tree.node.AbstractNode;
import com.oallouch.mongodoc.tree.node.PropertyNode;
import com.oallouch.mongodoc.tree.node.WithValueNode;
import com.oallouch.mongodoc.util.FXUtils;
import com.oallouch.mongodoc.util.JsonUtils;
import java.util.Collections;
import java.util.Map;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.InputEvent;
import javafx.scene.input.TransferMode;

/**
 * handles the drag and drop
 */
public abstract class AbstractCell extends TreeTableCell<AbstractNode, Object> {
	protected static final DataFormat DRAG_DROP_DATA_FORMAT_PROPERTY = new DataFormat("serialized json property");
	protected static final DataFormat DRAG_DROP_DATA_FORMAT_ARRAY_ELEMENT = new DataFormat("serialized json array element");
	protected static final DataFormat DRAG_DROP_DATA_FORMAT_PATH = new DataFormat("json path");
	
	public AbstractCell() {
		setOnDragDetected(mouseEvent -> {
			AbstractNode node = getAbstractNode();
			//-- value -> Map -> (later) json --//
			// jsonValue can be a Map, a List or a primitive (or wrapper)
			Object jsonValue = TreeItemFactory.toJsonValue(node.getTreeItem());
			Map<String, Object> jsonObject;
			DataFormat dataFormat;
			if (node instanceof PropertyNode) {
				dataFormat = DRAG_DROP_DATA_FORMAT_PROPERTY;
				String name = ((PropertyNode) node).getName();
				jsonObject = Collections.singletonMap(name, jsonValue);
			} else { // ArrayElementNode
				dataFormat = DRAG_DROP_DATA_FORMAT_ARRAY_ELEMENT;
				jsonObject = Collections.singletonMap("array value", jsonValue);
			}
			String jsonText = JsonUtils.toJsonText(jsonObject);
			
			//-- content --//
			Map<DataFormat, Object> content = Maps.newHashMapWithExpectedSize(2);
			content.put(DRAG_DROP_DATA_FORMAT_PATH, node.getPathStringFromRoot());
			content.put(dataFormat, jsonText);
			
			//-- in dragboard --//
			Dragboard dragboard = startDragAndDrop(mouseEvent.isControlDown() ? TransferMode.COPY : TransferMode.MOVE);
			dragboard.setContent(content);
			mouseEvent.consume();
		});
		
		setOnDragOver(dragEvent -> {
			if (!getAbstractNode().isEndNode()) {
				String sourcePath = (String) dragEvent.getDragboard().getContent(DRAG_DROP_DATA_FORMAT_PATH);
				String destPath = getAbstractNode().getPathStringFromRoot();
				if (!destPath.equals(sourcePath)) {
					dragEvent.acceptTransferModes(TransferMode.COPY);
					if (!destPath.startsWith(sourcePath)) {
						dragEvent.acceptTransferModes(TransferMode.MOVE);
					}
				}
			}
			dragEvent.consume();
		});
		
		setOnDragDropped(dragEvent -> {
			Dragboard dragboard = dragEvent.getDragboard();
			TreeItem<AbstractNode> treeItem = getTreeItem();
			AbstractNode node = treeItem.getValue();
			//----------------------------------------------------------------//
			//------------- jsonText -> propertyName and jsonValue -----------//
			//----------------------------------------------------------------//
			String propertyName = null;
			Object jsonValue;
			String jsonText = (String) dragboard.getContent(DRAG_DROP_DATA_FORMAT_PROPERTY);
			if (jsonText != null) {
				Map.Entry<String, Object> jsonEntry = JsonUtils.toJsonObject(jsonText).entrySet().iterator().next();
				propertyName = jsonEntry.getKey();
				jsonValue = jsonEntry.getValue();
			} else {
				jsonText = (String) dragboard.getContent(DRAG_DROP_DATA_FORMAT_ARRAY_ELEMENT);
				jsonValue = JsonUtils.toJsonObject(jsonText).values().iterator().next();
			}
			
			//----------------------------------------------------------------//
			//------------------- source removal when MOVE -------------------//
			//----------------------------------------------------------------//
			if (dragEvent.getTransferMode() == TransferMode.MOVE) {
				String path = (String) dragboard.getContent(DRAG_DROP_DATA_FORMAT_PATH);
				AbstractNode sourceNode = node.getNodeFromPathString(path);
				((WithValueNode) sourceNode).remove();
			}
			
			//----------------------------------------------------------------//
			//------------ propertyName and jsonValue -> TreeItems -----------//
			//----------------------------------------------------------------//
			TreeItem<AbstractNode> parentItem = treeItem.getParent();
			int index = FXUtils.getIndexInParent(treeItem);
			WithValueNode parentNode = (WithValueNode) parentItem.getValue();
			
			if (getAbstractNode() instanceof PropertyNode) {
				//-- propertyName --//
				if (propertyName == null || parentNode.containsPropertyName(propertyName)) {
					propertyName = parentNode.getNextDefaultPropertyName();
				}
				TreeItemFactory.createPropertyTreeItem(propertyName, jsonValue, parentItem, index)
					.select().getTreeItem().setExpanded(true);
			} else { // ArrayElementNode
				TreeItemFactory.createArrayElementTreeItem(jsonValue, parentItem, index)
					.select().getTreeItem().setExpanded(true);
				parentNode.reindexArray();
			}
			dragEvent.consume();
			
			this.fireEvent(new InputEvent(DocumentEditor.MODIFIED));
		});
	}
	
	protected AbstractNode getAbstractNode() {
		return getTreeTableRow().getItem();
	}
	protected TreeItem<AbstractNode> getTreeItem() {
		return getAbstractNode().getTreeItem();
	}
}
