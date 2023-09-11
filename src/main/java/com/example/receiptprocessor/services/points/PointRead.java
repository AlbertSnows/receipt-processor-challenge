package com.example.receiptprocessor.services.points;

import com.example.receiptprocessor.data.entities.Points;
import com.example.receiptprocessor.data.entities.Receipt;
import com.example.receiptprocessor.data.repositories.PointsRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class PointRead {
	@Autowired
	private final PointsRepository pointRepo;

	public PointRead(PointsRepository pointRepo) {
		this.pointRepo = pointRepo;
	}

	public Points findByReceipt(Receipt receipt) {
		return pointRepo.findByReceipt(receipt);
	}

}
