package com.example.receiptprocessor.controllers;

import com.example.receiptprocessor.data.records.SimpleHTTPResponse;
import com.example.receiptprocessor.data.states.Json;
import com.example.receiptprocessor.services.item.ItemWrite;
import com.example.receiptprocessor.services.receipt.ReceiptWrite;
import com.example.receiptprocessor.utility.Collections;
import com.example.receiptprocessor.utility.Validation;
import com.fasterxml.jackson.databind.JsonNode;
import io.vavr.Lazy;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/receipts")
public class ReceiptController {
	@Autowired
	private final ReceiptWrite receiptWrite;
	@Autowired
	private final ItemWrite itemWrite;
	public ReceiptController(ReceiptWrite receiptService, ItemWrite itemWrite) {
		this.receiptWrite = receiptService;
		this.itemWrite = itemWrite;
	}

	Lazy<Object> getReceiptQuery(JsonNode receipt) {
		var receiptEntity = ReceiptWrite.hydrate(receipt);
		return Lazy.of(() -> { receiptWrite.recordReceipt(receiptEntity); return null; });
	}

	  List<Lazy<Object>> getItemQueries(@NotNull JsonNode items) {
		return StreamSupport.stream(items.spliterator(), true)
						.map(ItemWrite::hydrate)
						.map(item -> Lazy.of(() -> { itemWrite.save(item); return null; }))
						.toList();
	}

	public Pair<String, String> validateReceipt(@RequestBody JsonNode receipt) {
		var jsonFile = Paths.get("src/main/java/com/example/receiptprocessor/data/schemas/receipt.json");
		return Validation.validateJsonSchemaFrom(jsonFile).apply(receipt).get();
	}

	public List<Pair<String, String>> validateItems(Path file, @NotNull JsonNode items) {
		var validateItem = Validation.validateJsonSchemaFrom(file);
		return StreamSupport
						.stream(items.spliterator(), true)
						.map(validateItem)
						.map(Lazy::get)
						.toList();
	}

	public List<Pair<String, String>> validateItems(@RequestBody @NotNull JsonNode receipt) {
		var jsonFile = Paths.get("src/main/java/com/example/receiptprocessor/data/schemas/item.json");
		var items = receipt.get("items");
		var hasItems = items != null;
		var validationOutcomes = hasItems? validateItems(jsonFile, items) : null;
		assert validationOutcomes != null;
		return validationOutcomes.stream()
						.filter(validationResult -> !validationResult.getFirst().equals(Json.MATCHED_SCHEMA))
						.toList();

	}

	@PostMapping("/process")
	public ResponseEntity<Map<String, String>> recordReceipt(@RequestBody JsonNode receipt) {
		Pair<String, String> receiptOutcomes = validateReceipt(receipt);
		var validReceipt = receiptOutcomes.getFirst().equals(Json.MATCHED_SCHEMA);
		var invalidItemOutcomes = validateItems(receipt);
		List<Lazy<Object>> itemQueries = invalidItemOutcomes.isEmpty()?
						getItemQueries(receipt.get("items")) :
						List.of();
		var invalidData = validReceipt?
						invalidItemOutcomes :
						Collections.add(invalidItemOutcomes).apply(receiptOutcomes);
		var queriesToRun = (validReceipt && invalidItemOutcomes.isEmpty()) ?
						Collections.add(itemQueries).apply(getReceiptQuery(receipt)) :
						itemQueries;
		if(!queriesToRun.isEmpty()) {
			//noinspection ResultOfMethodCallIgnored
			queriesToRun.stream().map(Lazy::get);
		}
		var responseInfo = invalidData.isEmpty()?
						new SimpleHTTPResponse(HttpStatus.CREATED, Map.of("message", "receipt stored!")) :
						errorState(invalidData);

		return ResponseEntity.status(responseInfo.statusCode()).body(responseInfo.body());
	}
	SimpleHTTPResponse errorState(@NotNull List<Pair<String, String>> invalidData) {
		var errors = Map.of("errors", invalidData.toString());
		return new SimpleHTTPResponse(HttpStatus.BAD_REQUEST, errors);
	}

	@GetMapping("/{id}/points")
	public ResponseEntity<Integer> getPoints(@PathVariable Long id) {
		// Implement retrieval logic here, e.g., fetch points for a receipt by ID
		// Replace 'Integer' with the actual type of the data you're returning
		Integer points = 100;
		return ResponseEntity.ok(points);
	}
}
