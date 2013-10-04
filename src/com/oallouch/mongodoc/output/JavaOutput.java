package com.oallouch.mongodoc.output;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.oallouch.mongodoc.util.StringBuilderCollector;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

public class JavaOutput extends BorderPane {
	private TextArea textArea;
	private StringBuilder builder;
	
	public JavaOutput() {
		this.textArea = new TextArea();
		textArea.setEditable(false);
		setCenter(textArea);
	}
	
	public void setRootJsonObject(Map<String, Object> jsonObject) {
		this.builder = new StringBuilder(1000);
		appendDBO(jsonObject, 0);
		textArea.setText(builder.toString());
		builder = null;
	}
	
	private JavaOutput append(String text) {
		builder.append(text);
		return this;
	}
	
	private JavaOutput appendIndent(int indentLevel) {
		for (int i = 0; i < indentLevel; i++) {
			builder.append("\t");
		}
		return this;
	}
	
	/**
	 * @param jsonObject
	 * @param indentLevel the indent level of this "new DBO("
	 * @return 
	 */
	private JavaOutput appendDBO(Map<String, Object> jsonObject, int indentLevel) {
		append("new DBO(\n");
		boolean firstDone = false;
		indentLevel++;
		for (Entry<String, Object> propertyEntry : jsonObject.entrySet()) {
			if (firstDone) {
				append(",\n");
			} else {
				firstDone = true;
			}
			appendIndent(indentLevel).append("\"").append(propertyEntry.getKey()).append("\", ")
			.appendValue(propertyEntry.getValue(), indentLevel);
		}
		append("\n").appendIndent(--indentLevel).append(")");
		return this;
	}
	
	private JavaOutput appendDBL(List<Object> jsonArray, int indentLevel) {
		append("new DBL(\n");
		boolean firstDone = false;
		indentLevel++;
		for (Object value : jsonArray) {
			if (firstDone) {
				append(",\n");
			} else {
				firstDone = true;
			}
			appendIndent(indentLevel).appendValue(value, indentLevel);
		}
		append("\n").appendIndent(--indentLevel).append(")");
		return this;
	}
	
	private JavaOutput appendValue(Object value, int indentLevel) {
		if (value == null) {
			append("null");
		} else if (value instanceof Map) {
			appendDBO((Map<String, Object>) value, indentLevel);
		} else if (value instanceof List) {
			appendDBL((List<Object>) value, indentLevel);
		} else if (value instanceof String) {
			append("\"").append((String) value).append("\"");
		} else {
			append(value.toString());
		}
		return this;
	}
}
