package com.oallouch.mongodoc.ui.module;

import com.google.common.collect.ImmutableMap;
import com.oallouch.mongodoc.node.AbstractNode;
import com.oallouch.mongodoc.node.OperatorNode;
import com.oallouch.mongodoc.node.OperatorNode.Operator;
import com.oallouch.mongodoc.node.PropertiesNode;
import com.oallouch.mongodoc.node.PropertyNode;
import com.oallouch.mongodoc.node.EqualsValueNode;
import com.oallouch.mongodoc.node.WithSingleChildNode;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

public class AbstractTreeTableCell extends TreeTableCell<AbstractNode, Object> {
	private static final ImmutableMap<Class<? extends AbstractNode>, StringConverter<? extends AbstractNode>> STRING_CONVERTERS = ImmutableMap.<Class<? extends AbstractNode>, StringConverter<? extends AbstractNode>>builder()
		//-- PropertiesNode --//
		.put(PropertiesNode.class, new StringConverter<PropertiesNode>() {
			@Override public String toString(PropertiesNode node) { return "{"; }
			@Override public PropertiesNode fromString(String string) { return null; }
		})
		//-- PropertyNode --//
		.put(PropertyNode.class, new StringConverter<PropertyNode>() {
			@Override public String toString(PropertyNode node) { return node.getName(); }
			@Override public PropertyNode fromString(String string) { return null; }
		})
		//-- EqualsValueNode --//
		.put(EqualsValueNode.class, new StringConverter<EqualsValueNode>() {
			@Override public String toString(EqualsValueNode node) {
				return node == null ? null : node.getEditingString();
			}
			@Override public EqualsValueNode fromString(String string) { return null; }
		}).build();

	public static final StringConverter<Operator> OPERATOR_STRING_CONVERTER = new StringConverter<Operator>() {
		@Override public String toString(Operator operator) {
			return operator.name(); // can't add semicolon here because it's used by the combo itself
		}
		@Override public Operator fromString(String name) {
			name = removeEndingColon(name);
			return Operator.valueOf(name);
		}
	};



    private ComboBox<Operator> operatorComboBox;
    private TextField textField;

	private Node currentGraphic;
	private StringConverter currentStringConverter;
	private ChangeListener focusListener;

