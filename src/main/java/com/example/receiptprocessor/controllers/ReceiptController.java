package com.example.receiptprocessor.controllers;

import com.example.receiptprocessor.data.entities.Points;
import com.example.receiptprocessor.data.entities.Receipt;
import com.example.receiptprocessor.data.entities.ReceiptItems;
import com.example.receiptprocessor.data.records.SimpleHTTPResponse;
import com.example.receiptprocessor.data.repositories.PointsRepository;
import com.example.receiptprocessor.data.repositories.ReceiptItemsRepository;
import com.example.receiptprocessor.services.item.ItemRead;
import com.example.receiptprocessor.services.points.PointWrite;
import com.example.receiptprocessor.services.receipt.ReceiptRead;
import com.example.receiptprocessor.services.receipt.ReceiptWrite;
import com.example.receiptprocessor.utility.Collections;
import com.fasterxml.jackson.databind.JsonNode;
import io.vavr.control.Try;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static com.example.receiptprocessor.data.Constants.MATCHED_SCHEMA;

@RestController
@RequestMapping("/receipts")
public class ReceiptController {
	@Autowired
	private final ReceiptWrite receiptWrite;
	@Autowired
	private final ItemRead itemRead;
	@Autowired
	private final ReceiptRead receiptRead;
	@Autowired
	private final ReceiptItemsRepository receiptItemRepo;
	@Autowired
	private final PointsRepository pointRepo;
	@Autowired
	private final PointWrite pointWrite;

	public ReceiptController(ReceiptWrite receiptWrite,
	                         ItemRead itemRead,
	                         ReceiptRead receiptRead,
	                         ReceiptItemsRepository receiptItemRepo,
	                         PointsRepository pointRepo,
	                         PointWrite pointWrite) {

		this.receiptWrite = receiptWrite;
		this.itemRead = itemRead;
		this.receiptRead = receiptRead;
		this.receiptItemRepo = receiptItemRepo;
		this.pointRepo = pointRepo;
		this.pointWrite = pointWrite;
	}

	public static @NotNull Boolean matchedSchema(@NotNull Pair<String, String> outcome) {
		return outcome.getFirst().equals(MATCHED_SCHEMA);
	}

	@PostMapping("/process")
	public ResponseEntity<Map<String, String>> recordReceipt(@RequestBody JsonNode receipt) {
		var receiptOutcomes = receiptRead.validateReceipt(receipt);
		var itemOutcomes = itemRead.tryValidatingItems(receipt);
		var validReceipt = Boolean.TRUE.equals(matchedSchema(receiptOutcomes));
		var validItems = itemOutcomes.stream()
						.noneMatch(itemOutcome -> Boolean.FALSE.equals(matchedSchema(itemOutcome)));
		Stream<Pair<String, String>> allOutcomes = Stream.concat(
						Stream.of(receiptOutcomes),
						itemOutcomes.stream());
		List<Pair<String, String>> invalidOutcomes = Collections.firstTrueEagerStateOf(List.of(
						Pair.of(validReceipt && validItems, List.of()),
						Pair.of(validReceipt, itemOutcomes),
						Pair.of(validItems, List.of(receiptOutcomes)),
						Pair.of(true, allOutcomes.toList())));
		var canSave = invalidOutcomes.isEmpty();
		var receiptId = canSave ? receiptWrite.runProcessQueries(receipt) : null;
		var responseInfo = receiptId != null ?
						new SimpleHTTPResponse(HttpStatus.CREATED, Map.of("id", receiptId)) :
						com.example.receiptprocessor.data.states.Receipt.errorState(invalidOutcomes);
		return ResponseEntity.status(responseInfo.statusCode()).body(responseInfo.body());
	}


	@GetMapping("/all")
	public ResponseEntity<List<String>> listAllData() {
		var allReceipts = receiptRead.findAll().stream()
						.map(Receipt::getId).toList().toString();
		var allItems = itemRead.findAll().stream()
						.map(com.example.receiptprocessor.data.entities.Item::getId).toList().toString();
		var allReadItems = receiptItemRepo.findAll().stream()
						.map(ReceiptItems::getId).toList().toString();
		var allPoints = pointRepo.findAll().stream()
						.map(Points::getId).toList().toString();
		return ResponseEntity.ok(List.of(
						"Receipts: ", allReceipts, " | ",
						"Items: ", allItems, " | ",
						"Receipt Items:", allReadItems, " | ",
						"Points: ", allPoints));
	}

	@GetMapping("/{providedReceiptID}/points")
	public ResponseEntity<Map<String, String>> getPoints(@PathVariable String providedReceiptID) {
		var maybeUUID = Try.of(() -> UUID.fromString(providedReceiptID));
		var validUUID = maybeUUID.isSuccess();
		var receiptID = validUUID ? maybeUUID.get() : null;
		var maybeReceipt = validUUID ?
						receiptRead.findById(receiptID) :
						java.util.Optional.<Receipt>empty();
		var receipt = maybeReceipt.orElse(null);
		var pointOutcome = pointWrite.getsertPointOutcome(receipt, validUUID);
		return ResponseEntity.status(pointOutcome.statusCode()).body(pointOutcome.body());
	}
}
