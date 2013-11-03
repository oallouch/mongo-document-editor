package com.oallouch.mongodoc.output;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

public abstract class AbstractOutput extends BorderPane {
	private TextArea textArea;
	private StringBuilder builder;
	
	public AbstractOutput() {
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
	
	private AbstractOutput append(String text) {
		builder.append(text);
		return this;
	}
	
	private AbstractOutput appendIndent(int indentLevel) {
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
	private AbstractOutput appendDBO(Map<String, Object> jsonObject, int indentLevel) {
		append(getObjectPrefix()).append("\n");
		boolean firstDone = false;
		indentLevel++;
		for (Entry<String, Object> propertyEntry : jsonObject.entrySet()) {
			if (firstDone) {
				append(",\n");
			} else {
				firstDone = true;
			}
			appendIndent(indentLevel).append(getQuote()).append(propertyEntry.getKey()).append(getQuote()).append(getNameValueSeparator())
			.appendValue(propertyEntry.getValue(), indentLevel);
		}
		append("\n").appendIndent(--indentLevel).append(getObjectSuffix());
		return this;
	}
	
	private AbstractOutput appendDBL(List<Object> jsonArray, int indentLevel) {
		append(getArrayPrefix()).append("\n");
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
		append("\n").appendIndent(--indentLevel).append(getArraySuffix());
		return this;
	}
	
	protected abstract String getObjectPrefix() ;
	protected abstract String getObjectSuffix() ;
	protected abstract String getNameValueSeparator() ;
	protected abstract String getArrayPrefix() ;
	protected abstract String getArraySuffix() ;
	protected abstract String getQuote() ;
	
	private AbstractOutput appendValue(Object value, int indentLevel) {
		if (value == null) {
			append("null");
		} else if (value instanceof Date) {
			appendDate((Date) value);
		} else if (value instanceof Map) {
			appendDBO((Map<String, Object>) value, indentLevel);
		} else if (value instanceof List) {
			appendDBL((List<Object>) value, indentLevel);
		} else if (value instanceof String) {
			append(getQuote()).append((String) value).append(getQuote());
		} else {
			append(value.toString());
		}
		return this;
	}
	
	protected void appendDate(Date date) {
		append("new Date(").append(Long.toString(date.getTime())).append(")");
	}
}
