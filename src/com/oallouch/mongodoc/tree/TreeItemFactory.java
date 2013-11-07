package com.oallouch.mongodoc.tree;

import com.google.common.collect.Maps;
import com.oallouch.mongodoc.tree.node.AbstractNode;
import com.oallouch.mongodoc.tree.node.ArrayElementNode;
import com.oallouch.mongodoc.tree.node.ArrayEndNode;
import com.oallouch.mongodoc.tree.node.NodeTreeItem;
import com.oallouch.mongodoc.tree.node.PropertiesEndNode;
import com.oallouch.mongodoc.tree.node.PropertyNode;
import com.oallouch.mongodoc.tree.node.RootNode;
import com.oallouch.mongodoc.tree.node.WithValueNode;
import com.oallouch.mongodoc.tree.node.WithValueNode.SpecialValue;
import com.oallouch.mongodoc.util.FXUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;

/**
 * Why we use a node structure:
 * We don't want a Map or a List as a TreeItem  because we want the TreeTable structure
 *   to be the Collections. We don't want to have to update an underlying Map or List
 *   whenever a TreeItem is dragged.
 */
public class TreeItemFactory {
	//------------------------------------------------------------------------//
	//---------------------- json Object => TreeItem -------------------------//
	//------------------------------------------------------------------------//
	public static RootNode createRootTreeItem(TreeTableView<AbstractNode> treeTable, Map<String, ?> jsonObject, TreeItem root) {
		RootNode rootNode = new RootNode(treeTable);
		NodeTreeItem rootTreeItem = new NodeTreeItem(rootNode);
		createPropertyTreeItems(jsonObject, rootTreeItem);
		root.getChildren().add(rootTreeItem);
		root.getChildren().add(new TreeItem<>(new PropertiesEndNode()));
		return rootNode;
	}
	
	public static PropertyNode createPropertyTreeItem(String propertyName, Object jsonValue, TreeItem parent, int index) {
		PropertyNode propertyNode = new PropertyNode(propertyName, getNodeValue(jsonValue));
		NodeTreeItem propertyItem = new NodeTreeItem(propertyNode);
		addValueTreeItem(propertyItem, jsonValue, parent, index); // can be several items
		return propertyNode;
	}
	public static ArrayElementNode createArrayElementTreeItem(Object jsonValue, TreeItem parent, int index) {
		ArrayElementNode arrayElementNode = new ArrayElementNode(getNodeValue(jsonValue));
		NodeTreeItem arrayElementItem = new NodeTreeItem(arrayElementNode);
		addValueTreeItem(arrayElementItem, jsonValue, parent, index);
		arrayElementNode.setIndexFromPrecedingSibling();
		return arrayElementNode;
	}
	
	private static void createArrayElementTreeItems(List jsonList, TreeItem parent) {
		for (Object arrayElement : jsonList) {
			createArrayElementTreeItem(arrayElement, parent, -1);
		}
	}
	private static void createPropertyTreeItems(Map<String, ?> jsonObject, TreeItem parent) {
		for (Map.Entry<String, ?> propertyEntry : jsonObject.entrySet()) {
			createPropertyTreeItem(propertyEntry.getKey(), propertyEntry.getValue(), parent, -1);
		}
	}
	
	
	private static Object getNodeValue(Object jsonValue) {
		if (jsonValue instanceof Map) {
			return SpecialValue.properties;
		} else if (jsonValue instanceof List) {
			return SpecialValue.array;
		} else {
			return jsonValue;
		}
	}
	
	/**
	 * also adds withValueTreeItem to the parent
	 * @param withValueTreeItem
	 * @param jsonValue
	 * @param parent 
	 */
	private static void addValueTreeItem(TreeItem<AbstractNode> withValueTreeItem, Object jsonValue, TreeItem parent, int index) {
		int indexOfClosing = FXUtils.addChild(parent, withValueTreeItem, index) ? index + 1 : -1;
		
		//-------------- the children ----------------//
		WithValueNode withValueNode = (WithValueNode) withValueTreeItem.getValue();
		if (withValueNode.isProperties()) {
			createPropertyTreeItems((Map<String, ?>) jsonValue, withValueTreeItem);
			FXUtils.addChild(parent, new NodeTreeItem(new PropertiesEndNode()), indexOfClosing);
		} else if (withValueNode.isArray()) {
			createArrayElementTreeItems((List) jsonValue, withValueTreeItem);
			FXUtils.addChild(parent, new NodeTreeItem(new ArrayEndNode()), indexOfClosing);
		}
	}
	
	//------------------------------------------------------------------------//
	//--------------------- json Object <==> TreeItem ------------------------//
	//------------------------------------------------------------------------//
	/**
	 * @param rootTreeItem contains a RootNode, not a WithValueNode
	 * @return 
	 */
	public static Map<String, Object> toJsonObject(TreeItem<AbstractNode> rootTreeItem) {
		return (Map<String, Object>) createJsonObject(rootTreeItem);
	}
	
	public static Object toJsonValue(TreeItem<AbstractNode> withValueTreeItem) {
		WithValueNode withValueNode = (WithValueNode) withValueTreeItem.getValue();
		Object value = withValueNode.getValue();
		ObservableList<TreeItem<AbstractNode>> childItems = withValueTreeItem.getChildren();
		if (value == SpecialValue.properties) {
			return createJsonObject(withValueTreeItem);
		} else if (value == SpecialValue.array) {
			List<Object> childJsonArray = new ArrayList<>(childItems.size());
			for (TreeItem<AbstractNode> arrayElementTreeItem : childItems) {
				if (arrayElementTreeItem.getValue() instanceof ArrayElementNode) {
					Object jsonValue = toJsonValue(arrayElementTreeItem);
					childJsonArray.add(jsonValue);
				}
			}
			return childJsonArray;
		} else {
			return value;
		}
	}
	
	private static Map<String, Object> createJsonObject(TreeItem<AbstractNode> parent) {
		Map<String, Object> childJsonObject = Maps.newLinkedHashMap();
		for (TreeItem<AbstractNode> propertyTreeItem : parent.getChildren()) {
			AbstractNode node = propertyTreeItem.getValue();
			if (node instanceof PropertyNode) {
				PropertyNode propertyNode = (PropertyNode) node;
				String name = propertyNode.getName();
				Object jsonValue = toJsonValue(propertyTreeItem);
				childJsonObject.put(name, jsonValue);
			}
		}
		return childJsonObject;
	}
}
