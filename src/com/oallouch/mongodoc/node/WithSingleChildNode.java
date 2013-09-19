package com.oallouch.mongodoc.node;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * represents a JSON map value with a single child (the value)
 */
public abstract class WithSingleChildNode extends AbstractNode {
	private static final List<Class<? extends AbstractNode>> ACCEPTED_CHILDREN_TYPES = new ArrayList<>(1);

	static {
		ACCEPTED_CHILDREN_TYPES.add(EqualsValueNode.class);
		ACCEPTED_CHILDREN_TYPES.add(PropertiesNode.class);
	}

	@Override
	public AbstractNode addChild(AbstractNode child) {
		super.addChild(child);
		if (getChildCount() > 1) {
			setError("A property node can only have 1 child");
		}
		return this;
	}

    /*
     * Generate an error because these DBO are build by the parent of the property
     */
    @Override
    public Object getJsonElement() {
        throw new IllegalArgumentException("getDBO can't be called directly. The DBO is built by PropertiesQueryNode");
    }

	@Override
	public List<Class<? extends AbstractNode>> getAcceptedChildrenTypes() {
		return ACCEPTED_CHILDREN_TYPES;
	}
}