	public AbstractTreeTableCell() {
		focusListener = new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (!newValue) {
					if (isEditing()) {
						//cancelEdit();
					}
				}
			}
		};
		setEditable(true);
	}

	protected AbstractNode getNode() {
		TreeItem<AbstractNode> treeItem = getTreeTableView().getTreeItem(getIndex());
		return treeItem == null ? null : treeItem.getValue();
	}

	protected ComboBox getOperatorComboBoxCreated() {
		if (operatorComboBox == null) {
			// inspired by CellUtils.createComboBox
			operatorComboBox = new ComboBox<>(OperatorNode.OPERATOR_LIST);
			operatorComboBox.setConverter(OPERATOR_STRING_CONVERTER);
			operatorComboBox.setMaxWidth(Double.MAX_VALUE);
			operatorComboBox.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					if (isEditing()) {
						OperatorNode operatorNode = (OperatorNode) getNode();
						operatorNode.setOperator(operatorComboBox.getValue());
						cancelEdit();
					}
				}
			});
			operatorComboBox.setEditable(false);
			operatorComboBox.focusedProperty().addListener(focusListener);
		}
		return operatorComboBox;
	}

	protected TextField getTextField() {
		if (textField == null) {
			// inspired by CellUtils.createTextField
			System.out.println("getTextField, hashcode" + this.hashCode());
			textField = new TextField(currentStringConverter.toString(getNode()));
			textField.setOnKeyReleased(new EventHandler<KeyEvent>() {
				@Override public void handle(KeyEvent t) {
					if (t.getCode() == KeyCode.ENTER) {
						String text = textField.getText();
						AbstractNode node = getNode();
						if (node instanceof EqualsValueNode) {
							((EqualsValueNode) node).setEditingString(text);
						} else {
							text = removeEndingColon(text); // in case the user enters ":" or " :" at the end
							((PropertyNode) node).setName(text);
						}
						cancelEdit();
					} else if (t.getCode() == KeyCode.ESCAPE) {
						cancelEdit();
					}
				}
			});
			textField.focusedProperty().addListener(focusListener);
		}
		return textField;
	}

	@Override
    public void startEdit() {
		System.out.println("start edit");
		//-- editable ? --//
		final AbstractNode node = getNode();
		//-- graphic lazy init --//
		if (node instanceof OperatorNode) {
			OperatorNode operatorNode = (OperatorNode) node;
			ComboBox comboBox = getOperatorComboBoxCreated();
			comboBox.getSelectionModel().select(operatorNode.getOperator());
			currentGraphic = comboBox;
		} else {
			currentGraphic = getTextField();
		}

        super.startEdit();
        setText(null);
        setGraphic(currentGraphic);
		if (currentGraphic != null) {
			if (currentGraphic instanceof ComboBox) {
				((ComboBox) currentGraphic).show();
			}
			currentGraphic.requestFocus();
		}
    }

	private static String removeEndingColon(String text) {
		if (text.endsWith(":")) { // not " :" by safety
			text = text.substring(text.length() - 2).trim(); // trim if it ands by " :", and not ":"
		}
		return text;
	}

	private static String ensureEndingColor(String text) {
		if (!text.endsWith(":")) {
			text += " :";
		}
		return text;
	}

	@Override
	public void cancelEdit() {
        super.cancelEdit();

		/*AbstractNode node = getItem();
		if (node instanceof OperatorNode) {
			OperatorNode operatorNode = (OperatorNode) node;
			setText(OPERATOR_STRING_CONVERTER.toString(operatorNode.getOperator()));
		} else {
			setText(currentStringConverter.toString(node));
		}
        setGraphic(null);*/
    }



    @Override
	public void updateItem(Object value, boolean empty) {
        super.updateItem(value, empty);

		AbstractNode node = getNode();
		System.out.println("updateItem, value: " + value + "node: " + node);
		System.out.println("updateItem, hashcode" + this.hashCode());
		if (node == null) {
			currentStringConverter = null;
            setText(null);
            setGraphic(null);
			//setEditable(false);
			return;
		}

        //setEditable(node != null && !(node instanceof PropertiesNode) && !(node instanceof OperatorsNode));

        Node graphic = null;//treeItem.getGraphic();
		if (node instanceof OperatorNode) {
			OperatorNode operatorNode = (OperatorNode) node;
			currentStringConverter = OPERATOR_STRING_CONVERTER;
            if (isEditing()) {
				if (operatorComboBox != null) {
					operatorComboBox.getSelectionModel().select(operatorNode.getOperator());
				}
				setText(null);
				setGraphic(operatorComboBox);
            } else {
				setText(ensureEndingColor(currentStringConverter.toString(operatorNode.getOperator())));
                setGraphic(graphic);
            }
		} else {
			currentStringConverter = STRING_CONVERTERS.get(node.getClass());
            if (isEditing()) {
                if (textField != null) {
                    textField.setText(currentStringConverter.toString(node));
                }
                setText(null);
				setGraphic(textField);
            } else {
				String text = currentStringConverter.toString(node);
				if (node instanceof WithSingleChildNode) {
					text = ensureEndingColor(text);
				}
				setText(text);
                setGraphic(graphic);
            }
		}

        /*TreeItem<AbstractNode> treeItem = getTreeItem();
        Node graphic = treeItem == null ? null : treeItem.getGraphic();
		if (item == null) {
			currentStringConverter = null;
            setText(null);
            setGraphic(graphic);
		} else if (item instanceof OperatorNode) {
			OperatorNode operatorNode = (OperatorNode) item;
			currentStringConverter = OPERATOR_STRING_CONVERTER;
            if (isEditing()) {
				if (comboBox != null) {
					comboBox.getSelectionModel().select(operatorNode.getOperator());
				}
				setText(null);
				setGraphic(comboBox);
            } else {
				setText(ensureEndingColor(currentStringConverter.toString(operatorNode.getOperator())));
                setGraphic(graphic);
            }
		} else {
			currentStringConverter = STRING_CONVERTERS.get(getItem().getClass());
            if (isEditing()) {
                if (textField != null) {
                    textField.setText(currentStringConverter.toString(item));
                }
                setText(null);
				setGraphic(textField);
            } else {
				String text = currentStringConverter.toString(item);
				if (item instanceof WithSingleChildNode) {
					text = ensureEndingColor(text);
				}
				setText(text);
                setGraphic(graphic);
            }
		}*/
    }
}
