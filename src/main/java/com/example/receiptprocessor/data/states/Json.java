package com.example.receiptprocessor.data.states;

import com.example.receiptprocessor.utility.Shorthand;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.networknt.schema.ValidationMessage;
import io.vavr.Lazy;
import io.vavr.control.Try;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.util.Pair;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static org.hibernate.validator.internal.metadata.core.ConstraintHelper.MESSAGE;

/**
 * This class encompasses, currently, all recognized states we could encounter when
 * specifically working with json
 */
public class Json {

	public static final String MATCHED_SCHEMA = "Matched Schema";
	public static final String INVALID_SCHEMA = "Invalid Schema";
	public static final String NO_FILE = "No File";
	public static final String MALFORMED_JSON = "Malformed Json";
	public static final String UNRECOGNIZED_PROBLEM = "Unrecognized Problem";

	private Json() {
		throw new IllegalStateException("Utility class");
	}

	public static @NotNull Pair<Lazy<Boolean>, Lazy<Pair<String, String>>>
	matchesSchema(@NotNull Try<Set<ValidationMessage>> validationResultOrFailure) {
		return Shorthand.makeLazyStatePair(
						() -> validationResultOrFailure.isSuccess() && validationResultOrFailure.get().isEmpty(),
						() -> Pair.of(MATCHED_SCHEMA, "Valid json, yay!"));
	}

	public static @NotNull Pair<Lazy<Boolean>, Lazy<Pair<String, String>>>
	invalidSchema(@NotNull Try<Set<ValidationMessage>> validationResultOrFailure) {
		var message = Map.of(
						MESSAGE, "Invalid json, refer to details for more info.",
						"details", validationResultOrFailure.get().toString());
		return Shorthand.makeLazyStatePair(
						() -> validationResultOrFailure.isSuccess() && !validationResultOrFailure.get().isEmpty(),
						() -> Pair.of(INVALID_SCHEMA, "Json doesn't match schema. Details: " + message));
	}

	public static @NotNull Pair<Lazy<Boolean>, Lazy<Pair<String, String>>>
	noSchemaFile(Try<Set<ValidationMessage>> validationResultOrFailure) {
		return Shorthand.makeLazyStatePair(
						() -> validationResultOrFailure.getCause() instanceof IOException,
						() -> Pair.of(NO_FILE, "Could not find file, could not build json schema"));
	}

	public static @NotNull Pair<Lazy<Boolean>, Lazy<Pair<String, String>>>
	malformed(Try<Set<ValidationMessage>> validationResultOrFailure) {
		return Shorthand.makeLazyStatePair(
						() -> {
							var exceptionType = validationResultOrFailure.getCause().getClass();
							return Set.of(JsonProcessingException.class, JsonMappingException.class).contains(exceptionType);
						},
						() -> Pair.of(MALFORMED_JSON, "Problem building tree, invalid json"));
	}

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
