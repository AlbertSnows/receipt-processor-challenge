package com.example.receiptprocessor.services.points;

import com.example.receiptprocessor.data.entities.Item;
import com.example.receiptprocessor.data.entities.Points;
import com.example.receiptprocessor.data.entities.Receipt;
import com.example.receiptprocessor.data.records.SimpleHTTPResponse;
import com.example.receiptprocessor.data.repositories.PointsRepository;
import com.example.receiptprocessor.data.states.Json;
import com.example.receiptprocessor.services.item.ItemRead;
import com.example.receiptprocessor.utility.Collections;
import io.vavr.Function0;
import io.vavr.Lazy;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PointWrite {
	@Autowired
	private final PointsRepository pointRepo;
	@Autowired
	private final ItemRead itemRead;
	@Autowired
	private final PointRead pointRead;

	public PointWrite(PointsRepository pointRepo,
	                  ItemRead itemRead,
	                  PointRead pointRead) {
		this.pointRepo = pointRepo;
		this.itemRead = itemRead;
		this.pointRead = pointRead;
	}

	@Contract(value = "_, _ -> new", pure = true)
	public static @NotNull Points hydrate(int totalPoints, Receipt receipt) {
		return new Points(totalPoints, receipt);
	}

	public static @NotNull Integer calculatePoints(Receipt receipt, List<Item> receiptItems) {
		var possiblePointStates =
						com.example.receiptprocessor.data.states.Points.possibleStatesForReceiptPoints(receipt, receiptItems);
		var relevantPointStates = possiblePointStates.stream()
						.filter(state -> state.getFirst().get()).toList();
		return relevantPointStates.stream()
						.map(state -> state.getSecond().get())
						.mapToInt(Integer::intValue)// returns an int
						.sum();
	}

	public void save(Points points) {
		pointRepo.save(points);
	}

	public Integer calculateAndSavePoints(Receipt receipt) {
		var receiptItems = itemRead.findAllForReceipt(receipt);
		var totalPoints = calculatePoints(receipt, receiptItems);
		save(hydrate(totalPoints, receipt));
		return totalPoints;
	}

	public SimpleHTTPResponse getsertPointOutcome(Receipt receipt, boolean validUUID) {
		var getPoints = Function0.of(() -> pointRead.findByReceipt(receipt)).memoized();
		var calcPoints = Lazy.of(() -> calculateAndSavePoints(receipt));
		return Collections.firstTrueStateOf(List.of(
										Json.invalidID(validUUID),
										com.example.receiptprocessor.data.states.Receipt.idNotFound(receipt),
										com.example.receiptprocessor.data.states.Receipt.getPoints(getPoints),
										com.example.receiptprocessor.data.states.Receipt.calculatePoints(getPoints, calcPoints)))
						.get();
	}
}
