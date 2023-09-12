package com.example.receiptprocessor.data.states;

import com.example.receiptprocessor.data.records.SimpleHTTPResponse;
import com.example.receiptprocessor.utility.Shorthand;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.networknt.schema.ValidationMessage;
import io.vavr.Lazy;
import io.vavr.control.Try;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static com.example.receiptprocessor.data.Constants.*;

/**
 * This class encompasses, currently, all recognized states we could encounter when
 * specifically working with json for this project
 */
public class Json {
	private Json() {
		throw new IllegalStateException("Utility class");
	}

	public static @NotNull Pair<Lazy<Boolean>, Lazy<SimpleHTTPResponse>>
	invalidID(boolean idExists) {
		return Shorthand.makeLazyStatePair(
						() -> !idExists,
						() -> new SimpleHTTPResponse(HttpStatus.BAD_REQUEST,
										Map.of("error", "Invalid ID")));
	}
	/**
	 * Matches Schema -> there were no problems, happy path
	 */
	public static @NotNull Pair<Lazy<Boolean>, Lazy<Pair<String, String>>>
	matchesSchema(@NotNull Try<Set<ValidationMessage>> validationResultOrFailure) {
		return Shorthand.makeLazyStatePair(
						() -> validationResultOrFailure.isSuccess() && validationResultOrFailure.get().isEmpty(),
						() -> Pair.of(MATCHED_SCHEMA, "Valid json, yay!"));
	}
	/**
	 * Invalid Schema -> json does not match schema
	 */
	public static @NotNull Pair<Lazy<Boolean>, Lazy<Pair<String, String>>>
	invalidSchema(@NotNull Try<Set<ValidationMessage>> validationResultOrFailure) {
		return Shorthand.makeLazyStatePair(
						() -> validationResultOrFailure.isSuccess() && !validationResultOrFailure.get().isEmpty(),
						() -> Pair.of(INVALID_SCHEMA,
										"Json doesn't match schema. Details: " + validationResultOrFailure.get().toString()));
	}
	/**
	 * No Schema File -> could not find the file to validate against, this should not happen
	 */
	public static @NotNull Pair<Lazy<Boolean>, Lazy<Pair<String, String>>>
	noSchemaFile(Try<Set<ValidationMessage>> validationResultOrFailure) {
		return Shorthand.makeLazyStatePair(
						() -> validationResultOrFailure.getCause() instanceof IOException,
						() -> Pair.of(NO_FILE, "Could not find file, could not build json schema"));
	}

	/**
	 * Malformed -> could not parse json to validate
	 */
	public static @NotNull Pair<Lazy<Boolean>, Lazy<Pair<String, String>>>
	malformed(Try<Set<ValidationMessage>> validationResultOrFailure) {
		return Shorthand.makeLazyStatePair(
						() -> {
							var exceptionType = validationResultOrFailure.getCause().getClass();
							return Set.of(JsonProcessingException.class, JsonMappingException.class).contains(exceptionType);
						},
						() -> Pair.of(MALFORMED_JSON, "Problem building tree, invalid json"));
	}

	/**
	 * - Unknown Problem - catch all for all other problems that may not have been anticipated
	 */
	public static @NotNull Pair<Lazy<Boolean>, Lazy<Pair<String, String>>>
	unknownProblem(Try<Set<ValidationMessage>> validationResultOrFailure) {
		return Shorthand.makeLazyStatePair(
						() -> true,
						() -> {
							var exceptionType = validationResultOrFailure.getCause().getClass();
							return Pair.of(UNRECOGNIZED_PROBLEM,
											"Unrecognized problem trying to validate json: " + exceptionType.descriptorString());
						});
	}
}
