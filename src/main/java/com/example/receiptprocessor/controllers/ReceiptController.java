package com.example.receiptprocessor.controllers;

import com.example.receiptprocessor.data.entities.Receipt;
import com.example.receiptprocessor.data.entities.ReceiptItems;
import com.example.receiptprocessor.data.records.SimpleHTTPResponse;
import com.example.receiptprocessor.data.repositories.ItemRepository;
import com.example.receiptprocessor.data.repositories.ReceiptItemsRepository;
import com.example.receiptprocessor.data.repositories.ReceiptRepository;
import com.example.receiptprocessor.data.states.Item;
import com.example.receiptprocessor.data.states.Json;
import com.example.receiptprocessor.services.item.ItemWrite;
import com.example.receiptprocessor.services.points.PointRead;
import com.example.receiptprocessor.services.receipt.ReceiptWrite;
import com.example.receiptprocessor.services.receipt_items.ReceiptItemWrites;
import com.example.receiptprocessor.utility.Collections;
import com.example.receiptprocessor.utility.Objects;
import com.example.receiptprocessor.utility.Validation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.vavr.Function0;
import io.vavr.Function1;
import io.vavr.Lazy;
import io.vavr.control.Try;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.StreamSupport;

import static com.example.receiptprocessor.data.Constants.MATCHED_SCHEMA;

@RestController
@RequestMapping("/receipts")
public class ReceiptController {
	public static final String BOTH_VALID = "both_valid";
	public static final String RECEIPT_VALID = "receipt_valid";
	public static final String ITEM_VALID = "item_valid";
	public static final String NEITHER_VALID = "neither_valid";
	@Autowired
	private final ReceiptWrite receiptWrite;
	@Autowired
	private final ItemWrite itemWrite;
	@Autowired
	private final ItemRepository itemRead;
	@Autowired
	private final ReceiptRepository receiptRead;
	@Autowired
	private final ReceiptItemWrites receiptItemWrites;
	@Autowired
	private final ReceiptItemsRepository receiptItemRepo;
	@Autowired
	private final PointRead pointRead;
	private static final ObjectMapper objectMapper = new ObjectMapper();
	public ReceiptController(ReceiptWrite receiptService,
	                         ItemWrite itemWrite,
	                         ItemRepository itemRead,
	                         ReceiptRepository receiptRead,
	                         ReceiptItemWrites receiptItemWrites,
	                         ReceiptItemsRepository receiptItemRepo,
	                         PointRead pointRead) {
		this.receiptWrite = receiptService;
		this.itemWrite = itemWrite;
		this.itemRead = itemRead;
		this.receiptRead = receiptRead;
		this.receiptItemWrites = receiptItemWrites;
		this.receiptItemRepo = receiptItemRepo;
		this.pointRead = pointRead;
	}

	Lazy<Receipt> getReceiptQuery(JsonNode receipt) {
		var receiptEntity = ReceiptWrite.hydrate(receipt);
		return Lazy.of(() -> receiptWrite.recordReceipt(receiptEntity));
	}

	  List<com.example.receiptprocessor.data.entities.Item> getItemQueries(@NotNull JsonNode items) {
		 return StreamSupport.stream(items.spliterator(), true)
						.map(ItemWrite::hydrate)
						.map(itemWrite::save)
						 .toList();
	}

	public Pair<String, String> validateReceipt(@RequestBody JsonNode receipt) {
		var jsonFile = Paths.get("src/main/java/com/example/receiptprocessor/data/schemas/receipt.json");
		return Validation.validateJsonSchemaFrom(jsonFile).apply(receipt).get();
	}

	public static Try<JsonNode> convertPriceToNumber(@NotNull JsonNode item) {
		Try<ObjectNode> readTree = Try.of(() -> objectMapper.readTree(item.traverse()));
		return readTree.flatMap(validItem -> {
			var priceString = validItem.get("price").asText();
			var dubiousPrice = Try.of(() -> new BigDecimal(priceString));
			return dubiousPrice.map(priceAsNum -> validItem.put("price", priceAsNum));
		});

	}

	public @NotNull Lazy<List<Pair<String, String>>> validateItems(Path file, @NotNull JsonNode items) {
		var validateItem = Validation.validateJsonSchemaFrom(file);
		var conversionOutcomes = StreamSupport
						.stream(items.spliterator(), true)
						.map(ReceiptController::convertPriceToNumber).toList();
		var castingFailures = conversionOutcomes.stream()
						.filter(Try::isFailure).map(Try::getCause).toList();
		var successCases = conversionOutcomes.stream()
						.filter(Try::isSuccess).map(Try::get);
		var validationFailures = successCases
						.map(validateItem)
						.map(Lazy::get)
						.toList();
		return Collections.firstTrueStateOf(List.of(
						Item.success(castingFailures, validationFailures),
						Item.multipleFailures(castingFailures, validationFailures),
						Item.couldNotCastPrice(castingFailures),
						Item.didNotMatchSchema(validationFailures),
						Item.unhandledProblem(items)));
	}

