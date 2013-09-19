package com.oallouch.mongodoc;

import com.oallouch.mongodoc.node.NodeFactory;
import com.oallouch.mongodoc.node.PropertiesNode;
import com.oallouch.mongodoc.tree.DocumentTree;
import java.util.Map;
import javafx.event.EventHandler;
import javafx.scene.control.SplitPane;
import javafx.scene.input.InputEvent;
import javafx.scene.layout.StackPane;

public class DocumentEditor extends SplitPane {
	private DocumentTree documentTree;
	private JsonArea jsonArea;

	public DocumentEditor() {
		documentTree = new DocumentTree();
		jsonArea = new JsonArea();
		jsonArea.addEventHandler(JsonArea.MODIFIED, (InputEvent event) -> {
			System.out.println("jsonArea MODIFIED");
			PropertiesNode node = jsonArea.getRootNode();
			documentTree.setRootNode(node);
		});

		getItems().addAll(new StackPane(documentTree), new StackPane(jsonArea));
		setDividerPositions(0.5f, 0.5f);
	}

	public Map<String, Object> getJsonObject() {
		PropertiesNode node = documentTree.getRootNode();
		return NodeFactory.toJsonObject(node);
	}
	public void setJsonObject(Map<String, Object> document) {
		PropertiesNode node = NodeFactory.toNode(document);
		documentTree.setRootNode(node);
	}

	public String getJsonText() {
		return jsonArea.getJsonText();
	}
	public void setJsonText(String jsonText) {
		jsonArea.setJsonText(jsonText);
	}
}
