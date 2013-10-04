package test;

import java.util.Date;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import jidefx.scene.control.field.DateField;

public class TestDateFieldCell extends Application {
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage stage) {
		stage.setTitle("DateField Editor Test");
		TreeItem<Person> root = new TreeItem<>(new Person("John"));
		TreeItem<Person> childNode1 = new TreeItem<>(new Person("Paul"));
		TreeItem<Person> childNode2 = new TreeItem<>(new Person("George"));
		TreeItem<Person> childNode3 = new TreeItem<>(new Person("Ringo"));

		root.setExpanded(true);
		root.getChildren().setAll(childNode1, childNode2, childNode3);
		
		TreeTableColumn<Person,String> nameCol = new TreeTableColumn<>("Name");
		nameCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
		
		TreeTableColumn<Person,Date> dateCol = new TreeTableColumn<>("Date");
		dateCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("date"));
		dateCol.setCellFactory(col -> new DateCellEditor());

		TreeTableView<Person> treeTableView = new TreeTableView<>(root);
		treeTableView.getColumns().setAll(nameCol, dateCol);
		treeTableView.setEditable(true);
		
        StackPane rootNode = new StackPane();
        rootNode.getChildren().add(treeTableView);
        stage.setScene(new Scene(rootNode, 300, 250));
        stage.show();
	}
	
	public static class DateCellEditor extends TreeTableCell<Person, Date> {
		private DateField dateField = new DateField();

		@Override
		protected void updateItem(Date date, boolean empty) {
			System.out.println("Date: " + date + ", empty: " + empty + ", index: " + getIndex());
			if (empty) {
				setText(null);
				setGraphic(null);
				return;
			}
			setEditable(true);
			super.updateItem(date, empty);
			if (isEditing()) {
				dateField.setValue(date);
				setText(null);
				setGraphic(dateField);
			} else {
				setText(date.toString());
				setGraphic(null);
			}
		}

		@Override
		public void startEdit() {
			super.startEdit();
			dateField.setValue(getItem());
			setText(null);
			setGraphic(dateField);
		}

		@Override
		public void cancelEdit() {
			super.cancelEdit();
			setText(getItem().toString());
			setGraphic(null);
		}
	}
	
	public static class Person {
		private String name;
		private Date date = new Date();
		
		public Person(String name) {
			this.name = name;
		}
		
		public String getName() {
			return this.name;
		}
		public Date getDate() {
			return this.date;
		}
	}
}
