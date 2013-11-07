package com.oallouch.mongodoc.output;

import codearea.control.CodeArea;
import com.google.gson.JsonSyntaxException;
import static com.oallouch.mongodoc.DocumentEditor.MODIFIED;
import com.oallouch.mongodoc.util.JsonUtils;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.InputEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

public class JsonArea extends BorderPane {
	private static final Pattern ERROR_PATTERN = Pattern.compile("line (.*?) column (.*?)\\z");

	//private CodeArea codeArea;
	private TextArea codeArea;
	private Label errorLabel;
	private Map<String, Object> rootJsonObject;

	public JsonArea() {
		//-- UI controls --//
		this.codeArea = new TextArea();//CodeArea();
		codeArea.textProperty().addListener((ChangeListener) (observableValue, oldText, newText) -> {
			String source = codeArea.getText();
			try {
				rootJsonObject = JsonUtils.toJsonObject(source);
				if (rootJsonObject == null) {
					rootJsonObject = Collections.emptyMap();
				}
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
		setCenter(codeArea);

		errorLabel = new Label();
		errorLabel.setTextFill(Color.RED);
		setBottom(errorLabel);
	}


	public Map<String, Object> getRootJsonObject() {
		return this.rootJsonObject;
	}
	public void setRootJsonObject(Map<String, Object> jsonObject) {
		String jsonText = JsonUtils.toJsonText(jsonObject);
		setJsonText(jsonText);
	}
	public String getJsonText() {
		return codeArea.getText();
	}
	public void setJsonText(String jsonText) {
		codeArea.replaceText(0, codeArea.getLength(), jsonText);
	}
}
