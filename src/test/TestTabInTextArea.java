package test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class TestTabInTextArea extends Application {
	@Override
	public void start(Stage primaryStage) {
		Scene scene = new Scene(new TextArea("1	2"), 1000, 500);
		primaryStage.setScene(scene);
		primaryStage.show();
		
	}
	public static void main(String[] args) {
		launch(args);
	}
	
}
