package com.oallouch.mongodoc;

import com.oallouch.mongodoc.tree.DocumentTree;
import java.util.Map;
import javafx.event.EventType;
import javafx.scene.control.SplitPane;
import javafx.scene.input.InputEvent;
import javafx.scene.layout.StackPane;

public class DocumentEditor extends SplitPane {
	public static final EventType<InputEvent> MODIFIED = new EventType<>(InputEvent.ANY, "MODIFIED");
	private DocumentTree documentTree;
	private JsonArea jsonArea;
	private boolean tempEventBlocking;

	public DocumentEditor() {
		documentTree = new DocumentTree();
		documentTree.addEventHandler(MODIFIED, e -> {
			System.out.println("documentTree MODIFIED");
			Map<String, Object> jsonObject = documentTree.getRootJsonObject();
			tempEventBlocking = true;
			try {
				jsonArea.setRootJsonObject(jsonObject);
			} finally {
				tempEventBlocking = false;
			}
			
		});
		jsonArea = new JsonArea();
		jsonArea.addEventHandler(MODIFIED, e -> {
			if (!tempEventBlocking) {
				System.out.println("jsonArea MODIFIED");
				Map<String, Object> jsonObject = jsonArea.getRootJsonObject();
				documentTree.setRootJsonObject(jsonObject);
			}
		});

		getItems().addAll(new StackPane(documentTree), new StackPane(jsonArea));
		setDividerPositions(0.7f, 0.3f);
	}

	public Map<String, Object> getJsonObject() {
		return documentTree.getRootJsonObject();
	}
	public void setJsonObject(Map<String, Object> jsonObject) {
		documentTree.setRootJsonObject(jsonObject);
	}

	public String getJsonText() {
		return jsonArea.getJsonText();
	}
	public void setJsonText(String jsonText) {
		jsonArea.setJsonText(jsonText);
	}
}
