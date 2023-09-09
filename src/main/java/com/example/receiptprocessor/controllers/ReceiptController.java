package com.example.receiptprocessor.controllers;

import com.example.receiptprocessor.data.states.Database;
import com.example.receiptprocessor.data.states.Receipt;
import com.example.receiptprocessor.services.receipt.ReceiptWrite;
import com.example.receiptprocessor.utility.Collections;
import com.example.receiptprocessor.utility.Validation;
import com.fasterxml.jackson.databind.JsonNode;
import io.vavr.Function0;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/receipts")
public class ReceiptController {
	@Autowired
	private final ReceiptWrite receiptWrite;
	public ReceiptController(ReceiptWrite receiptService) {
		this.receiptWrite = receiptService;
	}

	@PostMapping("/process")
	public ResponseEntity<Map<String, String>> recordReceipt(@RequestBody JsonNode receipt) {
		var jsonFile = Paths.get("src/main/java/com/example/receiptprocessor/data/schemas/receipt.json");
		var validationResultOrFailure = Validation.validateJsonSchema(jsonFile, receipt);
		var validationOptions = Receipt.processReceiptStatesOn(validationResultOrFailure);
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

	@GetMapping("/{id}/points")
	public ResponseEntity<Integer> getPoints(@PathVariable Long id) {
		// Implement retrieval logic here, e.g., fetch points for a receipt by ID
		// Replace 'Integer' with the actual type of the data you're returning
		Integer points = 100;
		return ResponseEntity.ok(points);
	}
}