	public List<Pair<String, String>> validateItems(@RequestBody @NotNull JsonNode receipt) {
		var jsonFile = Paths.get("src/main/java/com/example/receiptprocessor/data/schemas/item.json");
		var items = receipt.get("items");
		return Collections.firstTrueEagerStateOf(List.of(
						Pair.of(items != null, validateItems(jsonFile, (items != null)? items : objectMapper.createObjectNode())),
						Pair.of(true, Lazy.of(() -> List.of(Pair.of("No items", "No items provided")))))).get();
	}

	public static @NotNull Boolean isValid(@NotNull Pair<String, String> outcome) {
		return outcome.getFirst().equals(MATCHED_SCHEMA);
	}

	@Contract(pure = true)
	public static <V> @NotNull Function1<Map<String, V>, V>
	actOnProcessReceiptValidationOutcomes(boolean validReceipt, boolean validItems) {
		return actions -> Collections.firstTrueEagerStateOf(List.of(
						Pair.of(validReceipt && validItems, actions.get(BOTH_VALID)),
						Pair.of(validReceipt, actions.get(RECEIPT_VALID)),
						Pair.of(validItems, actions.get(ITEM_VALID)),
						Pair.of(true, actions.get(NEITHER_VALID))));
	}
	@PostMapping("/process")
	public ResponseEntity<Map<String, String>> recordReceipt(@RequestBody JsonNode receipt) {
		Pair<String, String> receiptOutcomes = validateReceipt(receipt);
		var itemOutcomes = validateItems(receipt);
		var validReceipt = Boolean.TRUE.equals(isValid(receiptOutcomes));
		var validItems = itemOutcomes.stream().noneMatch(itemOutcome -> Boolean.FALSE.equals(isValid(itemOutcome)));
		var basedOnValidationOutcomes =
						ReceiptController.actOnProcessReceiptValidationOutcomes(validReceipt, validItems);
		var invalidDataOutcome = basedOnValidationOutcomes.apply(Map.of(
						BOTH_VALID, List.of(),
						RECEIPT_VALID, List.of(itemOutcomes),
						ITEM_VALID, List.of(receiptOutcomes),
						NEITHER_VALID, List.of(receiptOutcomes, itemOutcomes)));
		List<Pair<String, String>> invalidData = Objects.uncheckedCast(invalidDataOutcome);
		List<com.example.receiptprocessor.data.entities.Item> itemQueries = getItemQueries(receipt.get("items"));
		Receipt receiptEntity = validReceipt? getReceiptQuery(receipt).get() : null;
		var receiptItems =
						receiptItemWrites.saveReceiptItemConnections(itemQueries, receiptEntity).toList();
		var responseInfo = receiptEntity != null?
						new SimpleHTTPResponse(HttpStatus.CREATED, Map.of("id", receiptEntity.getId().toString())) :
						errorState(invalidData);
		return ResponseEntity.status(responseInfo.statusCode()).body(responseInfo.body());
	}
	SimpleHTTPResponse errorState(@NotNull List<Pair<String, String>> invalidData) {
		var errors = Map.of("errors", invalidData.toString());
		return new SimpleHTTPResponse(HttpStatus.BAD_REQUEST, errors);
	}

	@GetMapping("/all")
	public ResponseEntity<List<String>> listAllData() {
		var allReceipts = receiptRead.findAll().stream()
						.map(Receipt::getId).toList().toString();
		var allItems = itemRead.findAll().stream()
						.map(com.example.receiptprocessor.data.entities.Item::getId).toList().toString();
		var allReadItems = receiptItemRepo.findAll().stream()
						.map(ReceiptItems::getId).toList().toString();
		return ResponseEntity.ok(List.of(
						"Receipts: ", allReceipts, " | ",
						"Items: ", allItems, " | ",
						"Receipt Items:", allReadItems));
	}

	@GetMapping("/{providedReceiptID}/points")
	public ResponseEntity<Map<String, String>> getPoints(@PathVariable String providedReceiptID) {
		var maybeUUID = Try.of(() -> UUID.fromString(providedReceiptID));
		var validUUID = maybeUUID.isSuccess();
		var receiptID = validUUID? maybeUUID.get() : null;
		var maybeReceipt = validUUID? receiptRead.findById(receiptID) : java.util.Optional.<Receipt>empty();
		var receipt = maybeReceipt.orElse(null);
		var getPoints = Function0.of(() -> pointRead.findByReceipt(receipt)).memoized();
		var outcome = Collections.firstTrueStateOf(List.of(
						Json.invalidID(validUUID),
						com.example.receiptprocessor.data.states.Receipt.idNotFound(receipt),
						com.example.receiptprocessor.data.states.Receipt.getPoints(getPoints),
						com.example.receiptprocessor.data.states.Receipt.calculatePoints(getPoints, receipt)))
						.get();
		return ResponseEntity.status(outcome.statusCode()).body(outcome.body());
	}
}
