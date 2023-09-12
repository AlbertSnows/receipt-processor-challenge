package com.example.receiptprocessor.data.entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "receipt_items")
public class ReceiptItems {
	@Id
	@GeneratedValue
	private UUID id;
	@ManyToOne
	@JoinColumn(name = "item_id", nullable = false)
	private Item item;
	@ManyToOne
	@JoinColumn(name = "receipt_id", nullable = false)
	private Receipt receipt;
	public ReceiptItems() {

	}
	public ReceiptItems(Item item, Receipt receipt) {
		this.item = item;
		this.receipt = receipt;
	}

	public UUID getId() {
		return this.id;
	}
	public Item getItem() {
		return this.item;
	}
	public Receipt getReceipt() {
		return this.receipt;
	}
}
