package com.oallouch.mongodoc.output;

import static com.oallouch.mongodoc.DocumentEditor.MODIFIED;
import java.util.Map;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.InputEvent;

public class OutputPane extends TabPane {
	private JsonArea jsonArea;
	private JavaOutput javaOutput;
	private boolean tempEventBlocking;
	
	public OutputPane() {
		Tab jsonTab = new Tab("Json");
		jsonTab.setClosable(false);
		jsonTab.setContent(jsonArea = new JsonArea());
		jsonArea.addEventHandler(MODIFIED, e -> {
			if (!tempEventBlocking) {
				fireEvent(new InputEvent(MODIFIED));
			}
		});
		
		Tab javaTab = new Tab("Java r/o");
		javaTab.setClosable(false);
		javaTab.setContent(javaOutput = new JavaOutput());
		
		getTabs().setAll(jsonTab, javaTab);
	}
	
	public Map<String, Object> getRootJsonObject() {
		return jsonArea.getRootJsonObject();
	}
	public void setRootJsonObject(Map<String, Object> jsonObject) {
		tempEventBlocking = true;
		try {
			jsonArea.setRootJsonObject(jsonObject);
			javaOutput.setRootJsonObject(jsonObject); // very fast, no need to see if it's visible or not
		} finally {
			tempEventBlocking = false;
		}
	}
	public String getJsonText() {
		return jsonArea.getJsonText();
	}
	public void setJsonText(String jsonText) {
		tempEventBlocking = true;
		try {
			jsonArea.setJsonText(jsonText);
			javaOutput.setRootJsonObject(jsonArea.getRootJsonObject());
		} finally {
			tempEventBlocking = false;
		}
	}
}
