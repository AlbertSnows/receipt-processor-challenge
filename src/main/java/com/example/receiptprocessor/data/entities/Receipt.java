package com.example.receiptprocessor.data.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "receipts")
public class Receipt {
	@Id
	@GeneratedValue
	@Column(columnDefinition = "uuid")
	private UUID id;
	private String retailer;
	private LocalDateTime purchaseDateTime;
	private BigDecimal total;
	public Receipt() {

	}

	public Receipt(String retailer, LocalDateTime purchaseDateTime, BigDecimal total) {
		this.retailer = retailer;
		this.purchaseDateTime = purchaseDateTime;
		this.total = total;
	}

	public UUID getId() {
		return this.id;
	}

	public String getRetailer() {
		return this.retailer;
	}

	public BigDecimal getTotal() {
		return this.total;
	}

	public LocalDateTime getPurchaseDateTime() {
		return this.purchaseDateTime;
	}
}
