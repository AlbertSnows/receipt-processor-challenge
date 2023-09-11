package com.example.receiptprocessor.services.receipt;

import com.example.receiptprocessor.data.entities.Receipt;
import com.example.receiptprocessor.data.records.ItemDescLengthAndPrice;
import com.example.receiptprocessor.data.repositories.ReceiptRepository;
import com.example.receiptprocessor.services.item.ItemRead;
import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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
		var retailerPointNames = receipt.getRetailer().length();
		var retailerNameCountPointTotal = receipt.getTotal();
		var totalIsRound = retailerNameCountPointTotal.scale() == 0;
		var quarterFractional = totalIsRound
						|| retailerNameCountPointTotal.remainder(new BigDecimal("0.25")).equals(BigDecimal.ZERO);
		var haveRelevantItems = receiptItems.size() >= 2;
		//todo: pretrim descriptions
		var relevantItemPrices = receiptItems.stream()
						.map(item -> new ItemDescLengthAndPrice(item.getShortDescription().length(), item.getPrice()))
						.filter(itemRecord -> itemRecord.length() % 3 == 0);
		//todo: pass stream to success state (if not relevant)
//						.map(itemRecord -> itemRecord.price().multiply(BigDecimal.valueOf(0.2)))
//						.map(BigDecimal::scale)
//						.mapToInt(Integer::intValue)
//						.sum();
		var purchaseDateTime = receipt.getPurchaseDateTime();
		var purchaseMonthDay = purchaseDateTime.getDayOfMonth();
		var oddDay = purchaseMonthDay % 2 == 1;
		var purchaseTime = purchaseDateTime.toLocalTime();
		var isBetweenTwoAndFour = purchaseTime.isAfter(LocalTime.of(14, 0))
										&& purchaseTime.isBefore(LocalTime.of(16, 0));




			//todo: ...
			return 69;
	}
}
