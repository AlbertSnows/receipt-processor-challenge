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
	private UUID id;

	@Column(name = "short_description")
	private String shortDescription;

	@Column(name = "price")
	private BigDecimal price;

	@ManyToOne
	@JoinColumn(name = "receipt_id")
	private Receipt receipt;

	public Item() {

	}

	public Item(String shortDescription, BigDecimal price) {
		this.shortDescription = shortDescription;
		this.price = price;
	}

	public UUID getId() {
		return this.id;
	}
}
