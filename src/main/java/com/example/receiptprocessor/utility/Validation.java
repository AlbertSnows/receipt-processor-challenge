package com.example.receiptprocessor.utility;

import com.example.receiptprocessor.data.states.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import io.vavr.Function1;
import io.vavr.Lazy;
import io.vavr.control.Try;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.util.Pair;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Validation {
	private Validation() {
		throw new IllegalStateException("Utility class");
	}

	@Contract(pure = true)
	public static @NotNull Function1<JsonNode, Lazy<Pair<String, String>>>
	validateJsonSchemaFrom(Path jsonFile) {
		return jsonTree -> {
			JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
			var maybeSchema = Try.of(() -> {
				var schemaString = Files.readString(jsonFile);
				return factory.getSchema(schemaString);
			});
			var maybeValidationResult = maybeSchema.flatMap(schema ->
							Try.success(schema.validate(jsonTree)));
			return Collections.firstTrueStateOf(List.of(
							Json.matchesSchema(maybeValidationResult),
							Json.invalidSchema(maybeValidationResult),
							Json.noSchemaFile(maybeValidationResult),
							Json.malformed(maybeValidationResult),
							Json.unknownProblem(maybeValidationResult)));
		};
		}
}
