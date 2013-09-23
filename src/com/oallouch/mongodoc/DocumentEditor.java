package com.oallouch.mongodoc;

import com.oallouch.mongodoc.tree.DocumentTree;
import java.util.Map;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;

public class DocumentEditor extends SplitPane {
	private DocumentTree documentTree;
	private JsonArea jsonArea;

	public DocumentEditor() {
		documentTree = new DocumentTree();
		jsonArea = new JsonArea();
		jsonArea.addEventHandler(JsonArea.MODIFIED, event -> {
			System.out.println("jsonArea MODIFIED");
			Map<String, Object> node = jsonArea.getRootJsonObject();
			documentTree.setRootJsonObject(node);
		});

		getItems().addAll(new StackPane(documentTree), new StackPane(jsonArea));
		setDividerPositions(0.5f, 0.5f);
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
