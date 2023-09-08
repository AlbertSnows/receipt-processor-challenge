package com.example.receiptprocessor.data.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
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
	private final LocalDate purchaseDate;
	private final LocalTime purchaseTime;
	private final BigDecimal total;

	public Receipt(String retailer, LocalDate purchaseDate, LocalTime purchaseTime, BigDecimal total) {
		this.retailer = retailer;
		this.purchaseDate = purchaseDate;
		this.purchaseTime = purchaseTime;
		this.total = total;
	}
}
