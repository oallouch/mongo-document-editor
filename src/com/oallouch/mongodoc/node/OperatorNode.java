package com.oallouch.mongodoc.node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/*
 * This class represent a comparison in a mongo query.
 * The OperatorsNode reading can help.
 */
public class OperatorNode extends WithSingleChildNode {
    /*
     * Enum that represent the Operators commands
     */
    public static enum Operator {
        ALL                 ("$all"),
        GREATER             ("$gt"),
        GREATER_OR_EQUAL    ("$gte"),
        IN                  ("$in"),
        LESS                ("$lt"),
        LESS_OR_EQUAL       ("$lte"),
        NOT_EQUAL           ("$ne"),
        NOT_IN              ("$nin");
        private String op;

        private Operator(String str) {
            this.op = str;
        }

        public String getOp() {
            return op;
        }
    }

	public static final ObservableList<Operator> OPERATOR_LIST = FXCollections.observableArrayList(Arrays.asList(Operator.values()));

    private Operator operator;

    public OperatorNode() {
        super();
        operator = Operator.NOT_EQUAL;
    }

	public OperatorNode(String operatorProperty) {
		this(toOperator(operatorProperty));
	}
    public OperatorNode(Operator operator) {
        setOperator(operator);
    }

	/**
	 * @param property must start with '$'
	 * @return
	 */
	public static Operator toOperator(String property) {
		for (Operator operator : Operator.values()) {
			if (operator.getOp().equals(property)) {
				return operator;
			}
		}
		return null;
	}

    @Override
    public Object getJsonElement() {
        throw new IllegalArgumentException("getJsonElement can't be called directly. The json element is built by OperatorsNode");
    }

    public Operator getOperator() {
        return operator;
    }

    public OperatorNode setOperator(Operator operator) {
        this.operator = operator;
        return this;
    }

    @Override
    public String toString() {
        return operator.toString();
    }
}
