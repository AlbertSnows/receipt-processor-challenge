package com.example.receiptprocessor.data.states;

import com.example.receiptprocessor.data.records.ItemDescLengthAndPrice;
import com.example.receiptprocessor.data.records.SimpleHTTPResponse;
import com.example.receiptprocessor.utility.Shorthand;
import io.vavr.Lazy;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Map;

public class Points {
	public static @org.jetbrains.annotations.NotNull Pair<Lazy<Boolean>, Lazy<SimpleHTTPResponse>>
	retailerNameCount(com.example.receiptprocessor.data.entities.@NotNull Receipt receipt) {
		var retailerPointNames = receipt.getRetailer().length();

		return Shorthand.makeLazyStatePair(
						() -> receipt == null,
						() -> new SimpleHTTPResponse(HttpStatus.NOT_FOUND,
										Map.of("error", "No receipt associated with provided id.")));
	}
	public static @org.jetbrains.annotations.NotNull Pair<Lazy<Boolean>, Lazy<SimpleHTTPResponse>>
	roundTotal(com.example.receiptprocessor.data.entities.Receipt receipt) {
		var totalIsRound = retailerPurchaseTotal.scale() == 0;
		return Shorthand.makeLazyStatePair(
						() -> receipt == null,
						() -> new SimpleHTTPResponse(HttpStatus.NOT_FOUND,
										Map.of("error", "No receipt associated with provided id.")));
	}

	public static @org.jetbrains.annotations.NotNull Pair<Lazy<Boolean>, Lazy<SimpleHTTPResponse>>
	quarterFractional(com.example.receiptprocessor.data.entities.Receipt receipt) {
		var quarterFractional = totalIsRound
						|| retailerPurchaseTotal.remainder(new BigDecimal("0.25")).equals(BigDecimal.ZERO);

		return Shorthand.makeLazyStatePair(
						() -> receipt == null,
						() -> new SimpleHTTPResponse(HttpStatus.NOT_FOUND,
										Map.of("error", "No receipt associated with provided id.")));
	}

public static @org.jetbrains.annotations.NotNull Pair<Lazy<Boolean>, Lazy<SimpleHTTPResponse>>
				pointsPerTwoItems(com.example.receiptprocessor.data.entities.Receipt receipt) {
	var haveRelevantItems = receiptItems.size() >= 2;

				return Shorthand.makeLazyStatePair(
				() -> receipt == null,
				() -> new SimpleHTTPResponse(HttpStatus.NOT_FOUND,
				Map.of("error", "No receipt associated with provided id.")));
				}

public static @org.jetbrains.annotations.NotNull Pair<Lazy<Boolean>, Lazy<SimpleHTTPResponse>>
				itemPricePointsFromItemDescription(com.example.receiptprocessor.data.entities.Receipt receipt) {
	//todo: pretrim descriptions
	var relevantItemPrices = receiptItems.stream()
					.map(item -> new ItemDescLengthAndPrice(item.getShortDescription().length(), item.getPrice()))
					.filter(itemRecord -> itemRecord.length() % 3 == 0);
	//todo: pass stream to success state (if not relevant)
//						.map(itemRecord -> itemRecord.price().multiply(BigDecimal.valueOf(0.2)))
//						.map(BigDecimal::scale)
//						.mapToInt(Integer::intValue)
//						.sum();
				return Shorthand.makeLazyStatePair(
				() -> receipt == null,
				() -> new SimpleHTTPResponse(HttpStatus.NOT_FOUND,
				Map.of("error", "No receipt associated with provided id.")));
				}

public static @org.jetbrains.annotations.NotNull Pair<Lazy<Boolean>, Lazy<SimpleHTTPResponse>>
				oddPurchaseDate(com.example.receiptprocessor.data.entities.Receipt receipt) {
	var purchaseMonthDay = purchaseDateTime.getDayOfMonth();
	var oddDay = purchaseMonthDay % 2 == 1;
				return Shorthand.makeLazyStatePair(
				() -> receipt == null,
				() -> new SimpleHTTPResponse(HttpStatus.NOT_FOUND,
				Map.of("error", "No receipt associated with provided id.")));
				}

public static @org.jetbrains.annotations.NotNull Pair<Lazy<Boolean>, Lazy<SimpleHTTPResponse>>
				timeBetweenTwoAndFour(com.example.receiptprocessor.data.entities.Receipt receipt) {
	var purchaseTime = purchaseDateTime.toLocalTime();
	var isBetweenTwoAndFour = purchaseTime.isAfter(LocalTime.of(14, 0))
					&& purchaseTime.isBefore(LocalTime.of(16, 0));

				return Shorthand.makeLazyStatePair(
				() -> receipt == null,
				() -> new SimpleHTTPResponse(HttpStatus.NOT_FOUND,
				Map.of("error", "No receipt associated with provided id.")));
				}
}
