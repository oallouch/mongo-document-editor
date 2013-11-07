package com.oallouch.mongodoc.embed;

import com.oallouch.mongodoc.DocumentEditor;
import com.sun.javafx.application.PlatformImpl;
import java.io.File;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DocumentEditorDialog {
	private Stage stage;
	private DocumentEditor documentEditor;
	
	public DocumentEditorDialog() {
		init();
	}
	
	/**
	 * called from the Swing thread
	 */
	private void init() {
		PlatformImpl.runAndWait(() -> {
			//-- stage --//
			stage = new Stage(StageStyle.UTILITY);
			stage.setWidth(1000);
			stage.setHeight(500);
			stage.centerOnScreen();
			/*stage.setOnCloseRequest(windowEvent -> {
				stage.hide();
				windowEvent.consume(); // no exit
			});*/
			stage.initOwner(JFXContextInSwingApp.getJavaFXWindow());
			stage.initModality(Modality.APPLICATION_MODAL);
			//-- documentEditor --//
			documentEditor = new DocumentEditor();
			//-- scene --//
			Scene scene = new Scene(documentEditor, 300, 250, Color.WHITE);
			scene.getStylesheets().add("com/oallouch/mongodoc/DocumentEditor.css");
			stage.setScene(scene);
		});
	}
	
	public String showEditor(String jsonText) {
		PlatformImpl.runAndWait(() -> {
			documentEditor.setJsonText(jsonText);
			stage.showAndWait();
		});
		return documentEditor.getJsonText();
	}
}
