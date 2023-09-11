package com.example.receiptprocessor.data.entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "points")
public class Points {
	@Id
	@GeneratedValue
	@Column(columnDefinition = "uuid")
	private UUID id;
	@Column(name = "points")
	private Integer points;

	@OneToOne
	private Receipt receipt;

	public Points() {

	}

	public Points(Integer points) {
		this.points = points;
	}

	public UUID getId() {
		return this.id;
	}

	public Integer getPoints() {
		return this.points;
	}
}
