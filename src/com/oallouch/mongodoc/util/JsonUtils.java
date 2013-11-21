package com.oallouch.mongodoc.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.util.JSON;
import com.mongodb.util.JSONCallback;
import com.mongodb.util.JSONSerializers;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class JsonUtils {
	private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

	private static final JsonFactory JSON_FACTORY = new JsonFactory();

	static {
		JSON_FACTORY.configure(
			JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		JSON_FACTORY.configure(
			JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
	}
	
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper(JSON_FACTORY);
	
	/*static {
		//----------------- Calendar for JavaScript ------------------//
		// it's not Dates because we need a TimeZone
		SimpleModule module = new SimpleModule("DatesForJavaScript", new com.fasterxml.jackson.core.Version(0, 1, 0, "alpha"));
		// functionality includes ability to register serializers, deserializers, add mix-in annotations etc:
		module.addSerializer(Date.class, new JsonSerializer<Date>() {
			@Override
			public void serialize(Date value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
				StringBuilder builder = new StringBuilder(50);
				builder.append("new Date(")
					.append(value.get(Calendar.YEAR)).append(", ")
					.append(value.get(Calendar.MONTH)).append(", ")
					.append(value.get(Calendar.DAY_OF_MONTH)).append(", ")
					.append(value.get(Calendar.HOUR_OF_DAY)).append(", ")
					.append(value.get(Calendar.MINUTE)).append(", ")
					.append(value.get(Calendar.SECOND)).append(")");
				
				jgen.writeRawValue(builder.toString()); // not writeRaw() (for an eventual separator)
			}
		});
		// and the magic happens here when we register module with mapper:
		OBJECT_MAPPER_FOR_JAVASCRIPT.registerModule(module);
	}*/
	
	public static Object parse(String str) throws JsonParseException {
		try {
			return OBJECT_MAPPER.readValue(str, Object.class);
		} catch (IOException e) {
			if (e instanceof JsonParseException) {
				throw (JsonParseException) e;
			}
			throw new RuntimeException(e);
		}
	}
	
	public static JsonFactory getJsonFactory() {
		return JSON_FACTORY;
	}
	
	public static String format(Object obj, boolean prettyOutput) {
		StringWriter stringWriter = new StringWriter(512);
		format(obj, stringWriter, prettyOutput);
		return stringWriter.toString();
	}
	public static void format(Object obj, Writer writer, boolean prettyOutput) {
		try {
			JsonGenerator gen = JSON_FACTORY.createGenerator(writer);
			if (prettyOutput) {
				gen.useDefaultPrettyPrinter();
			}
			OBJECT_MAPPER.writeValue(gen, obj);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Map<String, Object> toJsonObject(String text) throws JsonParseException {
		return (Map) parse(text);
	}
	
	public static String toJsonTextPretty(Map<String, Object> jsonObject) {
		return format(jsonObject, true);
	}
	
	/**
	 * @param jsonObject this argument isn't modified
	 * @return a copy
	 */
	public static Map<String, Object> removeSpecialJavaTypes(Map<String, Object> jsonObject) {
		// . JSON always uses legacy
		// . anyway, we use legacy because it output more readable $date tags (a String instead of a numeric timestamp)
		String jsonText = JSONSerializers./*getStrict()*/getLegacy().serialize(jsonObject);
		try {
			return (Map<String, Object>) parse(jsonText);
		} catch (Throwable e) {
			// never happens
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * @param jsonObject this argument isn't modified
	 * @return a copy
	 */
	public static Map<String, Object> putSpecialJavaTypes(Map<String, Object>  jsonObject) {
		String jsonText = format(jsonObject, false);
		return (Map<String, Object>) JSON.parse(jsonText);
	}
}
