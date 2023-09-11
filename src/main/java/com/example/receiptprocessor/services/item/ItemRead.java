package com.example.receiptprocessor.services.item;

import com.example.receiptprocessor.data.entities.Item;
import com.example.receiptprocessor.data.entities.Receipt;
import com.example.receiptprocessor.data.repositories.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemRead {
	@Autowired
	private final ItemRepository itemRepository;

	public ItemRead(ItemRepository itemRepository) {
		this.itemRepository = itemRepository;
	}

	public List<Item> findAll(Receipt receipt) {
		return itemRepository.findAllByReceipt(receipt);
	}
}
