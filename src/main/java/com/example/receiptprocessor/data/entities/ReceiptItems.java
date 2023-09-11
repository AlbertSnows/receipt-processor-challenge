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
	@JoinColumn(name = "item_id")
	private Item item;
	@ManyToOne
	@JoinColumn(name = "receipt_id")
	private Receipt receipt;
	public ReceiptItems() {

	}
	public ReceiptItems(Item item, Receipt receipt) {
		this.item = item;
		this.receipt = receipt;
	}
}
