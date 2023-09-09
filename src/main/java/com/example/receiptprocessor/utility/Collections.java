package com.example.receiptprocessor.utility;

import io.vavr.Lazy;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.util.Pair;

import java.util.List;

public class Collections {
	private Collections() {
		throw new IllegalStateException("Utility class");
	}

	/** getFirstTrue is just meant to provide access to a declarative alternative
	 *  to if-else-if styles of coding
	 *  It acts as a very rudimentary implementation of pattern matching.
	 *  For an example of the spirit of what this function is trying to capture,
	 *  look at an example in languages like scala:
	 *  <a href="https://docs.scala-lang.org/tour/pattern-matching.html">PM in Scala</a>
	 * @param stateMap
	 * stateMap is a list of Pairs (Tuples) where L is a 0 arity (i.e. lazy) function that returns a bool and
	 * R is a lazy function that returns a value identical to all other values in a list
	 * L is meant to represent application state; a possible situation your code can be in
	 * R is meant to encompass what you want to accomplish *if* L were true
	 * @return As such, the return of this function represents a lazy evaluation of what
	 *  you want in state L
	 * The goal of laziness is to represent values without actually doing the expensive
	 * calculations necessary to get them, which saves on computation.
	 * For more examples of laziness, refer to Vavr's documentation; there's also plenty
	 * of other resources online
	 * <a href="https://javadoc.io/doc/io.vavr/vavr/0.9.2/io/vavr/Lazy.html">Laziness</a>
	 * @param <V> any identical value
	 */
	public static <V> Lazy<V> getFirstTrue(@NotNull List<Pair<Lazy<Boolean>, Lazy<V>>> stateMap) {
		for (Pair<Lazy<Boolean>, Lazy<V>> statePair : stateMap) {
			var value = statePair.getFirst().get();
			if (Boolean.TRUE.equals(value)) {
				return statePair.getSecond();
			}
		}
		throw new IllegalArgumentException("You must provide a default case as the last argument");
	}
}
