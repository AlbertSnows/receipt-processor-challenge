package com.example.receiptprocessor.services.item;

import com.example.receiptprocessor.data.entities.Item;
import com.example.receiptprocessor.data.repositories.ItemRepository;
import com.fasterxml.jackson.databind.JsonNode;
import io.vavr.Lazy;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class ItemWrite {
	@Autowired
	private final ItemRepository itemRepo;

	public ItemWrite(ItemRepository itemRepository) {
		this.itemRepo = itemRepository;
	}

	@Contract("_ -> new")
	public static @NotNull Item hydrate(@NotNull JsonNode item) {
		return new com.example.receiptprocessor.data.entities.Item(
						item.get("shortDescription").asText().trim(),
						new BigDecimal(item.get("price").asText()));
	}

	public Item save(Item item) {
		return itemRepo.save(item);
	}

	public Stream<Lazy<Item>> getItemQueries(@NotNull JsonNode items) {
		return StreamSupport.stream(items.spliterator(), true)
						.map(ItemWrite::hydrate)
						.map(itemEntity -> Lazy.of(() -> save(itemEntity)));
	}
}
