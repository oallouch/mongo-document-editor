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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

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
	public static void createRootTreeItem(Map<String, ?> jsonObject, TreeItem root) {
		NodeTreeItem rootTreeItem = new NodeTreeItem(new RootNode());
		createPropertyTreeItems(jsonObject, rootTreeItem);
		root.getChildren().add(rootTreeItem);
		root.getChildren().add(new TreeItem<>(new PropertiesEndNode()));
	}
	
	private static void createPropertyTreeItems(Map<String, ?> jsonObject, TreeItem parent) {
		for (Map.Entry<String, ?> propertyEntry : jsonObject.entrySet()) {
			createPropertyTreeItem(propertyEntry.getKey(), propertyEntry.getValue(), parent);
		}
	}
	private static void createPropertyTreeItem(String name, Object jsonValue, TreeItem parent) {
		PropertyNode propertyNode = new PropertyNode(name, getNodeValue(jsonValue));
		NodeTreeItem propertyItem = new NodeTreeItem(propertyNode);
		addValueTreeItem(propertyItem, jsonValue, parent); // can be several items
		addClosingItem(propertyNode, parent);
	}
	
	private static void createArrayElementTreeItems(List jsonList, TreeItem parent) {
		int index = 0;
		for (Object arrayElement : jsonList) {
			createArrayElementTreeItem(index, arrayElement, parent);
			index++;
		}
	}
	private static void createArrayElementTreeItem(int index, Object jsonValue, TreeItem parent) {
		ArrayElementNode arrayElementNode = new ArrayElementNode(getNodeValue(jsonValue));
		arrayElementNode.setIndex(index);
		NodeTreeItem arrayElementItem = new NodeTreeItem(arrayElementNode);
		addValueTreeItem(arrayElementItem, jsonValue, parent);
		addClosingItem(arrayElementNode, parent);
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
	
	private static void addClosingItem(WithValueNode withValueNode, TreeItem parent) {
		if (withValueNode.isContainsProperties()) {
			parent.getChildren().add(new NodeTreeItem(new PropertiesEndNode()));
		} else if (withValueNode.isContainsArrayElements()) {
			parent.getChildren().add(new NodeTreeItem(new ArrayEndNode()));
		}
	}
	
	/**
	 * also adds withValueTreeItem to the parent
	 * @param withValueTreeItem
	 * @param jsonValue
	 * @param parent 
	 */
	private static void addValueTreeItem(TreeItem<AbstractNode> withValueTreeItem, Object jsonValue, TreeItem parent) {
		parent.getChildren().add(withValueTreeItem);
		WithValueNode withValueNode = (WithValueNode) withValueTreeItem.getValue();
		
		//-------------- the children ----------------//
		if (withValueNode.isContainsProperties()) {
			createPropertyTreeItems((Map<String, ?>) jsonValue, withValueTreeItem);
		} else if (withValueNode.isContainsArrayElements()) {
			createArrayElementTreeItems((List) jsonValue, withValueTreeItem);
		}
	}
	
	//------------------------------------------------------------------------//
	//---------------------- json Object => TreeItem -------------------------//
	//------------------------------------------------------------------------//
	/**
	 * @param rootTreeItem contains a RootNode, not a WithValueNode
	 * @return 
	 */
	public static Map<String, Object> toJsonObject(TreeItem<AbstractNode> rootTreeItem) {
		return (Map<String, Object>) createJsonObject(rootTreeItem);
	}
	
	private static Object createJsonValue(TreeItem<AbstractNode> withValueTreeItem) {
		WithValueNode withValueNode = (WithValueNode) withValueTreeItem.getValue();
		Object value = withValueNode.getValue();
		ObservableList<TreeItem<AbstractNode>> childItems = withValueTreeItem.getChildren();
		if (value == SpecialValue.properties) {
			return createJsonObject(withValueTreeItem);
		} else if (value == SpecialValue.array) {
			List<Object> childJsonArray = new ArrayList<>(childItems.size());
			for (TreeItem<AbstractNode> arrayElementTreeItem : childItems) {
				if (arrayElementTreeItem.getValue() instanceof ArrayElementNode) {
					Object jsonValue = createJsonValue(arrayElementTreeItem);
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
				Object jsonValue = createJsonValue(propertyTreeItem);
				childJsonObject.put(name, jsonValue);
			}
		}
		return childJsonObject;
	}
}
