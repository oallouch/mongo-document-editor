package com.oallouch.mongodoc.node;

import java.util.List;
import java.util.Map;

public class NodeFactory {
	public static PropertiesNode toNode(Map<String, Object> jsonObject) {
		return (PropertiesNode) toNode((Object) jsonObject);
	}
	public static Map<String, Object> toJsonObject(PropertiesNode propertiesNode) {
		return (Map<String, Object>) propertiesNode.getJsonElement();
	}

	private static AbstractNode toNode(Object jsonElement) {
		if (jsonElement instanceof Map) {
			Map<String, Object> map = (Map<String, Object>) jsonElement;
			if (map.isEmpty()) {
				return null;
			}

			//-- PropertiesNode --//
			AbstractNode newNode = new PropertiesNode();

			//-- child entries --//
			//-- (recursion) --//
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				AbstractNode child = toNode(entry);
				newNode.addChild(child);
			}
			return newNode;
		} else if (jsonElement instanceof Map.Entry) {
			Map.Entry<String, Object> entry = (Map.Entry<String, Object>) jsonElement;

			//-- PropertyNode --//
			String key = entry.getKey();
			PropertyNode propertyNode = new PropertyNode(key);

			//-- recursion --//
			AbstractNode child = toNode(entry.getValue());
			if (child != null) {
				propertyNode.addChild(child);
			}
			return propertyNode;
		} else if (jsonElement instanceof List) {
			List list = (List) jsonElement;

			//-- ArrayNode --//
			ArrayNode arrayNode = new ArrayNode();
			for (Object listElement : list) {
				AbstractNode arrayValueNode = toNode(listElement);
				arrayNode.addArrayItem(arrayValueNode);
			}
			return arrayNode;
		} else {
			//-- EqualsValueNode --//
			return new EqualsValueNode(jsonElement);
		}
	}
}
