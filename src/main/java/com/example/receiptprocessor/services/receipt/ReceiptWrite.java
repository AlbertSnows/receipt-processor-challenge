package com.example.receiptprocessor.services.receipt;

import com.example.receiptprocessor.data.entities.Receipt;
import com.example.receiptprocessor.data.repositories.ReceiptRepository;
import com.example.receiptprocessor.services.item.ItemWrite;
import com.example.receiptprocessor.services.receipt_items.ReceiptItemWrites;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.Function0;
import io.vavr.Lazy;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class ReceiptWrite {
	private static final ObjectMapper objectMapper = new ObjectMapper();
	@Autowired
	private final ReceiptRepository receiptRepository;
	@Autowired
	private final ItemWrite itemWrite;
	@Autowired
	private final ReceiptItemWrites receiptItemWrites;

	public ReceiptWrite(ReceiptRepository receiptRepository,
	                    ItemWrite itemWrite,
	                    ReceiptItemWrites receiptItemWrite) {
		this.receiptRepository = receiptRepository;
		this.itemWrite = itemWrite;
		this.receiptItemWrites = receiptItemWrite;
	}

	public static com.example.receiptprocessor.data.entities.@NotNull Receipt hydrate(@NotNull JsonNode receipt) {
		var dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		var date = receipt.get("purchaseDate").asText();
		var time = receipt.get("purchaseTime").asText();
		return new com.example.receiptprocessor.data.entities.Receipt(
						receipt.get("retailer").asText(),
						LocalDateTime.parse(date + " " + time, dateFormatter),
						new BigDecimal(receipt.get("total").asText()));
	}

	public Receipt recordReceipt(Receipt receiptEntity) {
		return receiptRepository.save(receiptEntity);
	}

	public String runProcessQueries(@NotNull JsonNode receipt) {
		JsonNode receiptItemsJson = Optional.ofNullable(receipt.get("items"))
						.orElseGet(objectMapper::nullNode);
		var itemQueries = itemWrite.getItemQueries(receiptItemsJson);
		var receiptEntity = getReceiptQuery(receipt).get();
		var receiptItems = receiptItemWrites
						.saveReceiptItemConnections(itemQueries.map(Lazy::get).toList(), receiptEntity).toList();
		return receiptEntity.getId().toString();
	}

	Function0<Receipt> getReceiptQuery(JsonNode receipt) {
		var receiptEntity = ReceiptWrite.hydrate(receipt);
		return Function0.of(() -> recordReceipt(receiptEntity));
	}
}
