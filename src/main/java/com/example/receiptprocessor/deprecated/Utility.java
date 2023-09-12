package com.example.receiptprocessor.deprecated;

import com.example.receiptprocessor.data.records.SimpleHTTPResponse;
import com.example.receiptprocessor.utility.Collections;
import com.example.receiptprocessor.utility.Shorthand;
import com.networknt.schema.ValidationMessage;
import io.vavr.Function1;
import io.vavr.Lazy;
import io.vavr.control.Try;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static com.example.receiptprocessor.data.Constants.*;
import static org.hibernate.validator.internal.metadata.core.ConstraintHelper.MESSAGE;

/**
 * For documentation purposes
 */
@SuppressWarnings("java:S1133")
public class Utility {
	private Utility() {
		throw new IllegalStateException("Utility class");
	}

	public static @org.jetbrains.annotations.NotNull Pair<Lazy<Boolean>, Lazy<SimpleHTTPResponse>>
	created(Try<Set<ValidationMessage>> validationResultOrFailure) {
		return Shorthand.makeLazyStatePair(
						() -> validationResultOrFailure.isSuccess() && validationResultOrFailure.get().isEmpty(),
						() -> new SimpleHTTPResponse(HttpStatus.CREATED, Map.of(MESSAGE, "created")));
	}

	public static SimpleHTTPResponse
	mapJsonResultsToReceiptResponse(@jakarta.validation.constraints.NotNull @org.jetbrains.annotations.NotNull Pair<String, String> validationOutcome) {
		var details = Map.of(validationOutcome.getFirst(), validationOutcome.getSecond());
		return Map.of(
										INVALID_SCHEMA, new SimpleHTTPResponse(HttpStatus.BAD_REQUEST, details),
										MALFORMED_JSON, new SimpleHTTPResponse(HttpStatus.BAD_REQUEST, details),
										NO_FILE, new SimpleHTTPResponse(HttpStatus.INTERNAL_SERVER_ERROR, details),
										UNRECOGNIZED_PROBLEM, new SimpleHTTPResponse(HttpStatus.INTERNAL_SERVER_ERROR, details),
										MATCHED_SCHEMA, new SimpleHTTPResponse(HttpStatus.CREATED, details))
						.get(validationOutcome.getFirst());
	}

	/**
	 * @deprecated has weird casting behavior, generics are tough to work with
	 */
	@Contract(pure = true)
	@Deprecated(since = "sept 11")
	public static <V> @NotNull Function1<Map<String, V>, V>
	actOnProcessReceiptValidationOutcomes(boolean validReceipt, boolean validItems) {
		return actions -> Collections.firstTrueEagerStateOf(List.of(
						Pair.of(validReceipt && validItems, actions.get(BOTH_VALID)),
						Pair.of(validReceipt, actions.get(RECEIPT_VALID)),
						Pair.of(validItems, actions.get(ITEM_VALID)),
						Pair.of(true, actions.get(NEITHER_VALID))));
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

	/**
	 * @deprecated not necessary?
	 */
	@Deprecated(since = "sept 11")
	public static @NotNull String makeStateString(@NotNull Pair<String, String> pair) {
		return "State pair: (" + pair.getFirst() + ", " + pair.getSecond() + ")\n";
	}
}
