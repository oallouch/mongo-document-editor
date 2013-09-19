package com.oallouch.mongodoc.tree;

import com.oallouch.mongodoc.node.AbstractNode;
import com.oallouch.mongodoc.node.PropertiesNode;
import com.oallouch.mongodoc.node.WithSingleChildNode;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;


public class DocumentTree extends StackPane {
    // Observer list
    private ArrayList<DocumentTreeObserver> observers;
    // edit form factory
    //private QueryTreeEditorFactory factory;

	private PropertiesNode rootNode;
	private TreeTableView<AbstractNode> treeTable;
    private Menu cmiNodes;
    private MenuItem cmiRemove;
    
    public DocumentTree() {
        observers = new ArrayList<>();
        // Set the tree view not resizable when container grow up
        //SplitPane.setResizableWithParent(bpLeft, Boolean.FALSE);

		treeTable = new TreeTableView<>();
		TreeTableColumn<AbstractNode, Object> nameCol = new TreeTableColumn<>("Name");
		nameCol.setCellFactory((TreeTableColumn<AbstractNode, Object> treeTableColumn) -> new DocumentTreeTableCell());

		TreeTableColumn<AbstractNode, Object> valueCol = new TreeTableColumn<>("Value");
		valueCol.setCellFactory((TreeTableColumn<AbstractNode, Object> treeTableColumn) -> new SecondColumnTreeTableCell());

		treeTable.getColumns().setAll(nameCol, valueCol);
		//treeTable.setTreeColumn(nameCol);

		treeTable.setEditable(true);

		// . treeTable mustn't be in row selection mode
		// . found in TreeTableCellBehavior.simpleSelect(MouseEvent e) (7th line of the method)
		treeTable.getSelectionModel().setCellSelectionEnabled(true);

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

        resetQuery();
    }
    
    /*
     * Reset the query (just set a new root element to the treeview)
     */
    @FXML
    public void resetQuery() {
        setRootNode(new PropertiesNode());
    }

	public PropertiesNode getRootNode() {
		return rootNode;
	}
	public void setRootNode(PropertiesNode rootNode) {
		this.rootNode = rootNode;
        TreeItem<AbstractNode> newRoot = createTreeItem(rootNode);
        treeTable.setRoot(newRoot);
        treeTable.getSelectionModel().select(newRoot);
        updateObservers();

		//treeTable.edit(1, treeTable.getTreeColumn());
	}

	/** recursive */
	private TreeItem<AbstractNode> createTreeItem(AbstractNode node) {
		//-- the node itself --//
		TreeItem<AbstractNode> treeItem = new TreeItem<>(node);
		treeItem.setExpanded(true);

		if (node instanceof WithSingleChildNode) {
			//-- we skip the value because it's in the second column --//
			//-- (see SecondColumnTreeTableCell) --//
			AbstractNode valueNode = node.getChild(0);
			// we add the valueNode children to the valueNode's parent's TreeItem
			if (valueNode != null) {
				for (AbstractNode child : valueNode.getChildren()) {
					treeItem.getChildren().add(createTreeItem(child));
				}
			}
		} else {
			//-- children --//
			for (AbstractNode child : node.getChildren()) {
				treeItem.getChildren().add(createTreeItem(child));
			}
		}
		return treeItem;
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
            for (Class<? extends AbstractNode> clazz : selectedNode.getAcceptedChildrenTypes()) {
                MenuItem menuItem = new MenuItem(clazz.getSimpleName());
                menuItem.setUserData(clazz);
                menuItem.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        MenuItem menuItem = (MenuItem) e.getSource();
                        Class<? extends AbstractNode> nodeClass = (Class) menuItem.getUserData();
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
                    }
                });
                items.add(menuItem);
            }
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
        AbstractNode parent = selected.getValue().getParent();
        parent.removeChild(selected.getValue());
        selected.getParent().getChildren().remove(selected);
        sModel.selectPrevious();
    }
    
    public void addObserver(DocumentTreeObserver observer) {
        observers.add(observer);
        updateObservers();
    }
    
    public void removeObserver(DocumentTreeObserver observer) {
        observers.remove(observer);
    }
    
    private void updateObservers() {
        for(DocumentTreeObserver ob : observers) {
            ob.updateQuery(treeTable.getRoot().getValue());
        }
    }
}
