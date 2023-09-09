package com.example.receiptprocessor.controllers;

import com.example.receiptprocessor.data.records.SimpleHTTPResponse;
import com.example.receiptprocessor.data.states.Json;
import com.example.receiptprocessor.data.states.Receipt;
import com.example.receiptprocessor.utility.Collections;
import com.example.receiptprocessor.utility.Validation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.ValidationMessage;
import io.vavr.Lazy;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hibernate.validator.internal.metadata.core.ConstraintHelper.MESSAGE;

@RestController
@RequestMapping("/receipts")
public class ReceiptController {
	private static final Logger logger = LoggerFactory.getLogger(ReceiptController.class);

	@Autowired
	private final Validation validation;
	@Autowired
	private final ObjectMapper objectMapper;

	public ReceiptController(Validation validation, ObjectMapper objectMapper) {
		this.validation = validation;
		this.objectMapper = objectMapper;
	}

	@PostMapping("/process")
	public ResponseEntity<Map<String, String>> recordReceipt(@RequestBody JsonNode receipt) {

		var json = "{" +
						"\"retailer\": \"Target\"," +
						"\"purchaseDate\": \"2022-01-02\"," +
						"\"purchaseTime\": \"13:13\"," +
						"\"total\": \"1.25\"," +
						"\"items\": [" +
						"{\"shortDescription\": \"Pepsi - 12-oz\", \"price\": \"1.25\"}" +
						"]" + "}";

		// Specify the path to your JSON file
		var jsonFile = Paths.get("src/main/java/com/example/receiptprocessor/data/schemas/receipt.json");
		var maybeJsonTree = Try.of(() -> objectMapper.readTree(json));
		var validationResultOrFailure = validation.validateJsonSchema(jsonFile, maybeJsonTree);
		var validationStateMap = processReceiptStates(validationResultOrFailure);
		var exceptionType = validationResultOrFailure.getCause().getClass();
		var failCase = Lazy.of(() -> new SimpleHTTPResponse(
						HttpStatus.INTERNAL_SERVER_ERROR,
						Map.of(MESSAGE, "Unrecognized problem trying to validate json.",
										"error", exceptionType.descriptorString())));
		SimpleHTTPResponse result = Collections.getFirstTrue(validationStateMap, failCase);
		if(result.statusCode() == HttpStatus.CREATED) {
			var x = "foo";
//			receiptService.recordReceipt();
		}
		return ResponseEntity.status(result.statusCode()).body(result.body());
	}

	private List<Pair<Lazy<Boolean>, Lazy<SimpleHTTPResponse>>> processReceiptStates(Try<Set<ValidationMessage>> validationResultOrFailure) {
		return List.of(
						Receipt.created(validationResultOrFailure),
						Json.invalid(validationResultOrFailure),
						Json.noSchemaFile(validationResultOrFailure),
						Json.malformed(validationResultOrFailure));
	}

	@GetMapping("/{id}/points")
	public ResponseEntity<Integer> getPoints(@PathVariable Long id) {
		// Implement retrieval logic here, e.g., fetch points for a receipt by ID
		// Replace 'Integer' with the actual type of the data you're returning
		Integer points = 100;
		return ResponseEntity.ok(points);
	}
}
