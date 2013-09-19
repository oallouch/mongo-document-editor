package com.oallouch.mongodoc;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.oallouch.mongodoc.node.NodeFactory;
import com.oallouch.mongodoc.node.PropertiesNode;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.InputEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

public class JsonArea extends BorderPane {
	public static final EventType<InputEvent> MODIFIED = new EventType<>(InputEvent.ANY, "MODIFIED");

	private TextArea textArea;
	private Label errorLabel;
	private PropertiesNode rootNode;
	private Gson gson;

	public JsonArea() {
		gson = new Gson();
		//-- UI controls --//
		this.textArea = new TextArea();
		textArea.setPromptText("enter JSON here");
		textArea.textProperty().addListener((ObservableValue<? extends String> ov, String oldText, String newText) -> {
			System.out.println("textArea updated");
			String source = textArea.getText();
			try {
				Map<String, Object> rootAsMap = gson.fromJson(source, Map.class);
				rootNode = NodeFactory.toNode(rootAsMap);
				errorLabel.setText(null);
				fireEvent(new InputEvent(MODIFIED));
			} catch (JsonSyntaxException e) {
				errorLabel.setText(e.getMessage());
				Logger.getLogger(JsonArea.class.getName()).log(Level.INFO, null, e);
			}
		});
		setCenter(textArea);

		errorLabel = new Label();
		errorLabel.setTextFill(Color.RED);
		setBottom(errorLabel);
	}


	public PropertiesNode getRootNode() {
		return this.rootNode;
	}
	public String getJsonText() {
		return textArea.getText();
	}
	public void setJsonText(String jsonText) {
		textArea.setText(jsonText);
	}
}
