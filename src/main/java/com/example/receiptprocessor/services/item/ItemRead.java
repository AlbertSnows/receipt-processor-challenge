package com.example.receiptprocessor.services.item;

import com.example.receiptprocessor.data.repositories.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemRead {
	@Autowired
	private final ItemRepository itemRepository;

	public ItemRead(ItemRepository itemRepository) {
		this.itemRepository = itemRepository;
	}
}
