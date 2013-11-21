package com.oallouch.mongodoc.output;

import codearea.control.CodeArea;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import static com.oallouch.mongodoc.DocumentEditor.MODIFIED;
import com.oallouch.mongodoc.embed.JFXContextInSwingApp;
import com.oallouch.mongodoc.util.JsonUtils;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Label;
import javafx.scene.input.InputEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

public class JsonArea extends BorderPane {
	private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	private ScheduledFuture parsingFuture;
	private CodeArea codeArea;
	private Label errorLabel;
	private Map<String, Object> rootJsonObject;

	public JsonArea() {
		//-- UI controls --//
		this.codeArea = new CodeArea();
		codeArea.textProperty().addListener((ChangeListener) (observableValue, oldText, newText) -> {
			//----------------- parsingFuture ----------------//
			if (parsingFuture != null && !parsingFuture.isDone()) {
				parsingFuture.cancel(false);
			}
			parsingFuture = scheduler.schedule(() -> {
				Platform.runLater(this::completeParsing);
			}, 500, TimeUnit.MILLISECONDS);
			
			//--- parsing for the value (not for coloring) ---//
			String source = codeArea.getText();
			try {
				rootJsonObject = JsonUtils.toJsonObject(source);
				rootJsonObject = JsonUtils.putSpecialJavaTypes(rootJsonObject);
				if (rootJsonObject == null) {
					rootJsonObject = Collections.emptyMap();
				}
				errorLabel.setText(null);
				fireEvent(new InputEvent(MODIFIED));
			} catch (JsonParseException e) {
				JsonLocation errorLocation = e.getLocation();
				errorLabel.setText("Parsing error at line " + errorLocation.getLineNr() + " and column " + errorLocation.getColumnNr());
			}
		});
		setCenter(codeArea);

		errorLabel = new Label();
		errorLabel.setTextFill(Color.RED);
		setBottom(errorLabel);
	}

	private void completeParsing() {
		if (!Platform.isFxApplicationThread()) {
			throw new RuntimeException("This method must be called from the JavaFX event Thread");
		}
		String source = codeArea.getText();
		try {
			//------------------- first parsing ----------------//
			JsonUtils.toJsonObject(source);
			//------------------- second parsing ---------------//
			JsonParser jsonParser = JsonUtils.getJsonFactory().createParser(source);
			int lastParsedIndex = 0;
			JsonToken currentToken;
			while ((currentToken = jsonParser.nextToken()) != null) {
				int locationStart = (int) jsonParser.getCurrentLocation().getCharOffset();
				int length = -1;
				String cssClass = null;
				if (currentToken == JsonToken.FIELD_NAME) {
					String propertyName = jsonParser.getText();
					//------------ property name end => start -------------//
					// pb: locationStart is the start of the value
					while (true) {
						char c = source.charAt(locationStart);
						if (Character.isAlphabetic(c)) {
							locationStart = locationStart - propertyName.length() + 1;
							break;
						}
						locationStart--;
					}
					length = propertyName.length();
					cssClass = propertyName.startsWith("$") ? "jsonTextDollarProperty" : "jsonTextProperty";
				} else if (currentToken == JsonToken.START_ARRAY || currentToken == JsonToken.END_ARRAY) {
					length = 1;
					cssClass = "jsonTextArray";
				} else if (currentToken == JsonToken.START_OBJECT || currentToken == JsonToken.END_OBJECT) {
					length = 1;
					cssClass = "jsonTextObject";
				}
				if (cssClass != null) {
					int locationEnd = locationStart + length;
					codeArea.clearStyleClasses(lastParsedIndex, locationStart);
                    codeArea.setStyleClass(locationStart, locationEnd, cssClass);
                    lastParsedIndex = locationEnd;
				}
			}
			codeArea.clearStyleClasses(lastParsedIndex, source.length());
		} catch (IOException e) {
			// the first parsing failed => does nothing
		}
	}
	
	public static void shutdown() {
		scheduler.shutdown();
	}

	public Map<String, Object> getRootJsonObject() {
		return this.rootJsonObject;
	}
	public void setRootJsonObject(Map<String, Object> jsonObject) {
		jsonObject = JsonUtils.removeSpecialJavaTypes(jsonObject);
		String jsonText = JsonUtils.toJsonTextPretty(jsonObject);
		setJsonText(jsonText);
	}
	public String getJsonText() {
		return codeArea.getText();
	}
	public void setJsonText(String jsonText) {
		codeArea.replaceText(0, codeArea.getLength(), jsonText);
	}
}
