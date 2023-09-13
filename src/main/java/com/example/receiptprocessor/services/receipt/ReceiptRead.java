package com.example.receiptprocessor.services.receipt;

import com.example.receiptprocessor.data.entities.Receipt;
import com.example.receiptprocessor.data.repositories.ReceiptRepository;
import com.example.receiptprocessor.utility.Validation;
import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReceiptRead {

	@Autowired
	private final ReceiptRepository receiptRepository;

	public ReceiptRead(ReceiptRepository receiptRepository) {
		this.receiptRepository = receiptRepository;
	}


	/**
	 * @param receipt NOTE: assumes you've already validated the json node
	 *                If you haven't, this will likely throw an exception
	 */
	public static com.example.receiptprocessor.data.entities.@NotNull Receipt hydrateJson(@NotNull JsonNode receipt) {
		var dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		var date = receipt.get("purchaseDate").asText();
		var time = receipt.get("purchaseTime").asText();
		return new com.example.receiptprocessor.data.entities.Receipt(
						receipt.get("retailer").asText(),
						LocalDateTime.parse(date + " " + time, dateFormatter),
						new BigDecimal(receipt.get("total").asText()));
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

	public Pair<String, String> validateReceipt(@RequestBody JsonNode receipt) {
		var jsonFile = Paths.get("src/main/resources/schemas/receipt.json");
		return Validation.validateJsonSchemaFrom(jsonFile).apply(receipt).get();
	}

	public Optional<Receipt> findById(UUID receiptID) {
		return receiptRepository.findById(receiptID);
	}

	public List<Receipt> findAll() {
		return receiptRepository.findAll();
	}
}
