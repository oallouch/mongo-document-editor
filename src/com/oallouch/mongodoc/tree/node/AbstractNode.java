package com.oallouch.mongodoc.tree.node;

import com.google.common.base.Splitter;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Lists;
import com.oallouch.mongodoc.tree.node.WithValueNode.SpecialValue;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

/**
 * the iterator method iterates over non end nodes
 */
public abstract class AbstractNode implements Iterable<AbstractNode> {
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
	
	//------------------------------------------------------------------------//
	//------------------------- parents navigation ---------------------------//
	//------------------------------------------------------------------------//
	public AbstractNode getParent() {
		TreeItem<AbstractNode> parentItem = getTreeItem().getParent();
		return parentItem == null ? null : parentItem.getValue();
	}
	
	public RootNode getRootNode() {
		AbstractNode node = this;
		while (node != null) {
			if (node instanceof RootNode) {
				return (RootNode) node;
			}
			node = node.getParent();
		}
		return null;
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
	//------------------------------------------------------------------------//
	
	
	//------------------------------------------------------------------------//
	//------------------------------- path -----------------------------------//
	//------------------------------------------------------------------------//
	/**
	 * @return a LinkedList filled with: a RootNode and a mix of PropertyNodes and ArrayElementNodes
	 */
	public Iterable<AbstractNode> getPathFromRoot() {
		LinkedList<AbstractNode> parentList = new LinkedList<>();
		AbstractNode node = this;
		while (node != null) {
			parentList.add(0, node);
			node = node.getParent();
		}
		return parentList;
	}
	
	public String getPathStringFromRoot() {
		StringBuilder builder = new StringBuilder(100);
		for (AbstractNode node : getPathFromRoot()) {
			
			if (node instanceof PropertyNode) {
				builder.append('/').append(((PropertyNode) node).getName());
			} else if (node instanceof ArrayElementNode) {
				builder.append('[').append(((ArrayElementNode) node).getIndex()).append(']');
			} else { // RootNode
			}
		}
		return builder.toString();
	}
	
	private static Pattern BRACKET_PATTERN = Pattern.compile("\\[(\\d+)\\]");
	
	public AbstractNode getNodeFromPathString(String pathString) {
		AbstractNode node = getRootNode();
		for (String part : Splitter.on('/').omitEmptyStrings().split(pathString)) {
			int openingBracketIndex = part.indexOf('[');
			//-- property sub-part --//
			String propertyName = part.substring(0,
				openingBracketIndex != -1 ? openingBracketIndex : part.length());
			node = node.getPropertyNode(propertyName);
			//-- array element node(s) --//
			Matcher matcher = BRACKET_PATTERN.matcher(part);
			while (matcher.find()) {
				int index = Integer.parseInt(matcher.group(1));
				node = node.getArrayElementNode(index);
			}
		}
		return node;
	}
	//------------------------------------------------------------------------//
	
	//------------------------------------------------------------------------//
	//---------------- node type (properties, array, end node) ---------------//
	//------------------------------------------------------------------------//
	/**
	 * even if it returns true, it can also contain PropertiesendNodes or ArrayEndNodes
	 * @return 
	 */
	public boolean isProperties() {
		if (this instanceof RootNode) {
			return true;
		}
		if (this instanceof WithValueNode) {
			return ((WithValueNode) this).getValue() == SpecialValue.properties;
		}
		return false;
	}
	/**
	 * even if it returns true, it can also contain PropertiesendNodes or ArrayEndNodes
	 * @return 
	 */
	public boolean isArray() {
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
		return isProperties() || isArray();
	}
	
	public boolean isEndNode() {
		return this instanceof AbstractEndNode;
	}
	
	protected void checkIsProperties() {
		if (!isProperties()) {
			throw new IllegalArgumentException("To call this method, this node must be a RootNode or the value must be Special.properties.");
		}
	}
	
	protected void checkIsArray() {
		if (!isArray()) {
			throw new IllegalArgumentException("To call this method, the value must be Special.array.");
		}
	}
	//------------------------------------------------------------------------//
	
	
	//------------------------------------------------------------------------//
	//----- when value is SpecialValue.properties (or it's a RootNode) -------//
	//------------------------------------------------------------------------//
	/**
	 * only callable when the value is Special.properties or it's the RootNode
	 * @return 
	 */
	public String getNextDefaultPropertyName() {
		checkIsProperties();
		int index = 1;
		while (true) {
			String currentName = "property" + index++;
			if (containsPropertyName(currentName)) {
				// name found, let's try the next one
				continue;
			}
			return currentName;
		}
	}
	
	/**
	 * only callable when the value is Special.properties or it's the RootNode
	 * @param propertyName
	 * @return 
	 */
	public boolean containsPropertyName(String propertyName) {
		return getPropertyNode(propertyName) != null;
	}
	/**
	 * only callable when the value is Special.properties or it's the RootNode
	 * @param propertyName
	 * @return 
	 */
	public PropertyNode getPropertyNode(String propertyName) {
		checkIsProperties();
		for (AbstractNode childNode : this) {
			PropertyNode propertyNode = (PropertyNode) childNode;
			if (propertyName.equals(propertyNode.getName())) {
				// name found
				return propertyNode;
			}
		}
		return null;
	}
	//------------------------------------------------------------------------//
	
	
	//------------------------------------------------------------------------//
	//------------------- when value is SpecialValue.array -------------------//
	//------------------------------------------------------------------------//
	public void reindexArray() {
		checkIsArray();
		for (AbstractNode childNode : this) {
			((ArrayElementNode) childNode).setIndexFromPrecedingSibling();
		}
	}
	public ArrayElementNode getArrayElementNode(int index) {
		checkIsArray();
		for (AbstractNode node : this) {
			ArrayElementNode arrayElementNode = (ArrayElementNode) node;
			if ((arrayElementNode).getIndex() == index) {
				return arrayElementNode;
			}
		}
		return null;
	}
	//------------------------------------------------------------------------//
	
	
	//------------------------------------------------------------------------//
	//------------------------------ Iterators -------------------------------//
	//------------------------------------------------------------------------//
	@Override
	public Iterator<AbstractNode> iterator() {
		return nonEndNodeChildIterator();
	}
	public Iterator<AbstractNode> nonEndNodeChildIterator() {
		final Iterator<TreeItem<AbstractNode>> childIt = treeItem.getChildren().iterator();
		return new AbstractIterator<AbstractNode>() {
			@Override
			public AbstractNode computeNext() {
				while (childIt.hasNext()) {
					AbstractNode nextChild = childIt.next().getValue();
					if (!(nextChild instanceof AbstractEndNode)) {
						return nextChild;
					}
				}
				return endOfData();
			}
		};
	}
	
	public AbstractNode getPrecedingNonEndNodeSibling() {
		AbstractNode preceding = null;
		for (AbstractNode node : getParent()) {
			if (node == this) {
				return preceding;
			} else {
				preceding = node;
			}
		}
		return null;
	}
	//------------------------------------------------------------------------//
	
	/**
	 * @param value primitive value or SpecialValue (for properties or array)
	 * @param index
	 * @return
	 */
	public AbstractNode insert(Object value, int index) {
		//-------------- nodes --------------//
		AbstractNode openingNode = isProperties() ? new PropertyNode(value) : new ArrayElementNode(value);
		AbstractNode closingNode;
		if (value == SpecialValue.properties) {
			closingNode = new PropertiesEndNode();
		} else if (value == SpecialValue.array) {
			closingNode = new ArrayEndNode();
		} else { // value is a primitive
			closingNode = null;
		}
		//-------------- items --------------//
		NodeTreeItem openingItem = new NodeTreeItem(openingNode);
		NodeTreeItem closingItem = closingNode != null ? new NodeTreeItem(closingNode) : null;
		addOpeningAndClosing(openingItem, closingItem, index);
		
		if (isArray()) {
			//-- reindexing --//
			((WithValueNode) this).reindexArray();
		}
		
		return openingNode;
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
