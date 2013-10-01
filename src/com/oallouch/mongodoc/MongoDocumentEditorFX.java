package com.oallouch.mongodoc;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MongoDocumentEditorFX extends Application {

	@Override
	public void start(Stage primaryStage) {
		DocumentEditor documentEditor = new DocumentEditor();

		documentEditor.setJsonText(
			"{\n" +
			"  \"menu\": \"Fichier\",\n" +
			"  \"commandes\": [\n" +
			"      {\n" +
			"          \"numero\": 1,\n" +
			"          \"title\": \"Nouveau\",\n" +
			"          \"action\":\"CreateDoc\"\n" +
			"      },\n" +
			"      {\n" +
			"          \"title\": \"Ouvrir\",\n" +
			"          \"action\": \"OpenDoc\"\n" +
			"      },\n" +
			"      {\n" +
			"          \"title\": \"Fermer\",\n" +
			"          \"action\": \"CloseDoc\"\n" +
			"      }\n" +
			"   ]\n" +
			"} "
		);


		StackPane root = new StackPane();
		root.getChildren().add(documentEditor);

		Scene scene = new Scene(root, 700, 500);

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
