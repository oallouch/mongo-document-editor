package com.oallouch.mongodoc.output;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PhpOutput extends AbstractOutput {
	private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Override
	protected String getObjectPrefix() {
		return "array(";
	}
	@Override
	protected String getObjectSuffix() {
		return ")";
	}
	@Override
	protected String getNameValueSeparator() {
		return " => ";
	}
	@Override
	protected String getArrayPrefix() {
		return "array(";
	}
	@Override
	protected String getArraySuffix() {
		return ")";
	}
	@Override
	protected String getQuote() {
		return "'";
	}

	@Override
	protected void appendDate(Date date) {
		append("new MongoDate(strtotime(\"").append(DATE_FORMAT.format(date)).append("\"))");
	}
}
