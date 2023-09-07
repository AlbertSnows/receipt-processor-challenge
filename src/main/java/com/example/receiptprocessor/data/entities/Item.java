package com.example.receiptprocessor.data.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "items")
public class Item {
	@Id
	@GeneratedValue
	@Column(columnDefinition = "uuid")
	private UUID id;
	private String shortDescription;
	private BigDecimal price;
}
