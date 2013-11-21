package com.oallouch.mongodoc;

import com.oallouch.mongodoc.output.OutputPane;
import com.oallouch.mongodoc.tree.DocumentTree;
import java.util.Map;
import javafx.event.EventType;
import javafx.scene.control.SplitPane;
import javafx.scene.input.InputEvent;
import javafx.scene.layout.StackPane;

public class DocumentEditor extends SplitPane {
	public static final EventType<InputEvent> MODIFIED = new EventType<>(InputEvent.ANY, "MODIFIED");
	private DocumentTree documentTree;
	private OutputPane outputPane;

	public DocumentEditor() {
		documentTree = new DocumentTree(true);
		documentTree.addEventHandler(MODIFIED, event -> {
			System.out.println("documentTree MODIFIED");
			Map<String, Object> jsonObject = documentTree.getRootJsonObject();
			outputPane.setRootJsonObject(jsonObject);
		});

		outputPane = new OutputPane();
		outputPane.addEventHandler(MODIFIED, event -> {
			System.out.println("outputPane MODIFIED");
			documentTree.setRootJsonObject(outputPane.getRootJsonObject());
		});
		
		documentTree.setPrefHeight(200);
		getItems().addAll(documentTree, outputPane);
		setDividerPositions(0.6f, 0.4f);
	}

	public Map<String, Object> getJsonObject() {
		return documentTree.getRootJsonObject();
	}
	public void setJsonObject(Map<String, Object> jsonObject) {
		documentTree.setRootJsonObject(jsonObject);
	}

	public String getJsonText() {
		return outputPane.getJsonText();
	}
	public void setJsonText(String jsonText) {
		if (jsonText == null || jsonText.length() == 0) {
			jsonText = "{}";
		}
		outputPane.setJsonText(jsonText);
		// jsonArea parses the text
		documentTree.setRootJsonObject(outputPane.getRootJsonObject());
	}
}
