package com.oallouch.mongodoc.tree;

import com.oallouch.mongodoc.node.AbstractNode;

public interface DocumentTreeObserver {
    public void updateQuery(AbstractNode value);
}
