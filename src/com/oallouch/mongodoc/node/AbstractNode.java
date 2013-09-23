package com.oallouch.mongodoc.node;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public abstract class AbstractNode {
	/**
	 * . contains the error message
	 * . null is no error
	 */
	private StringProperty error = new SimpleStringProperty();

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
