package com.example.receiptprocessor.data.states;

import com.example.receiptprocessor.data.records.SimpleHTTPResponse;
import com.example.receiptprocessor.utility.Shorthand;
import com.networknt.schema.ValidationMessage;
import io.vavr.Lazy;
import io.vavr.control.Try;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.Set;

import static com.example.receiptprocessor.data.Constants.*;
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
		var details = Map.of(validationOutcome.getFirst(), validationOutcome.getSecond());
		return Map.of(
						INVALID_SCHEMA, new SimpleHTTPResponse(HttpStatus.BAD_REQUEST, details),
						MALFORMED_JSON, new SimpleHTTPResponse(HttpStatus.BAD_REQUEST, details),
						NO_FILE, new SimpleHTTPResponse(HttpStatus.INTERNAL_SERVER_ERROR, details),
						UNRECOGNIZED_PROBLEM, new SimpleHTTPResponse(HttpStatus.INTERNAL_SERVER_ERROR, details),
						MATCHED_SCHEMA, new SimpleHTTPResponse(HttpStatus.CREATED, details))
						.get(validationOutcome.getFirst());
	}
}
