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
	private final String retailer;
	private final LocalDateTime purchaseDateTime;
	private final BigDecimal total;

	public Receipt(String retailer, LocalDateTime purchaseDateTime, BigDecimal total) {
		this.retailer = retailer;
		this.purchaseDateTime = purchaseDateTime;
		this.total = total;
	}
}
