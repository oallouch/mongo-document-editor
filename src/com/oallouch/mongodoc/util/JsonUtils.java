package com.oallouch.mongodoc.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.text.DateFormat;
import java.util.Map;

public class JsonUtils {
	private static Gson gson = new GsonBuilder()/*.setDateFormat(DateFormat.FULL)*/.setPrettyPrinting().create();
	
	public static Map<String, Object> toJsonObject(String text) {
		return gson.fromJson(text, Map.class);
	}
	
	public static String toJsonText(Object jsonObject) {
		return gson.toJson(jsonObject);
	}
}
