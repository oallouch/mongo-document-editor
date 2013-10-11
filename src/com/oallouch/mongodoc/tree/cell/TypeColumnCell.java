package com.oallouch.mongodoc.tree.cell;

import com.oallouch.mongodoc.DocumentEditor;
import com.oallouch.mongodoc.tree.DataType;
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
			getComboBox().setValue(dataType);
			setText(null);
			setGraphic(getComboBox());
		} else {
			setText(dataType.getText());
			setGraphic(null);
		}
	}
	
	private ComboBox<DataType> getComboBox() {
		if (combo == null) {
			ObservableList<DataType> dataTypeList = FXCollections.observableArrayList(Arrays.asList(DataType.values()));
			combo = new ComboBox<>(dataTypeList);
			combo.setOnAction(t -> cancelEdit());
		}
		return combo;
	}
	
	@Override
    public void startEdit() {
		if (!isEditable()) {
			return;
		}

		getComboBox().getSelectionModel().select(getDataType());
		
        super.startEdit();
        setText(null);
        setGraphic(getComboBox());
		getComboBox().requestFocus();
		getComboBox().show();
    }

	@Override
	public void cancelEdit() {
        super.cancelEdit();
		
		DataType newDataType = getComboBox().getValue();
		if (newDataType != getDataType()) {
			Object newValue = newDataType.toValueOfType(getItem());
			setValue(newValue);
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
