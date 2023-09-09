package com.example.receiptprocessor.data.states;

import com.example.receiptprocessor.data.records.SimpleHTTPResponse;
import com.example.receiptprocessor.utility.Shorthand;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.networknt.schema.ValidationMessage;
import io.vavr.Lazy;
import io.vavr.control.Try;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static org.hibernate.validator.internal.metadata.core.ConstraintHelper.MESSAGE;

public class Json {
	private Json() {
		throw new IllegalStateException("Utility class");
	}
	public static Pair<Lazy<Boolean>, Lazy<SimpleHTTPResponse>> invalid(Try<Set<ValidationMessage>> validationResultOrFailure) {
		var message = Map.of(
						MESSAGE, "Invalid json, refer to details for more info.",
						"details", validationResultOrFailure.get().toString());
		return Shorthand.makeLazyStatePair(
						() -> validationResultOrFailure.isSuccess() && !validationResultOrFailure.isEmpty(),
						() -> new SimpleHTTPResponse(HttpStatus.BAD_GATEWAY, message));
	}

	public static Pair<Lazy<Boolean>, Lazy<SimpleHTTPResponse>> noSchemaFile(Try<Set<ValidationMessage>> validationResultOrFailure) {
		return Shorthand.makeLazyStatePair(
						() -> validationResultOrFailure.getCause() instanceof IOException,
						() -> new SimpleHTTPResponse(
										HttpStatus.INTERNAL_SERVER_ERROR,
										Map.of(MESSAGE, "Could not find file, could not build json schema")));
	}

	public static Pair<Lazy<Boolean>, Lazy<SimpleHTTPResponse>> malformed(Try<Set<ValidationMessage>> validationResultOrFailure) {
		var exceptionType = validationResultOrFailure.getCause().getClass();
		return Shorthand.makeLazyStatePair(
						() -> Set.of(JsonProcessingException.class, JsonMappingException.class).contains(exceptionType),
						() -> new SimpleHTTPResponse(
										HttpStatus.BAD_REQUEST,
										Map.of(MESSAGE, "Problem building tree, invalid json")));
	}

	public static Pair<Lazy<Boolean>, Lazy<SimpleHTTPResponse>> unknownProblem(Try<Set<ValidationMessage>> validationResultOrFailure) {
		var exceptionType = validationResultOrFailure.getCause().getClass();
		return Shorthand.makeLazyStatePair(
						() -> true,
						() -> new SimpleHTTPResponse(
										HttpStatus.INTERNAL_SERVER_ERROR,
										Map.of(MESSAGE, "Unrecognized problem trying to validate json.",
														"error", exceptionType.descriptorString())));
	}
}
