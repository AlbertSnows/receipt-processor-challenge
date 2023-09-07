package com.example.receiptprocessor.services.receipt;

public class Write {
	@Service
	public class ReceiptService {
		@Autowired
		private ReceiptRepository receiptRepository;

		public void saveReceipt(Receipt receipt) {
			// Implement logic to save the receipt data in the database
			receiptRepository.save(receipt);
		}
	}

}
