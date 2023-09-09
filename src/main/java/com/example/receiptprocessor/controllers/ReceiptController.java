package com.example.receiptprocessor.controllers;

import com.example.receiptprocessor.data.records.SimpleHTTPResponse;
import com.example.receiptprocessor.data.states.Database;
import com.example.receiptprocessor.data.states.Json;
import com.example.receiptprocessor.data.states.Receipt;
import com.example.receiptprocessor.services.receipt.ReceiptWrite;
import com.example.receiptprocessor.utility.Collections;
import com.example.receiptprocessor.utility.Validation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.ValidationMessage;
import io.vavr.Function0;
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

@RestController
@RequestMapping("/receipts")
public class ReceiptController {
	private static final Logger logger = LoggerFactory.getLogger(ReceiptController.class);
	@Autowired
	private final ObjectMapper objectMapper;
	@Autowired
	private final ReceiptWrite receiptWrite;
	public ReceiptController(ObjectMapper objectMapper, ReceiptWrite receiptService) {
		this.objectMapper = objectMapper;
		this.receiptWrite = receiptService;
	}

	@PostMapping("/process")
	public ResponseEntity<Map<String, String>> recordReceipt(@RequestBody JsonNode receipt) {
		var jsonFile = Paths.get("src/main/java/com/example/receiptprocessor/data/schemas/receipt.json");
		var validationResultOrFailure = Validation.validateJsonSchema(jsonFile, receipt);
		var validationOptions = processReceiptStatesOn(validationResultOrFailure);
		var validationResult = Collections.firstTrueStateOf(validationOptions).get();
		var validJson = validationResult.statusCode() == HttpStatus.CREATED;
		var receiptEntity = validJson? ReceiptWrite.hydrate(receipt) : null;
		var recordReceipt = Function0.of(() -> receiptWrite.recordReceipt(receiptEntity));
		var success = validJson && recordReceipt.get() != null;
		var responseResult = Collections.firstTrueEagerStateOf(List.of(
						Pair.of(success || !validJson, validationResult),
						Pair.of(true, Database.couldNotWrite())));
		return ResponseEntity.status(responseResult.statusCode()).body(responseResult.body());
	}

	/**
	 * A list of all recognized states that could happen when trying
	 * to process a receipt
	 * @param validationResultOrFailure A Try (i.e. Option) type that represents either
	 *                                  A) the result of trying to validate the incoming json
	 *                                  B) one of the many possible failure cases that could happen
	 *                                     when trying to parse a json request
	 * @return A list of endpoint states that is meant to encompass all expected states of
	 * in this case, the processReceipt request
	 */
	@org.jetbrains.annotations.Unmodifiable
	@org.jetbrains.annotations.Contract("_ -> new")
	private List<Pair<Lazy<Boolean>, Lazy<SimpleHTTPResponse>>>
	processReceiptStatesOn(Try<Set<ValidationMessage>> validationResultOrFailure) {
		return List.of(
						Receipt.created(validationResultOrFailure),
						Json.invalid(validationResultOrFailure),
						Json.noSchemaFile(validationResultOrFailure),
						Json.malformed(validationResultOrFailure),
						Json.unknownProblem(validationResultOrFailure));
	}

	@GetMapping("/{id}/points")
	public ResponseEntity<Integer> getPoints(@PathVariable Long id) {
		// Implement retrieval logic here, e.g., fetch points for a receipt by ID
		// Replace 'Integer' with the actual type of the data you're returning
		Integer points = 100;
		return ResponseEntity.ok(points);
	}
}
