package com.example.receiptprocessor.utility;

import io.vavr.Function0;
import io.vavr.Lazy;
import org.springframework.data.util.Pair;

public class Shorthand {
	private Shorthand() {
		throw new IllegalStateException("Utility class");
	}

	public static <L, R> Pair<Lazy<L>, Lazy<R>> makeLazyPair(Function0<L> left, Function0<R> right) {
		return Pair.of(Lazy.of(left), Lazy.of(right));
	}

	public static <R> Pair<Lazy<Boolean>, Lazy<R>> makeLazyStatePair(Function0<Boolean> isInState, Function0<R> action) {
		return makeLazyPair(isInState, action);
	}
}
