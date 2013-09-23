package com.oallouch.mongodoc.node;

import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/*
 * A class used to set the keys of the query. A key can be
 * represented by an property node and the children are the
 * request on the key.
 * 
 * They can be indented each one so complexes query can
 * (hopefully) be built
 */
public class ArrayNode extends AbstractNode {
	private static final List<Class<? extends AbstractNode>> ACCEPTED_CHILDREN_TYPES = new ArrayList<>(1);

	static {
		ACCEPTED_CHILDREN_TYPES.add(ArrayItemNode.class);
	}
	
    /*
	 * . convinience method
     * . Appends a new array item to the list
     */
    public ArrayNode addArrayItem(AbstractNode nodeChild) {
        ArrayItemNode arrayNode = new ArrayItemNode();
		arrayNode.setIndex(getChildCount());
        arrayNode.addChild(nodeChild);
        addChild(arrayNode);
        return this;
    }

    /*
     * Chain the array and its children to make a List
     */
    @Override
    public Object getJsonElement() {
		List list = new ArrayList(getChildCount());
        for (AbstractNode child : getChildren()) {
			if(child.getChild(0) == null) {
                child.setError("A Array item value can't be null");
                continue;
            }
            list.add(child.getChild(0).getJsonElement());
        }
        return list;
    }

	@Override
	public List<Class<? extends AbstractNode>> getAcceptedChildrenTypes() {
		return ACCEPTED_CHILDREN_TYPES;
	}

    @Override
    public String toString() {
        return "Array container (" + getChildCount() + ")";
    }
}
