package com.oallouch.mongodoc.tree;

import static com.oallouch.mongodoc.DocumentEditor.MODIFIED;
import com.oallouch.mongodoc.node.AbstractNode;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.InputEvent;
import javafx.scene.layout.StackPane;


public class DocumentTree extends StackPane {
	private TreeTableView<AbstractNode> treeTable;
	private TreeItem hiddenRootItem;
    private Menu cmiNodes;
    private MenuItem cmiRemove;
    
    public DocumentTree() {
        // Set the tree view not resizable when container grow up
        //SplitPane.setResizableWithParent(bpLeft, Boolean.FALSE);

		treeTable = new TreeTableView<>();
		TreeTableColumn<AbstractNode, AbstractNode> nameCol = new TreeTableColumn<>("Name");
		nameCol.setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper(cellDataFeatures.getValue().getValue()));
		nameCol.setCellFactory(treeTableColumn -> new NameColumnCell());
		nameCol.setSortable(false);
		nameCol.addEventHandler(MODIFIED, e -> fireModified());

		TreeTableColumn<AbstractNode, Object> valueCol = new TreeTableColumn<>("Value");
		valueCol.setCellValueFactory(new TreeItemPropertyValueFactory("value"));
		valueCol.setCellFactory(treeTableColumn -> new ValueColumnCell());
		valueCol.setSortable(false);
		valueCol.addEventHandler(MODIFIED, e -> fireModified());

		TreeTableColumn<AbstractNode, Object> typeCol = new TreeTableColumn<>("Type");
		typeCol.setCellValueFactory(new TreeItemPropertyValueFactory("value"));
		typeCol.setCellFactory(treeTableColumn -> new TypeColumnCell());
		typeCol.setSortable(false);
		typeCol.addEventHandler(MODIFIED, e -> fireModified());
		typeCol.setMinWidth(130);
		typeCol.setMaxWidth(130);

		treeTable.getColumns().setAll(nameCol, valueCol, typeCol);
		treeTable.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);

		treeTable.setEditable(true);

		// . treeTable mustn't be in row selection mode
		// . found in TreeTableCellBehavior.simpleSelect(MouseEvent e) (7th line of the method)
		treeTable.getSelectionModel().setCellSelectionEnabled(true);
		
		hiddenRootItem = new TreeItem();
		treeTable.setRoot(hiddenRootItem);
		treeTable.setShowRoot(false);
		
		this.getChildren().add(treeTable);
		
        /*
         * Set the tvTreeView selection handler so it will show an edit form
         * when selecting a node is focused/selected
         */
        /*FocusModel<TreeItem<AbstractNode>> fModel = tvQuery.getFocusModel();
        fModel.focusedItemProperty().addListener(new ChangeListener<TreeItem<AbstractNode>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<AbstractNode>> ov, TreeItem<AbstractNode> t, TreeItem<AbstractNode> t1) {
                if(t1 == null) return;
                /*
                 * . Ask the factory for the right panel editor
                 * . Add it to the right borderpane
                 */

                /*AbstractNode edited = t1.getValue();
                if(edited instanceof PropertyNode) {
                    bpRight.setCenter(factory.getPropertyEditor(t1));
                } else if (edited instanceof OperatorNode) {
                    bpRight.setCenter(factory.getOperatorEditor(t1));
                } else {
                    bpRight.setCenter(null);
                }
            }
        });*/

        //reset();
    }
    
    public void reset() {
        setRootJsonObject(new HashMap<>());
    }

	public Map<String, Object> getRootJsonObject() {
		if (hiddenRootItem.getChildren().isEmpty()) {
			return Collections.emptyMap();
		}
		return (Map<String, Object>) TreeItemFactory.toJsonObject((TreeItem) hiddenRootItem.getChildren().get(0));
	}
	public void setRootJsonObject(Map<String, Object> jsonObject) {
		hiddenRootItem.getChildren().clear();
		TreeItemFactory.createRootTreeItem(jsonObject, hiddenRootItem);
		expandAll(treeTable.getRoot());
	}
	
	private void fireModified() {
		fireEvent(new InputEvent(MODIFIED));
	}
	
	private void expandAll(TreeItem<?> item) {
		item.setExpanded(true);
		for (TreeItem<? extends Object> childItem : item.getChildren()) {
			expandAll(childItem);
		}
	}
    
    /*
     * Add/Remove the menu item of the treeview in function of the
     * selected teenode value. This function is intended to avoid adding bad node
     * to others node, but DOES NOT VALIDATE the query.
     */
    private void contextMenuRequest() {
        //Find what item is selected
        final SelectionModel<TreeItem<AbstractNode>> sModel = treeTable.getSelectionModel();
        final TreeItem<AbstractNode> selected = sModel.getSelectedItem();

        if (selected == null) {
            // none was selected ?
            cmiNodes.setDisable(true);
            cmiRemove.setDisable(true);
        } else {
            // Create allowed menu items for the node
            List<MenuItem> items = cmiNodes.getItems();
            items.clear();
            final AbstractNode selectedNode = selected.getValue();
            /*for (Class<? extends AbstractNode> clazz : selectedNode.getAcceptedChildrenTypes()) {
                MenuItem menuItem = new MenuItem(clazz.getSimpleName());
                menuItem.setUserData(clazz);
                menuItem.setOnAction(e -> {
					MenuItem menuItem1 = (MenuItem) e.getSource();
					Class<? extends AbstractNode> nodeClass = (Class) menuItem1.getUserData();
					AbstractNode node;
					try {
						node = nodeClass.newInstance();
					} catch (IllegalAccessException | InstantiationException ex) {
						// should never happens
						throw new RuntimeException(ex);
					}
					selectedNode.addChild(node);
					TreeItem<AbstractNode> newTreeItem = new TreeItem<>(node);
					selected.getChildren().add(newTreeItem);
					selected.setExpanded(true);
					sModel.select(newTreeItem);
				});
                items.add(menuItem);
            }*/
            // avoid root deletion and disable the nodes menu if needed
            cmiNodes.setDisable(items.isEmpty());
            cmiRemove.setDisable(selected.getParent() == null);
        }
    }

    /*
     * Remove an item from the query tree
     */
    private void removeItem() {
        // Get the selected item to remove
        SelectionModel<TreeItem<AbstractNode>> sModel = treeTable.getSelectionModel();
        TreeItem<AbstractNode> selected = sModel.getSelectedItem();
        
        // Remove also the node from the AbstractNode parent and select root node again
        //AbstractNode parent = selected.getValue().getParent();
        //parent.removeChild(selected.getValue());
        selected.getParent().getChildren().remove(selected);
        sModel.selectPrevious();
    }
}
