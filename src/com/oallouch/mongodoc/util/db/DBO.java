package com.oallouch.mongodoc.util.db;

import com.mongodb.BasicDBObject;

public class DBO extends BasicDBObject {

    public DBO() {
    }

    public DBO(String key, Object value) {
        super(key, value);
    }

    public DBO(
        String key1, Object value1,
        String key2, Object value2) {
        super(4);
        put(key1, value1);
        put(key2, value2);
    }

    public DBO(
        String key1, Object value1,
        String key2, Object value2,
        String key3, Object value3) {
        super(8);
        put(key1, value1);
        put(key2, value2);
        put(key3, value3);
    }

    public DBO(
        String key1, Object value1,
        String key2, Object value2,
        String key3, Object value3,
        String key4, Object value4) {
        super(8);
        put(key1, value1);
        put(key2, value2);
        put(key3, value3);
        put(key4, value4);
    }

    public DBO(
        String key1, Object value1,
        String key2, Object value2,
        String key3, Object value3,
        String key4, Object value4,
        String key5, Object value5) {
        super(8);
        put(key1, value1);
        put(key2, value2);
        put(key3, value3);
        put(key4, value4);
        put(key5, value5);
    }
}
