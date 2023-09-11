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
		var retailerTotal = receipt.getTotal();
		var totalIsRound = retailerTotal.scale() == 0;
		var quarterFractional = totalIsRound
						|| retailerTotal.remainder(new BigDecimal("0.25")).equals(BigDecimal.ZERO);
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

//		{
//			"retailer": "Target",
//						"purchaseDate": "2022-01-01",
//						"purchaseTime": "13:01",
//						"items": [
//			{
//				"shortDescription": "Mountain Dew 12PK",
//							"price": "6.49"
//			},{
//			"shortDescription": "Emils Cheese Pizza",
//							"price": "12.25"
//		},{
//			"shortDescription": "Knorr Creamy Chicken",
//							"price": "1.26"
//		},{
//			"shortDescription": "Doritos Nacho Cheese",
//							"price": "3.35"
//		},{
//			"shortDescription": "   Klarbrunn 12-PK 12 FL OZ  ",
//							"price": "12.00"
//		}
//  ],
//			"total": "35.35"
//		}
//		Total Points: 28
//		Breakdown:
//		6 points - retailer name has 6 characters
//		10 points - 4 items (2 pairs @ 5 points each)
//		3 Points - "Emils Cheese Pizza" is 18 characters (a multiple of 3)
//		item price of 12.25 * 0.2 = 2.45, rounded up is 3 points
//		3 Points - "Klarbrunn 12-PK 12 FL OZ" is 24 characters (a multiple of 3)
//		item price of 12.00 * 0.2 = 2.4, rounded up is 3 points
//		6 points - purchase day is odd
//						+ ---------
			//todo: ...
			return 69;
	}
}
