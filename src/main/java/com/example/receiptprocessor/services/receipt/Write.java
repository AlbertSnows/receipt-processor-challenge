package com.example.receiptprocessor.services.receipt;

import com.example.receiptprocessor.data.repositories.Receipt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

public class Write {
	@Service
	public class ReceiptService {
		@Autowired
		private Receipt receiptRepository;

		public void saveReceipt(com.example.receiptprocessor.data.entities.Receipt receipt) {
			// Implement logic to save the receipt data in the database
			receiptRepository.save(receipt);
		}
	}

}
