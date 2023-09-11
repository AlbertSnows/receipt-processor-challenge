package com.example.receiptprocessor.data.states;

import com.example.receiptprocessor.data.entities.Points;
import com.example.receiptprocessor.data.records.SimpleHTTPResponse;
import com.example.receiptprocessor.utility.Shorthand;
import com.networknt.schema.ValidationMessage;
import io.vavr.Function0;
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

	public static @org.jetbrains.annotations.NotNull Pair<Lazy<Boolean>, Lazy<SimpleHTTPResponse>>
	created(Try<Set<ValidationMessage>> validationResultOrFailure) {
		return Shorthand.makeLazyStatePair(
						() -> validationResultOrFailure.isSuccess() && validationResultOrFailure.get().isEmpty(),
						() -> new SimpleHTTPResponse(HttpStatus.CREATED, Map.of(MESSAGE, "created")));
	}

	public static SimpleHTTPResponse
	mapJsonResultsToReceiptResponse(@NotNull @org.jetbrains.annotations.NotNull Pair<String, String> validationOutcome) {
		var details = Map.of(validationOutcome.getFirst(), validationOutcome.getSecond());
		return Map.of(
						INVALID_SCHEMA, new SimpleHTTPResponse(HttpStatus.BAD_REQUEST, details),
						MALFORMED_JSON, new SimpleHTTPResponse(HttpStatus.BAD_REQUEST, details),
						NO_FILE, new SimpleHTTPResponse(HttpStatus.INTERNAL_SERVER_ERROR, details),
						UNRECOGNIZED_PROBLEM, new SimpleHTTPResponse(HttpStatus.INTERNAL_SERVER_ERROR, details),
						MATCHED_SCHEMA, new SimpleHTTPResponse(HttpStatus.CREATED, details))
						.get(validationOutcome.getFirst());
	}

	public static @org.jetbrains.annotations.NotNull Pair<Lazy<Boolean>, Lazy<SimpleHTTPResponse>>
	getPoints(@org.jetbrains.annotations.NotNull Function0<Points> getPoints) {
		var points = getPoints.get();
		return Shorthand.makeLazyStatePair(
						() -> points != null,
						() -> new SimpleHTTPResponse(HttpStatus.OK,
										Map.of("points", points.getPoints().toString())));
	}

	public static @org.jetbrains.annotations.NotNull Pair<Lazy<Boolean>, Lazy<SimpleHTTPResponse>>
	calculatePoints(Function0<Points> points, Lazy<Integer> calcPoints) {
		//todo: reorginize state
		//todo: move point calc to point write service
		//todo: check that inner functions aren't eagerly evaluated
		return Shorthand.makeLazyStatePair(
						() -> points.get() == null,
						() -> new SimpleHTTPResponse(HttpStatus.OK,
										Map.of("points", calcPoints.get().toString())));
	}

	public static @org.jetbrains.annotations.NotNull Pair<Lazy<Boolean>, Lazy<SimpleHTTPResponse>>
	idNotFound(com.example.receiptprocessor.data.entities.Receipt receipt) {
		return Shorthand.makeLazyStatePair(
						() -> receipt == null,
						() -> new SimpleHTTPResponse(HttpStatus.NOT_FOUND,
										Map.of("error", "No receipt associated with provided id.")));
	}




}
