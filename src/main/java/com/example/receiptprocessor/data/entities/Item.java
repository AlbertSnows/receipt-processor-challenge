package com.example.receiptprocessor.data.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "items")
public class Item {
	@Id
	@GeneratedValue
	@Column(columnDefinition = "uuid")
	private final UUID id;

	@Column(name = "short_description")
	private final String shortDescription;

	@Column(name = "price")
	private final BigDecimal price;

	public Item(UUID id, String shortDescription, BigDecimal price) {
		this.id = id;
		this.shortDescription = shortDescription;
		this.price = price;
	}
}
