package com.oallouch.mongodoc.tree.node;

import com.google.common.collect.Lists;
import com.oallouch.mongodoc.tree.node.WithValueNode.SpecialValue;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public abstract class AbstractNode {
	private TreeItem<AbstractNode> treeItem;

	public TreeItem<AbstractNode> getTreeItem() {
		return treeItem;
	}
	public void setTreeItem(TreeItem treeItem) {
		this.treeItem = treeItem;
	}

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
	
	public AbstractNode getParent() {
		TreeItem<AbstractNode> parentItem = getTreeItem().getParent();
		return parentItem == null ? null : parentItem.getValue();
	}
	
	
	/**
	 * even if it returns true, it can also contain PropertiesendNodes or ArrayEndNodes
	 * @return 
	 */
	public boolean isContainsProperties() {
		if (this instanceof RootNode) {
			return true;
		}
		if (this instanceof WithValueNode) {
			WithValueNode withValueNode = (WithValueNode) this;
			return withValueNode.getValue() == SpecialValue.properties;
		}
		return false;
	}
	/**
	 * even if it returns true, it can also contain PropertiesendNodes or ArrayEndNodes
	 * @return 
	 */
	public boolean isContainsArrayElements() {
		if (this instanceof WithValueNode) {
			WithValueNode withValueNode = (WithValueNode) this;
			return withValueNode.getValue() == SpecialValue.array;
		}
		return false;
	}
	/**
	 * more complicated than isLeaf, but more node type may come in the future (like ObjectId)
	 * @return 
	 */
	public boolean isPropertiesOrArray() {
		return isContainsProperties() || isContainsArrayElements();
	}
	
	public boolean isEndNode() {
		return this instanceof AbstractEndNode;
	}
	
	public AbstractNode findPropertiesOrArray() {
		AbstractNode currentNode = this;
		while (currentNode != null) {
			if (currentNode.isPropertiesOrArray()) {
				return currentNode;
			}
			currentNode = currentNode.getParent();
		}
		return null;
	}
	
	public void insert(Object value, int index) {
		System.out.println("isContainsProperties: " + isContainsProperties());
		System.out.println("isContainsArrayElements: " + isContainsArrayElements());
		//-------------- nodes --------------//
		AbstractNode openingNode = isContainsProperties() ? new PropertyNode(value) : new ArrayElementNode(value);
		AbstractNode closingNode;
		if (value == SpecialValue.properties) {
			closingNode = new PropertiesEndNode();
		} else if (value == SpecialValue.array) {
			closingNode = new ArrayEndNode();
		} else { // value is a primitive
			closingNode = null;
		}
		//-------------- items --------------//
		System.out.println("openingNode: " + openingNode);
		System.out.println("closingNode: " + closingNode);
		NodeTreeItem openingItem = new NodeTreeItem(openingNode);
		NodeTreeItem closingItem = closingNode != null ? new NodeTreeItem(closingNode) : null;
		addOpeningAndClosing(openingItem, closingItem, index);
		
		if (isContainsArrayElements()) {
			//-- reindexing --//
			int childIndex = 0;
			for (TreeItem<AbstractNode> childItem : treeItem.getChildren()) {
				AbstractNode childNode = childItem.getValue();
				if (childNode instanceof ArrayElementNode) { // it can also be a PropertiesendNode or an ArrayEndNode
					((ArrayElementNode) childNode).setIndex(childIndex++);
				}
			}
		}
	}
	
	
	/**
	 * @param opening
	 * @param closing can be null
	 */
	private void addOpeningAndClosing(NodeTreeItem opening, NodeTreeItem closing, int index) {
		ObservableList<TreeItem<AbstractNode>> children = treeItem.getChildren();
		if (index < 0 || index >= children.size()) {
			if (closing != null) {
				children.addAll(opening, closing);
			} else {
				children.add(opening);
			}
		} else {
			if (closing != null) {
				children.addAll(index, Lists.newArrayList(opening, closing));
			} else {
				children.add(index, opening);
			}
		}
		treeItem.setExpanded(true);
	}
}
