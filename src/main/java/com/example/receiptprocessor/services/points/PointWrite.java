package com.example.receiptprocessor.services.points;

import com.example.receiptprocessor.data.entities.Points;
import com.example.receiptprocessor.data.entities.Receipt;
import com.example.receiptprocessor.data.repositories.PointsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointWrite {
	@Autowired
	private final PointsRepository pointRepo;

	public PointWrite(PointsRepository pointRepo) {
		this.pointRepo = pointRepo;
	}
	public Points hydrate(int totalPoints, Receipt receipt) {
		return new Points(totalPoints, receipt);
	}

	public void save(Points points) {
		pointRepo.save(points);
	}
}
