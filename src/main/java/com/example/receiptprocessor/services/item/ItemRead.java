package com.example.receiptprocessor.services.item;

import com.example.receiptprocessor.data.entities.Item;
import com.example.receiptprocessor.data.entities.Receipt;
import com.example.receiptprocessor.data.entities.ReceiptItems;
import com.example.receiptprocessor.data.repositories.ItemRepository;
import com.example.receiptprocessor.data.repositories.ReceiptItemsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemRead {
	@Autowired
	private final ItemRepository itemRepository;
	@Autowired
	private final ReceiptItemsRepository receiptItemsRepository;
	public ItemRead(ItemRepository itemRepository, ReceiptItemsRepository receiptItemsRepository) {
		this.itemRepository = itemRepository;
		this.receiptItemsRepository = receiptItemsRepository;
	}

	public List<Item> findAll(Receipt receipt) {
		var receiptItems = receiptItemsRepository.findAllByReceipt(receipt);
		var items = receiptItems.stream()
						.map(ReceiptItems::getItem);
		return items.toList();
	}
}
