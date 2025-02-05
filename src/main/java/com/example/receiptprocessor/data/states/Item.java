package com.example.receiptprocessor.data.states;

import com.example.receiptprocessor.utility.Collections;
import com.example.receiptprocessor.utility.Shorthand;
import com.fasterxml.jackson.databind.JsonNode;
import io.vavr.Lazy;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.util.Pair;

import java.util.List;

import static com.example.receiptprocessor.data.Constants.MATCHED_SCHEMA;
import static com.example.receiptprocessor.data.Constants.UNRECOGNIZED_PROBLEM;

public class Item {

	private Item() {
		throw new IllegalStateException("Utility class");
	}

	// happy path
	public static @NotNull Pair<Lazy<Boolean>, Lazy<List<Pair<String, String>>>>
	success(List<Throwable> castingFailures, List<Pair<String, String>> validationFailures) {
		return Shorthand.makeLazyStatePair(
						() -> castingFailures.isEmpty() && validationFailures.isEmpty(),
						() -> List.of(Pair.of(MATCHED_SCHEMA, "Valid json, yay!")));
	}

	// Convert throwable to an error pair
	public static List<Pair<String, String>> convertThrowableToStateMaps(@NotNull List<Throwable> castingFailures) {
		return castingFailures.stream()
						.map(throwable -> Pair.of(throwable.getClass().toString(), throwable.getMessage()))
						.toList();
	}

	// If there are casting and validation failures
	public static @NotNull Pair<Lazy<Boolean>, Lazy<List<Pair<String, String>>>>
	multipleFailures(@NotNull List<Throwable> castingFailures, List<Pair<String, String>> validationFailures) {
		var castingFailurePairs = Item.convertThrowableToStateMaps(castingFailures);
		var combinedLists = Collections.combine(List.of(castingFailurePairs, validationFailures));
		return Shorthand.makeLazyStatePair(
						() -> !castingFailures.isEmpty() && !validationFailures.isEmpty(),
						combinedLists::toList);
	}

	// Failed when trying to cast item price to big decimal
	public static @NotNull Pair<Lazy<Boolean>, Lazy<List<Pair<String, String>>>>
	couldNotCastPrice(List<Throwable> castingFailures) {
		return Shorthand.makeLazyStatePair(
						() -> !castingFailures.isEmpty(),
						() -> convertThrowableToStateMaps(castingFailures));
	}

	// Item didn't match schema
	public static @NotNull Pair<Lazy<Boolean>, Lazy<List<Pair<String, String>>>>
	didNotMatchSchema(List<Pair<String, String>> validationFailures) {
		return Shorthand.makeLazyStatePair(
						() -> !validationFailures.isEmpty(),
						() -> validationFailures);
	}

	public static @NotNull Pair<Lazy<Boolean>, Lazy<List<Pair<String, String>>>>
	unhandledProblem(JsonNode items) {
		return Shorthand.makeLazyStatePair(
						() -> true,
						() -> List.of(Pair.of(UNRECOGNIZED_PROBLEM, "Issue not recognized, dumping info -> " + items.toString())));
	}
}
