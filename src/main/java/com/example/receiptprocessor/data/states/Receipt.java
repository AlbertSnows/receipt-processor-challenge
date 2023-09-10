package com.example.receiptprocessor.data.states;

import com.example.receiptprocessor.data.records.SimpleHTTPResponse;
import com.example.receiptprocessor.utility.Shorthand;
import com.networknt.schema.ValidationMessage;
import io.vavr.Lazy;
import io.vavr.control.Try;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.Set;

import static org.hibernate.validator.internal.metadata.core.ConstraintHelper.MESSAGE;

/**
 * This class encompasses, currently, all recognized states we could encounter when
 * specifically working with our receipts data
 */
public class Receipt {
	private Receipt() {
		throw new IllegalStateException("Utility class");
	}
	public static Pair<Lazy<Boolean>, Lazy<SimpleHTTPResponse>> created(Try<Set<ValidationMessage>> validationResultOrFailure) {
		return Shorthand.makeLazyStatePair(
						() -> validationResultOrFailure.isSuccess() && validationResultOrFailure.get().isEmpty(),
						() -> new SimpleHTTPResponse(HttpStatus.CREATED, Map.of(MESSAGE, "created")));
	}

	public static SimpleHTTPResponse
	mapJsonResultsToReceiptResponse(@NotNull Pair<String, String> validationOutcome) {
		return Map.of(
						Json.INVALID_SCHEMA, new SimpleHTTPResponse(
										HttpStatus.BAD_REQUEST,
										Map.of(validationOutcome.getFirst(), validationOutcome.getSecond())),
						Json.MALFORMED_JSON, new SimpleHTTPResponse(
										HttpStatus.BAD_REQUEST,
										Map.of(validationOutcome.getFirst(), validationOutcome.getSecond())),
						Json.NO_FILE, new SimpleHTTPResponse(
										HttpStatus.INTERNAL_SERVER_ERROR,
										Map.of(validationOutcome.getFirst(), validationOutcome.getSecond())),
						Json.UNRECOGNIZED_PROBLEM, new SimpleHTTPResponse(
										HttpStatus.INTERNAL_SERVER_ERROR,
										Map.of(validationOutcome.getFirst(), validationOutcome.getSecond()))).get(validationOutcome.getFirst());
	}
}
