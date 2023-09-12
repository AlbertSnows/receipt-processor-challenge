package com.example.receiptprocessor.utility;

import io.vavr.Lazy;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class Collections {
	private Collections() {
		throw new IllegalStateException("Utility class");
	}

	public static <V> Stream<V> combine(@NotNull List<List<V>> collectionOfCollections) {
		return collectionOfCollections.stream()
						.flatMap(List::stream);
	}

	/**
	 * @deprecated suspect, maybe just use stream?
	 * causes issues if trying to add to immutable lists
	 * streams seem to be better for this
	 */
	@Contract(pure = true)
	@Deprecated(since = "sept 11")
	public static <V> @NotNull Function<V, List<V>> add(List<V> list) {
		return item -> {
			list.add(item);
			return list;
		};
	}

	/** This is an eager implementation of firstTrueStateOf.
	 *  firstTrueStateOf is just meant to provide access to a declarative alternative
	 *  to if-else-if styles of coding
	 *  It acts as a very rudimentary implementation of pattern matching.
	 *  For an example of the spirit of what this function is trying to capture,
	 *  look at an example in languages like scala:
	 *  <a href="https://docs.scala-lang.org/tour/pattern-matching.html">PM in Scala</a>
	 * @param stateMap is a list of (P)airs (tuples) where
	 * (L)eft is a boolean
	 * (R)ight is any associated value that match all other R's in the list
	 * L is meant to represent application state; a possible situation your code can be in
	 * R is meant to encompass what you want to accomplish *if* L were true
	 * As such, P represents pairs of state => value relationships such that
	 * @return is represented by R, the value you want when L is *true*
	 */
	public static <V> @NotNull V firstTrueEagerStateOf(@NotNull List<Pair<Boolean, V>> stateMap) {
		for (Pair<Boolean, V> statePair : stateMap) {
			var value = statePair.getFirst();
			if (Boolean.TRUE.equals(value)) {
				return statePair.getSecond();
			}
		}
		throw new IllegalArgumentException("You must provide a default case as the last argument");
	}

	/** This is a lazy implementation of firstTrueStateOf (and the default)
	 * refer to eager above for more information about the motivation of this function
	 * @param stateMap is a list of Pairs where
	 * L is a *0 arity* (i.e. lazy) function that returns a bool
	 * R is a lazy function that returns a value identical to all other values in a list
	 * @return represents a lazy evaluation of what you want in state L, encompassed by R
	 * The goal of laziness is to represent values without actually doing the expensive
	 * calculations necessary to get them, which saves on computation.
	 * For more examples of laziness, refer to Vavr's documentation; there's also plenty
	 * of other resources online
	 * <a href="https://javadoc.io/doc/io.vavr/vavr/0.9.2/io/vavr/Lazy.html">Laziness</a>
	 */
	public static <V> @NotNull Lazy<V> firstTrueStateOf(@NotNull List<Pair<Lazy<Boolean>, Lazy<V>>> stateMap) {
		for (Pair<Lazy<Boolean>, Lazy<V>> statePair : stateMap) {
			var value = statePair.getFirst().get();
			if (Boolean.TRUE.equals(value)) {
				return statePair.getSecond();
			}
		}
		throw new IllegalArgumentException("You must provide a default case as the last argument");
	}

	/**
	 * @deprecated not necessary?
	 */
	@Deprecated(since = "sept 11")
	public static @NotNull String makeStateString(@NotNull Pair<String, String> pair) {
		return "State pair: (" + pair.getFirst() + ", " + pair.getSecond() + ")\n";
	}
}
