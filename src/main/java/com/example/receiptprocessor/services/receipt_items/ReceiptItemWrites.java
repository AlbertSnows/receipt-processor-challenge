package com.example.receiptprocessor.services.receipt_items;

import com.example.receiptprocessor.data.entities.Item;
import com.example.receiptprocessor.data.entities.Receipt;
import com.example.receiptprocessor.data.entities.ReceiptItems;
import com.example.receiptprocessor.data.repositories.ReceiptItemsRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class ReceiptItemWrites {
	@Autowired
	private final ReceiptItemsRepository receiptItemsRepo;
	private ReceiptItemWrites(ReceiptItemsRepository receiptItemsRepo) {
		this.receiptItemsRepo = receiptItemsRepo;
	}
	public Stream<ReceiptItems> saveReceiptItemConnections(@NotNull List<Item> items, Receipt receipt) {
		return items.stream()
						.map(item ->  new ReceiptItems(item, receipt))
						.map(receiptItemsRepo::save);
	}
	public ReceiptItems save(ReceiptItems receiptItems) {
		return receiptItemsRepo.save(receiptItems);
	}
}
