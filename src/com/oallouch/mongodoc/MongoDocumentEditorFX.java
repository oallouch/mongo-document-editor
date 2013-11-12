package com.oallouch.mongodoc;

import com.oallouch.mongodoc.output.JsonArea;
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

		Scene scene = new Scene(root, 1000, 500);
		scene.getStylesheets().add("com/oallouch/mongodoc/DocumentEditor.css");

		primaryStage.setTitle("Mongo Document Editor");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	@Override
	public void stop() throws Exception {
		JsonArea.shutdown();
	}
	

	public static void main(String[] args) {
		launch(args);
	}

}
