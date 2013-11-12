package com.oallouch.mongodoc.output;

import static com.oallouch.mongodoc.DocumentEditor.MODIFIED;
import java.util.Map;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.InputEvent;

public class OutputPane extends TabPane {
	private JsonArea jsonArea;
	private JavaOutput javaOutput;
	private PhpOutput phpOutput;
	private boolean tempEventBlocking;
	
	public OutputPane() {
		//---------------- Json -----------------//
		Tab jsonTab = new Tab("Json");
		jsonTab.setClosable(false);
		jsonTab.setContent(jsonArea = new JsonArea());
		jsonArea.addEventHandler(MODIFIED, e -> {
			e.consume(); // or it would also be seen as an OutputPane event
			if (!tempEventBlocking) {
				System.out.println("JsonArea MODIFIED");
				updateOutputs();
				fireEvent(new InputEvent(MODIFIED));
			}
		});
		
		//---------------- Java -----------------//
		Tab javaTab = new Tab("Java r/o");
		javaTab.setClosable(false);
		javaTab.setContent(javaOutput = new JavaOutput());
		
		//---------------- PHP -----------------//
		Tab phpTab = new Tab("PHP r/o");
		phpTab.setClosable(false);
		phpTab.setContent(phpOutput = new PhpOutput());
		
		getTabs().setAll(jsonTab, javaTab, phpTab);
	}
	
	public Map<String, Object> getRootJsonObject() {
		return jsonArea.getRootJsonObject();
	}
	public void setRootJsonObject(Map<String, Object> jsonObject) {
		tempEventBlocking = true;
		try {
			jsonArea.setRootJsonObject(jsonObject);
			updateOutputs();
		} finally {
			tempEventBlocking = false;
		}
	}
	
	private void updateOutputs() {
		Map<String, Object> jsonObject = getRootJsonObject();
		// very fast, no need to see if it's visible or not
		javaOutput.setRootJsonObject(jsonObject);
		phpOutput.setRootJsonObject(jsonObject);
	}
	
	public String getJsonText() {
		return jsonArea.getJsonText();
	}
	public void setJsonText(String jsonText) {
		tempEventBlocking = true;
		try {
			jsonArea.setJsonText(jsonText);
			Map<String, Object> rootJsonObject = jsonArea.getRootJsonObject();
			javaOutput.setRootJsonObject(rootJsonObject);
			phpOutput.setRootJsonObject(rootJsonObject);
		} finally {
			tempEventBlocking = false;
		}
	}
}
