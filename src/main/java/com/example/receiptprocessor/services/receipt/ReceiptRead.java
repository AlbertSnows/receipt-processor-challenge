package com.example.receiptprocessor.services.receipt;

import com.example.receiptprocessor.data.repositories.ReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReceiptRead {

	@Autowired
	private final ReceiptRepository receiptRepository;

	public ReceiptRead(ReceiptRepository receiptRepository) {
		this.receiptRepository = receiptRepository;
	}

}
