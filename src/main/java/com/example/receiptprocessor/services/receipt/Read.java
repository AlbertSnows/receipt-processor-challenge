package com.example.receiptprocessor.services.receipt;

import com.example.receiptprocessor.data.repositories.ReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Read {

	@Autowired
	private final ReceiptRepository receiptRepository;

	public Read(ReceiptRepository receiptRepository) {
		this.receiptRepository = receiptRepository;
	}

}
