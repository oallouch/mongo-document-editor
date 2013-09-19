package com.oallouch.mongodoc.tree;

import com.google.common.collect.Maps;
import com.oallouch.mongodoc.node.AbstractNode;
import com.oallouch.mongodoc.node.OperatorNode;
import com.oallouch.mongodoc.node.OperatorNode.Operator;
import com.oallouch.mongodoc.node.PropertiesNode;
import com.oallouch.mongodoc.node.PropertyNode;
import com.oallouch.mongodoc.node.EqualsValueNode;
import com.oallouch.mongodoc.node.WithSingleChildNode;
import java.util.Map;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

public class DocumentTreeCell extends TreeCell<AbstractNode> {
	private static final Map<Class<? extends AbstractNode>, StringConverter<? extends AbstractNode>> STRING_CONVERTERS = Maps.newHashMapWithExpectedSize(6);

	private static final StringConverter<Operator> OPERATOR_STRING_CONVERTER = new StringConverter<Operator>() {
		@Override public String toString(Operator operator) {
			return operator.name(); // can't add semicolon here because it's used by the combo itself
		}
		@Override public Operator fromString(String name) {
			name = removeEndingColon(name);
			return Operator.valueOf(name);
		}
	};

	static {
		STRING_CONVERTERS.put(PropertiesNode.class, new StringConverter<PropertiesNode>() {
			@Override public String toString(PropertiesNode node) { return "{"; }
			@Override public PropertiesNode fromString(String string) { return null; }
		});
		STRING_CONVERTERS.put(PropertyNode.class, new StringConverter<PropertyNode>() {
			@Override public String toString(PropertyNode node) { return node.getName(); }
			@Override public PropertyNode fromString(String string) { return null; }
		});
		STRING_CONVERTERS.put(EqualsValueNode.class, new StringConverter<EqualsValueNode>() {
			@Override public String toString(EqualsValueNode node) {
				return node == null ? null : node.getEditingString();
			}
			@Override public EqualsValueNode fromString(String string) { return null; }
		});
	}


    private ComboBox<Operator> comboBox;
    private TextField textField;
	
	private Node currentGraphic;
	private StringConverter currentStringConverter;
	private ChangeListener focusListener;

	public DocumentTreeCell() {
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
	}

	@Override
    public void startEdit() {
		//-- editable ? --//
		final AbstractNode node = getItem();
        if (node instanceof PropertiesNode) {
            return;
        }
		//-- graphic lazy init --//
		if (getItem() instanceof OperatorNode) {
			if (comboBox == null) {
				// inspired by CellUtils.createComboBox
				comboBox = new ComboBox<>(OperatorNode.OPERATOR_LIST);
				comboBox.setConverter(OPERATOR_STRING_CONVERTER);
				comboBox.setMaxWidth(Double.MAX_VALUE);
				comboBox.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						if (isEditing()) {
							OperatorNode operatorNode = (OperatorNode) getItem();
							operatorNode.setOperator(comboBox.getValue());
							cancelEdit();
						}
					}
				});
				comboBox.setEditable(false);
				comboBox.focusedProperty().addListener(focusListener);
			}
			OperatorNode operatorNode = (OperatorNode) node;
			comboBox.getSelectionModel().select(operatorNode.getOperator());
			currentGraphic = comboBox;
		} else {
			if (textField == null) {
				// inspired by CellUtils.createTextField
				textField = new TextField(currentStringConverter.toString(node));
				textField.setOnKeyReleased(new EventHandler<KeyEvent>() {
					@Override public void handle(KeyEvent t) {
						if (t.getCode() == KeyCode.ENTER) {
							String text = textField.getText();
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
			currentGraphic = textField;
		}

        super.startEdit();
        setText(null);
        setGraphic(currentGraphic);
		if (currentGraphic != null) {
			if (currentGraphic instanceof ComboBox) {
				comboBox.show();
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

		AbstractNode node = getItem();
		if (node instanceof OperatorNode) {
			OperatorNode operatorNode = (OperatorNode) node;
			setText(OPERATOR_STRING_CONVERTER.toString(operatorNode.getOperator()));
		} else {
			setText(currentStringConverter.toString(node));
		}
        setGraphic(null);
    }

    @Override
	public void updateItem(AbstractNode item, boolean empty) {
        super.updateItem(item, empty);

        TreeItem<AbstractNode> treeItem = getTreeItem();
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
		}
    }
}
