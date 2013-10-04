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
		documentTree = new DocumentTree();
		documentTree.addEventHandler(MODIFIED, e -> {
			System.out.println("documentTree MODIFIED");
			Map<String, Object> jsonObject = documentTree.getRootJsonObject();
			outputPane.setRootJsonObject(jsonObject);
		});

		outputPane = new OutputPane();
		outputPane.addEventHandler(MODIFIED, e -> {
			documentTree.setRootJsonObject(outputPane.getRootJsonObject());
		});
		
		getItems().addAll(new StackPane(documentTree), new StackPane(outputPane));
		setDividerPositions(0.7f, 0.3f);
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
		outputPane.setJsonText(jsonText);
		// jsonArea parses the text
		documentTree.setRootJsonObject(outputPane.getRootJsonObject());
	}
}
