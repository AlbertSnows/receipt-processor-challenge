package com.example.receiptprocessor.services.receipt;

import com.example.receiptprocessor.data.entities.Receipt;
import com.example.receiptprocessor.data.repositories.ReceiptRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class ReceiptWrite {
		@Autowired
		private final ReceiptRepository receiptRepository;

		public ReceiptWrite(ReceiptRepository receiptRepository) {
			this.receiptRepository = receiptRepository;
		}

		public static com.example.receiptprocessor.data.entities.Receipt hydrate(JsonNode receipt) {
			var dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			return new com.example.receiptprocessor.data.entities.Receipt(
							receipt.get("retailer").asText(),
							LocalDate.parse(receipt.get("purchaseDate").asText(), dateFormatter),
							receipt.get("purchaseTime").asText(),
							new BigDecimal(receipt.get("total").asText()));
		}

		public Receipt recordReceipt(Receipt receiptEntity) {
			return receiptRepository.save(receiptEntity);
		}
}
