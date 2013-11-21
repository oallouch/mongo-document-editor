package test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TestFocusInScrollPane extends Application {
	@Override
	public void start(Stage primaryStage) {
		Label label1 = new Label("first TextField");
		label1.setFocusTraversable(true);
		Label label2 = new Label("second TextField");
		label2.setFocusTraversable(true);
		
		/*FocusableLabel label1 = new FocusableLabel("first TextField");
		FocusableLabel label2 = new FocusableLabel("second TextField");*/
		
		
		/*TextField label1 = new TextField("first TextField");
		label1.setEditable(false);
		TextField label2 = new TextField("second TextField");*/
		
		/*Button label1 = new Button("first TextField");
		Button label2 = new Button("second TextField");*/
		
		VBox vBox = new VBox(label1, label2);
		ScrollPane scrollPane = new ScrollPane(vBox);
		Scene scene = new Scene(scrollPane, 1000, 500);
		scene.getStylesheets().add("test/TestFocusInScrollPane.css");
		primaryStage.setScene(scene);
		primaryStage.show();
		
	}
	public static void main(String[] args) {
		launch(args);
	}
}
