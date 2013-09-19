package com.oallouch.mongodoc.node;

import com.oallouch.mongodoc.util.db.DBO;
import java.util.ArrayList;
import java.util.List;


/*
 * A class used to set the keys of the query. A key can be
 * represented by an property node and the children are the
 * request on the key.
 * 
 * They can be indented each one so complexes query can
 * (hopefully) be built
 */
public class PropertiesNode extends AbstractNode {
	private static final List<Class<? extends AbstractNode>> ACCEPTED_CHILDREN_TYPES = new ArrayList<>(1);

	static {
		ACCEPTED_CHILDREN_TYPES.add(PropertyNode.class);
	}
	
    /*
	 * . convinience method
     * . Appends a new property to the list
     */
    public PropertiesNode addProperty(String name, AbstractNode nodeChild) {
        // TODO check object type for nodeChild?
        PropertyNode propNode = new PropertyNode();
        propNode.setName(name);
        propNode.addChild(nodeChild);
        addChild(propNode);
        return this;
    }

    /*
     * Chain the properties and their children to make the right DBO
     */
    @Override
    public Object getDBOValue() {
        int count = getChildCount();
        //------------------------ 0 ------------------------//
        if (count == 0) {
            // Can't be empty
            throw new IllegalStateException("No PropertyNode");
        }
      
        DBO dbo = new DBO();
        for (AbstractNode child : getChildren()) {
            PropertyNode prop = (PropertyNode) child;
			if(prop.getChild(0) == null) {
                prop.setError("A Property value can't be null");
                continue;
            }
            dbo.put(prop.getName(), prop.getChild(0).getDBOValue());
        }
        return dbo;
    }

	@Override
	public List<Class<? extends AbstractNode>> getAcceptedChildrenTypes() {
		return ACCEPTED_CHILDREN_TYPES;
	}

    @Override
    public String toString() {
        return "Properties container (" + getChildCount() + ")";
    }
}
