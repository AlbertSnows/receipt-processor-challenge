package com.example.receiptprocessor.services.points;

import com.example.receiptprocessor.data.entities.Points;
import com.example.receiptprocessor.data.entities.Receipt;
import com.example.receiptprocessor.data.repositories.PointsRepository;
import com.example.receiptprocessor.services.item.ItemRead;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointWrite {
	@Autowired
	private final PointsRepository pointRepo;
	@Autowired
	private final ItemRead itemRead;
	public PointWrite(PointsRepository pointRepo,
	                  ItemRead itemRead) {
		this.pointRepo = pointRepo;
		this.itemRead = itemRead;
	}
	@Contract(value = "_, _ -> new", pure = true)
	public static @NotNull Points hydrate(int totalPoints, Receipt receipt) {
		return new Points(totalPoints, receipt);
	}

	public void save(Points points) {
		pointRepo.save(points);
	}

	public Integer calculatePoints(Receipt receipt) {
		var receiptItems = itemRead.findAll(receipt);
		var possiblePointStates =
						com.example.receiptprocessor.data.states.Points.possibleStatesForReceiptPoints(receipt, receiptItems);
		var relevantPointStates = possiblePointStates.stream()
						.filter(state -> state.getFirst().get()).toList();
		var totalPoints = relevantPointStates.stream()
						.map(state -> state.getSecond().get())
						.mapToInt(Integer::intValue)// returns an int
						.sum();
		save(hydrate(totalPoints, receipt));
		return totalPoints;
	}
}
