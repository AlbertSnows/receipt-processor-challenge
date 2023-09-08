package com.example.receiptprocessor.data.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "receipts")
public class Receipt {
	@Id
	@GeneratedValue
	@Column(columnDefinition = "uuid")
	private UUID id;
	private String retailer;
	private LocalDate purchaseDate;
	private LocalTime purchaseTime;
	private BigDecimal total;
//	private List<Item> items;
}
