package com.oallouch.mongodoc.output;

public class JavaOutput extends AbstractOutput {
	@Override
	protected String getObjectPrefix() {
		return "new DBO(";
	}
	@Override
	protected String getObjectSuffix() {
		return ")";
	}

	@Override
	protected String getNameValueSeparator() {
		return ", ";
	}
	@Override
	protected String getArrayPrefix() {
		return "new DBL(";
	}
	@Override
	protected String getArraySuffix() {
		return ")";
	}
	@Override
	protected String getQuote() {
		return "\"";
	}
}
