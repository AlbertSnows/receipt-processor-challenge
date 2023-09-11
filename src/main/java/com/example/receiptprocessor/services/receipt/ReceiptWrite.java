package com.example.receiptprocessor.services.receipt;

import com.example.receiptprocessor.data.entities.Receipt;
import com.example.receiptprocessor.data.repositories.ReceiptRepository;
import com.example.receiptprocessor.services.points.PointRead;
import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ReceiptWrite {
		@Autowired
		private final ReceiptRepository receiptRepository;
		@Autowired
		private final PointRead pointRead;

		public ReceiptWrite(ReceiptRepository receiptRepository, PointRead pointRead) {
			this.receiptRepository = receiptRepository;
			this.pointRead = pointRead;
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
		// get points if already exists
//		var alreadyCalculated = point != null;

		// receipt.retailer.count
		// if total is round + 50
		// if total mod 0.25 + 25
		// 5 * total items / 2
		// itemdescription.trim.length mod 3 = 0, item price * 0.2 => ceiling => + result
		// if date is odd + 6
		// if time of purchase > 1400 hours and < 1600 hours

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
