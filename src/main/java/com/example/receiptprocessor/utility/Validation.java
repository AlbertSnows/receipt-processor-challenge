package com.example.receiptprocessor.utility;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import io.vavr.Function1;
import io.vavr.control.Try;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class Validation {
	private Validation() {
		throw new IllegalStateException("Utility class");
	}

	public static Function1<JsonNode, Try<Set<ValidationMessage>>>
	validateJsonSchema(Path jsonFile) {
		return jsonTree -> {
			// Read JSON from the file and map it to a Java object
			JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
			var maybeSchema = Try.of(() -> {
				var schemaString = Files.readString(jsonFile);
				return factory.getSchema(schemaString);
			});
			return maybeSchema.flatMap(schema ->
							Try.success(schema.validate(jsonTree)));
		};
		// todo: this  function always have a finite number of outcomes
		// could I just design this such that it returns a state map?
		}
}
