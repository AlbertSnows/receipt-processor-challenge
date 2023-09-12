package com.example.receiptprocessor.utility;

import io.vavr.Function0;
import io.vavr.Lazy;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.util.Pair;

/**
 * Quick ways to do simple repetitive things
 */
public class Shorthand {
	private Shorthand() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * A lazy pair means that when you want to get the left or right, it calculates it
	 * Until then, the values aren't calculated
	 */
	public static <L, R> @NotNull Pair<Lazy<L>, Lazy<R>> makeLazyPair(Function0<L> left, Function0<R> right) {
		return Pair.of(Lazy.of(left), Lazy.of(right));
	}

	/**
	 * A state pair will always be in the form Pair<Boolean, R>.
	 * For more information on state pairs, refer to the documentation.
	 */
	public static <R> @NotNull Pair<Lazy<Boolean>, Lazy<R>> makeLazyStatePair(Function0<Boolean> isInState, Function0<R> action) {
		return makeLazyPair(isInState, action);
	}
}
