package com.oallouch.mongodoc.tree;

import com.oallouch.mongodoc.tree.cell.TypeColumnCell;
import com.google.common.base.Function;
import com.google.common.collect.Maps;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public enum DataType {
	NULL("Null", null, (java.lang.Object v) -> null, null),
	STRING("String", String.class, (java.lang.Object v) -> String.valueOf(v), ""),
	DOUBLE("Double/Float", Double.class, (java.lang.Object v) -> {
		if (v instanceof Number) {
			return ((Number) v).doubleValue();
		} else if (v instanceof String) {
			return Double.valueOf((String) v);
		} else {
			throw new IllegalArgumentException("Not a Double: " + v);
		}
	}, new Double(0)),
	LONG("Long/Integer", Long.class, (java.lang.Object v) -> {
		if (v instanceof Number) {
			return ((Number) v).longValue();
		} else if (v instanceof String) {
			return Long.valueOf((String) v);
		} else {
			throw new IllegalArgumentException("Not a Long: " + v);
		}
	}, new Long(0)),
	BOOLEAN("Boolean", Boolean.class, (java.lang.Object v) -> {
		if (v instanceof Boolean) {
			return (Boolean) v;
		} else if (v instanceof String) {
			return Boolean.valueOf((String) v);
		} else {
			throw new IllegalArgumentException("Not a Boolean: " + v);
		}
	}, Boolean.TRUE),
	DATE("Date", Date.class, (java.lang.Object v) -> new Date(), null),
	PATTERN("Pattern", Pattern.class, (java.lang.Object v) -> {
		if (v instanceof Pattern) {
			return (Pattern) v;
		} else if (v instanceof String) {
			return Pattern.compile((String) v);
		} else {
			throw new IllegalArgumentException("Not a Pattern: " + v);
		}
	}, Pattern.compile(""));
	
	private String text;
	private Class valueClass;
	private Function<Object, Object> converter;
	private Object defaultValue;

	DataType(String text, Class valueClass, Function<Object, Object> converter, Object defaultValue) {
		this.text = text;
		this.valueClass = valueClass;
		this.converter = converter;
		this.defaultValue = defaultValue;
	}

	public Object toValueOfType(Object value) {
		try {
			return converter.apply(value);
		} catch (Throwable e) {
			Logger.getLogger(TypeColumnCell.class.getName()).log(Level.INFO, "Bad value for this type: " + value);
			return defaultValue;
		}
	}

	@Override
	public String toString() {
		return text;
	}

	public String getText() {
		return text;
	}

	public Class getValueClass() {
		return valueClass;
	}
	
	public static final Map<Class, DataType> DATA_TYPE_BY_VALUE_CLASS = Maps.newHashMapWithExpectedSize(8);
	
	static {
		for (DataType dataType : DataType.values()) {
			Class valueClass = dataType.getValueClass();
			if (valueClass != null) {
				DATA_TYPE_BY_VALUE_CLASS.put(valueClass, dataType);
			}
		}
	}
	
	public static DataType getDataType(Object value) {
		if (value == null) {
			return DataType.NULL;
		}
		DataType dataType = DATA_TYPE_BY_VALUE_CLASS.get(value.getClass());
		if (dataType != null) {
			return dataType;
		}
		if (value instanceof Integer) {
			return DataType.LONG;
		}
		if (value instanceof Float) {
			return DataType.DOUBLE;
		}
		return null;
	}
}
