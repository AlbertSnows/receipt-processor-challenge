package com.example.receiptprocessor.services.item;

import com.example.receiptprocessor.data.entities.Item;
import com.example.receiptprocessor.data.entities.Receipt;
import com.example.receiptprocessor.data.entities.ReceiptItems;
import com.example.receiptprocessor.data.repositories.ItemRepository;
import com.example.receiptprocessor.data.repositories.ReceiptItemsRepository;
import com.example.receiptprocessor.utility.Collections;
import com.example.receiptprocessor.utility.Validation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.vavr.Function1;
import io.vavr.Lazy;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class ItemRead {
	private static final ObjectMapper objectMapper = new ObjectMapper();
	@Autowired
	private final ItemRepository itemRepository;
	@Autowired
	private final ReceiptItemsRepository receiptItemsRepository;

	public ItemRead(ItemRepository itemRepository, ReceiptItemsRepository receiptItemsRepository) {
		this.itemRepository = itemRepository;
		this.receiptItemsRepository = receiptItemsRepository;
	}

	@Contract(pure = true)
	private static @NotNull Function1<JsonNode, Try<ObjectNode>> updatePriceNodeOrFail(ObjectNode jsonObject) {
		return priceNode -> {
			var priceString = priceNode.asText();
			return Try.of(() -> new BigDecimal(priceString))
							.map(priceAsNum -> jsonObject.put("price", priceAsNum));
		};
	}

	public static Try<ObjectNode> getAndUpsertPriceOrFail(@NotNull ObjectNode jsonObject) {
		var maybePriceJson = Option.of(jsonObject.get("price"))
						.toTry(() -> new RuntimeException("No price given."));
		return maybePriceJson.flatMap(updatePriceNodeOrFail(jsonObject));
	}

	public static Try<ObjectNode> convertPriceToNumber(@NotNull JsonNode item) {
		Try<ObjectNode> readTree = Try.of(() -> objectMapper.readTree(item.traverse()));
		return readTree.flatMap(ItemRead::getAndUpsertPriceOrFail);
	}

	public @NotNull Lazy<List<Pair<String, String>>> validateItems(Path file, @NotNull JsonNode items) {
		var validateItem = Validation.validateJsonSchemaFrom(file);
		var conversionOutcomes = StreamSupport
						.stream(items.spliterator(), true)
						.map(ItemRead::convertPriceToNumber).toList();
		var castingFailures = conversionOutcomes.stream()
						.filter(Try::isFailure).map(Try::getCause).toList();
		var successCases = conversionOutcomes.stream()
						.filter(Try::isSuccess).map(Try::get);
		var validationFailures = successCases
						.map(validateItem)
						.map(Lazy::get)
						.toList();
		return Collections.firstTrueStateOf(List.of(
						com.example.receiptprocessor.data.states.Item.success(castingFailures, validationFailures),
						com.example.receiptprocessor.data.states.Item.multipleFailures(castingFailures, validationFailures),
						com.example.receiptprocessor.data.states.Item.couldNotCastPrice(castingFailures),
						com.example.receiptprocessor.data.states.Item.didNotMatchSchema(validationFailures),
						com.example.receiptprocessor.data.states.Item.unhandledProblem(items)));
	}

	public List<Pair<String, String>> tryValidatingItems(@RequestBody @NotNull JsonNode receipt) {
		var jsonFile = Paths.get("src/main/java/com/example/receiptprocessor/data/schemas/item.json");
		var items = receipt.get("items");
		return Collections.firstTrueEagerStateOf(List.of(
						Pair.of(items != null, validateItems(jsonFile, (items != null) ? items : objectMapper.createObjectNode())),
						Pair.of(true, Lazy.of(() -> List.of(Pair.of("No items", "No items provided")))))).get();
	}

	public List<Item> findAllForReceipt(Receipt receipt) {
		var receiptItems = receiptItemsRepository.findAllByReceipt(receipt);
		var items = receiptItems.stream()
						.map(ReceiptItems::getItem);
		return items.toList();
	}

	public List<Item> findAll() {
		return itemRepository.findAll();
	}
}
