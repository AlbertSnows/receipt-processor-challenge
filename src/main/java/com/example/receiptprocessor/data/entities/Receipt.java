package com.example.receiptprocessor.data.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Receipt {
	private String retailer;
	private LocalDate purchaseDate;
	private LocalTime purchaseTime;
	private BigDecimal total;
	private List<Item> items;
}
