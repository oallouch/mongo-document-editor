package com.oallouch.mongodoc.util;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class StringBuilderCollector implements Collector<Object, StringBuilder, StringBuilder>{
	private StringBuilder builder;
	private String delimiter;
	private boolean firstCallDone;
	
	public StringBuilderCollector(StringBuilder builder, String delimiter) {
		this.builder = builder;
	}
	
	@Override
	public Supplier<StringBuilder> supplier() {
		return () -> builder;
	}

	@Override
	public BiConsumer<StringBuilder, Object> accumulator() {
		return (b, o) -> {
			if (firstCallDone) {
				if (delimiter != null) {
					b.append(delimiter);
				}
			} else {
				firstCallDone = true;
			}
			b.append(o);
		};
	}

	@Override
	public BinaryOperator<StringBuilder> combiner() {
		return (b1, b2) -> b1.append(b2);
	}

	@Override
	public Function<StringBuilder, StringBuilder> finisher() {
		return Function.identity();
	}

	@Override
	public Set<Characteristics> characteristics() {
		return Collections.emptySet();
	}
	
}
