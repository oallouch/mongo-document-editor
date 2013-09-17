package com.oallouch.mongodoc;

import com.oallouch.mongodoc.ui.module.QueryTreeBuilder;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MongoDocumentEditorFX extends Application {

	@Override
	public void start(Stage primaryStage) {
		QueryTreeBuilder queryTreeBuilder = new QueryTreeBuilder();

		StackPane root = new StackPane();
		root.getChildren().add(queryTreeBuilder);

		Scene scene = new Scene(root, 300, 250);

		primaryStage.setTitle("Mongo Document Editor");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * The main() method is ignored in correctly deployed JavaFX application.
	 * main() serves only as fallback in case the application can not be
	 * launched through deployment artifacts, e.g., in IDEs with limited FX
	 * support. NetBeans ignores main().
	 *
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}

}
