package com.oallouch.mongodoc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import static com.oallouch.mongodoc.DocumentEditor.MODIFIED;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.InputEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

public class JsonArea extends BorderPane {
	private static final Pattern ERROR_PATTERN = Pattern.compile("line (.*?) column (.*?)\\z");

	private TextArea textArea;
	private Label errorLabel;
	private Map<String, Object> rootJsonObject;
	private Gson gson;

	public JsonArea() {
		gson = new GsonBuilder().setPrettyPrinting().create();
		//-- UI controls --//
		this.textArea = new TextArea();
		textArea.setPromptText("enter JSON here");
		textArea.textProperty().addListener((observableValue, oldText, newText) -> {
			String source = textArea.getText();
			try {
				rootJsonObject = gson.fromJson(source, Map.class);
				errorLabel.setText(null);
				fireEvent(new InputEvent(MODIFIED));
			} catch (JsonSyntaxException e) {
				String message = e.getMessage();
				Matcher matcher = ERROR_PATTERN.matcher(message);
				if (matcher.find()) {
					message = "Parsing error at line " + matcher.group(1) + " and column " + matcher.group(2);
				} else {
					Logger.getLogger(JsonArea.class.getName()).log(Level.INFO, null, e);
				}
				errorLabel.setText(message);
			}
		});
		setCenter(textArea);

		errorLabel = new Label();
		errorLabel.setTextFill(Color.RED);
		setBottom(errorLabel);
	}


	public Map<String, Object> getRootJsonObject() {
		return this.rootJsonObject;
	}
	public void setRootJsonObject(Map<String, Object> jsonObject) {
		String jsonText = gson.toJson(jsonObject);
		setJsonText(jsonText);
	}
	public String getJsonText() {
		return textArea.getText();
	}
	public void setJsonText(String jsonText) {
		textArea.setText(jsonText);
	}
}
