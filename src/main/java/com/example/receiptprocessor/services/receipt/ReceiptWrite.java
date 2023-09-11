package com.example.receiptprocessor.services.receipt;

import com.example.receiptprocessor.data.entities.Receipt;
import com.example.receiptprocessor.data.repositories.ReceiptRepository;
import com.example.receiptprocessor.data.states.Points;
import com.example.receiptprocessor.services.item.ItemRead;
import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReceiptWrite {
		@Autowired
		private final ReceiptRepository receiptRepository;
		@Autowired
		private final ItemRead itemRead;

		public ReceiptWrite(ReceiptRepository receiptRepository, ItemRead itemRead) {
			this.receiptRepository = receiptRepository;
			this.itemRead = itemRead;
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

	public Integer calculatePoints(Receipt receipt) {
			//todo: we don't currently check if total of receipt matches item
		var receiptItems = itemRead.findAll(receipt);
		var retailerPurchaseTotal = receipt.getTotal();
		var purchaseDateTime = receipt.getPurchaseDateTime();
		var possiblePointStates = List.of(
						Points.retailerNameCount(),
						Points.roundTotal(),
						Points.quarterFractional(),
						Points.pointsPerTwoItems(),
						Points.itemPricePointsFromItemDescription(),
						Points.oddPurchaseDate(),
						Points.timeBetweenTwoAndFour());
		var relevantPointStates = possiblePointStates.stream()
						.filter(state -> state.getFirst().get());
		return relevantPointStates
						.map(state -> state.getSecond().get()) // returns an int
						.sum();
	}
}
