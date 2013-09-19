package com.oallouch.mongodoc.ui.module.json;

import com.google.gson.Gson;
import com.oallouch.mongodoc.node.AbstractNode;
import com.oallouch.mongodoc.node.EqualsValueNode;
import com.oallouch.mongodoc.node.PropertiesNode;
import com.oallouch.mongodoc.node.PropertyNode;
import com.oallouch.mongodoc.node.WithSingleChildNode;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
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

	private AbstractNode node;
	private Gson gson;

	public JsonArea() {
		gson = new Gson();
		//-- UI controls --//
		final TextArea textArea = new TextArea();
		textArea.setPromptText("enter JSON here");
		setCenter(textArea);

		final Label errorLabel = new Label();
		errorLabel.setTextFill(Color.RED);
		setBottom(errorLabel);

		Button goButton = new Button("<<");
		goButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				String source = textArea.getText();
				try {
					Map<String, Object> rootAsMap = gson.fromJson(source, Map.class);
					node = toNode(rootAsMap);
					errorLabel.setText(null);
					fireEvent(new InputEvent(MODIFIED));
				} catch (Throwable e) {
					errorLabel.setText(e.getMessage());
					Logger.getLogger(JsonArea.class.getName()).log(Level.INFO, null, e);
				}
			}
		});
		setLeft(goButton);
	}

	private AbstractNode toNode(Object jsonNode) {
		if (jsonNode instanceof Map) {
			Map<String, Object> map = (Map<String, Object>) jsonNode;
			if (map.isEmpty()) {
				return null;
			}

			//-- OperatorsNode or PropertiesNode --//
			String firstKey = map.keySet().iterator().next();
			AbstractNode newNode = new PropertiesNode();

			//-- child entries --//
			//-- (recursion) --//
			for (Entry<String, Object> entry : map.entrySet()) {
				AbstractNode child = toNode(entry);
				newNode.addChild(child);
			}
			return newNode;
		} else if (jsonNode instanceof Entry) {
			Entry<String, Object> entry = (Entry<String, Object>) jsonNode;

			//-- OperatorNode or PropertyNode --//
			String key = entry.getKey();
			WithSingleChildNode withSingleChildNode = new PropertyNode(key);

			//-- recursion --//
			AbstractNode child = toNode(entry.getValue());
			if (child != null) {
				withSingleChildNode.addChild(child);
			}
			return withSingleChildNode;
		} else {
			//-- EqualsValueNode --//
			return new EqualsValueNode(jsonNode);
		}
	}

	public AbstractNode getNode() {
		return this.node;
	}
}
