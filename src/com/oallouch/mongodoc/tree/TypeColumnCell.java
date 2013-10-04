package com.oallouch.mongodoc.tree;

import com.oallouch.mongodoc.DocumentEditor;
import java.util.Arrays;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.input.InputEvent;

public class TypeColumnCell extends AbstractValueColumnCell {
	private ComboBox<DataType> combo;

	@Override
	protected void updateItem(Object value, DataType dataType) {

        setEditable(true);

		if (isEditing()) {
			if (combo != null) {
				combo.setValue(dataType);
			}
			setText(null);
			setGraphic(combo);
		} else {
			setText(dataType != null ? dataType.getText() : null);
			setGraphic(null);
		}
	}
	
	@Override
    public void startEdit() {
		if (!isEditable()) {
			return;
		}
		if (combo == null) {
			ObservableList<DataType> dataTypeList = FXCollections.observableArrayList(Arrays.asList(DataType.values()));
			combo = new ComboBox<>(dataTypeList);
		}
		combo.getSelectionModel().select(getDataType());
		combo.setOnAction(t -> cancelEdit());

        super.startEdit();
        setText(null);
        setGraphic(combo);
		combo.requestFocus();
		combo.show();
    }

	@Override
	public void cancelEdit() {
        super.cancelEdit();
		
		DataType newDataType = combo.getValue();
		if (newDataType != getDataType()) {
			setValue(newDataType.toValueOfType(getItem()));
			// "value" is a Property, so the value cell repaints itself
			fireEvent(new InputEvent(DocumentEditor.MODIFIED));
			
			//-- ugly workaround to update the "value" cell --//
			// the other that consist in hiding and showing the column doesn't work (setText has then not effect)
			/*TreeItem treeItem = withValueNode.getTreeItem();
			ObservableList<TreeItem> parentsChildren = treeItem.getParent().getChildren();
			int index = parentsChildren.indexOf(treeItem);
			parentsChildren.remove(treeItem);
			Platform.runLater(() -> { // runLater is important
				parentsChildren.add(index, treeItem);
			});*/
			
		}
		
		setText(newDataType.getText());
        setGraphic(null);
    }
}
