package com.example.receiptprocessor.services.receipt;

import com.example.receiptprocessor.data.entities.Receipt;
import com.example.receiptprocessor.data.repositories.ReceiptRepository;
import com.example.receiptprocessor.data.states.Points;
import com.example.receiptprocessor.services.item.ItemRead;
import com.example.receiptprocessor.services.points.PointWrite;
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
		private final ItemRead itemRead;
		@Autowired
		private final PointWrite pointWrite;


	public ReceiptWrite(ReceiptRepository receiptRepository,
	                    ItemRead itemRead,
	                    PointWrite pointWrite) {
		this.receiptRepository = receiptRepository;
		this.itemRead = itemRead;
		this.pointWrite = pointWrite;
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
		var receiptItems = itemRead.findAll(receipt);
		//todo: we don't currently check if total of receipt matches item
		var possiblePointStates =
						Points.possibleStatesForReceiptPoints(receipt, receiptItems);
		var relevantPointStates = possiblePointStates.stream()
						.filter(state -> state.getFirst().get());
		var totalPoints = relevantPointStates
						.map(state -> state.getSecond().get())
						.mapToInt(Integer::intValue)// returns an int
						.sum();
		pointWrite.save(pointWrite.hydrate(totalPoints, receipt));
		return totalPoints;
	}
}
