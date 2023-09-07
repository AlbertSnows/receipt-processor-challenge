package com.example.receiptprocessor.services.receipt;

import com.example.receiptprocessor.data.repositories.ReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Write {
		@Autowired
		private final ReceiptRepository receiptRepository;

		public Write(ReceiptRepository receiptRepository) {
			this.receiptRepository = receiptRepository;
		}

		public void saveReceipt(com.example.receiptprocessor.data.entities.Receipt receipt) {
			// Implement logic to save the receipt data in the database
			receiptRepository.save(receipt);
		}
}
