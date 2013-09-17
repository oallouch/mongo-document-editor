package com.oallouch.mongodoc.util.db;

import com.mongodb.BasicDBList;

public class DBL extends BasicDBList {

    public DBL() {
    }

    public DBL(Object... items) {
        for (Object item : items) {
            add(item);
        }
    }
}
