package com.oallouch.mongodoc.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Calendar;
import java.util.Map;

public class JsonUtils {

	private static final JsonFactory JSON_FACTORY = new JsonFactory();

	static {
		JSON_FACTORY.configure(
			JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		JSON_FACTORY.configure(
			JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
	}
	
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper(JSON_FACTORY);
	private static final ObjectMapper OBJECT_MAPPER_FOR_JAVASCRIPT = new ObjectMapper(JSON_FACTORY);
	
	static {
		//----------------- Calendar for JavaScript ------------------//
		// it's not Dates because we need a TimeZone
		SimpleModule module = new SimpleModule("CalendarsForJavaScript", new com.fasterxml.jackson.core.Version(0, 1, 0, "alpha"));
		// functionality includes ability to register serializers, deserializers, add mix-in annotations etc:
		module.addSerializer(Calendar.class, new JsonSerializer<Calendar>() {
			@Override
			public void serialize(Calendar value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
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
	}
	
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
			JsonGenerator gen = JSON_FACTORY.createJsonGenerator(writer);
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
	
	public static String toJsonText(Object jsonObject) {
		return format(jsonObject, true);
	}
}
