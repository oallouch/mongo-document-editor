package com.oallouch.mongodoc.output;

public class PhpOutput extends AbstractOutput {
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
}
