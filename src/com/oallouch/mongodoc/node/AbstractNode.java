package com.oallouch.mongodoc.node;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/*
 * An abstract class that encapsulate the necessary methods who can
 * be used to build a query like a tree.
 * 
 * They can be derived into different kind of node, each one have
 * a parent so it is possible to make some "top" node wich can be used
 * with "sub" node.
 */
public abstract class AbstractNode {
    private AbstractNode parent;
	private ObservableList<AbstractNode> children = FXCollections.observableArrayList();
	/**
	 * . contains the error message
	 * . null is no error
	 */
	private StringProperty error = new SimpleStringProperty();

	public abstract List<Class<? extends AbstractNode>> getAcceptedChildrenTypes();

	public boolean acceptChild(AbstractNode childNode) {
		if (childNode == null) {
			return false;
		}
		for (Class<? extends AbstractNode> acceptedType : getAcceptedChildrenTypes()) {
			if (acceptedType.isAssignableFrom(acceptedType)) {
				return true;
			}
		}
		return false;
	}

	public void checkChild(AbstractNode childNode) {
		if (!acceptChild(childNode)) {
			String message = "This node can't have a child of type: " + childNode.getClass().getSimpleName()
				+ " (accepted type(s): "
				+ Joiner.on(", ").join(Iterables.transform(getAcceptedChildrenTypes(), new Function<Class, String>() {
					@Override
					public String apply(Class clazz) {
						return clazz.getSimpleName();
					}
				}))
				+ ")";
			throw new IllegalArgumentException(message);
		}
	}

    /*
     * Return the current value stored in the node
     */
    public abstract Object getJsonElement();

    /*
     * Add a child to the node.
     */
    public AbstractNode addChild(AbstractNode child) {
		checkChild(child);
        children.add(child);
		child.setParent(this);
        return this;
    }
    
    public void removeChild(AbstractNode child) {
        if(children == null) return;
        children.remove(child);
    }

    /*
     * Return the numbers of children of the node
     */
    public int getChildCount() {
        return children == null ? 0 : children.size();
    }

    /*
     * Return the children's list
     */
    public ObservableList<AbstractNode> getChildren() {
        return children;
    }
	public AbstractNode getChild(int index) {
		return getChildCount() >= (index + 1) ? children.get(index) : null;
	}

    /*
     * Return the parent of this node
     */
    public AbstractNode getParent() {
        return parent;
    }

    /*
     * Set the parent of the node
     */
    public AbstractNode setParent(AbstractNode parent) {
        this.parent = parent;
        return this;
    }

    public String getError() {
        return error.get();
    }
    public AbstractNode setError(String value) {
		error.set(value);
        return this;
    }
	public StringProperty errorProperty() {
		return error;
	}

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
