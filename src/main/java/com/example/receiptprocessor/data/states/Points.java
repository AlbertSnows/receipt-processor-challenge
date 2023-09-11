package com.example.receiptprocessor.data.states;

import com.example.receiptprocessor.data.entities.Item;
import com.example.receiptprocessor.data.entities.Receipt;
import com.example.receiptprocessor.data.records.ItemDescLengthAndPrice;
import com.example.receiptprocessor.utility.Shorthand;
import io.vavr.Function0;
import io.vavr.Lazy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.data.util.Pair;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class Points {
	private Points() {
		throw new IllegalStateException("Utility class");
	}

	public static @org.jetbrains.annotations.NotNull Pair<Lazy<Boolean>, Lazy<Integer>>
	retailerNameCount(Receipt receipt) {
		return Shorthand.makeLazyStatePair(
						() -> receipt != null,
						() -> receipt.getRetailer().length());
	}
	public static @org.jetbrains.annotations.NotNull Pair<Lazy<Boolean>, Lazy<Integer>>
	roundTotal(BigDecimal retailerPurchaseTotal) {
		return Shorthand.makeLazyStatePair(
						() -> retailerPurchaseTotal.scale() == 0,
						() -> 50);
	}
	public static @org.jetbrains.annotations.NotNull Pair<Lazy<Boolean>, Lazy<Integer>>
	quarterFractional(@NotNull BigDecimal retailerPurchaseTotal) {
		Function0<Boolean> quarterFractional = () -> retailerPurchaseTotal.scale() == 0
						|| retailerPurchaseTotal.remainder(new BigDecimal("0.25")).equals(BigDecimal.ZERO);
		return Shorthand.makeLazyStatePair(quarterFractional, () -> 25);
	}

	public static @org.jetbrains.annotations.NotNull Pair<Lazy<Boolean>, Lazy<Integer>>
	pointsPerTwoItems(List<Item> receiptItems) {
		return Shorthand.makeLazyStatePair(
				() -> receiptItems.size() >= 2,
				() -> 5 * (receiptItems.size() / 2));
	}
	public static @org.jetbrains.annotations.NotNull Pair<Lazy<Boolean>, Lazy<Integer>>
	itemPricePointsFromItemDescription(@NotNull List<Item> receiptItems) {
		//todo: pretrim descriptions
		var relevantItemPrices = receiptItems.stream()
					.map(item -> new ItemDescLengthAndPrice(item.getShortDescription().length(), item.getPrice()))
					.filter(itemRecord -> itemRecord.length() % 3 == 0).toList();
		return Shorthand.makeLazyStatePair(
		() -> !relevantItemPrices.isEmpty(),
		() -> relevantItemPrices.stream()
						.map(itemRecord -> itemRecord.price().multiply(BigDecimal.valueOf(0.2)))
						.map(BigDecimal::scale)
						.mapToInt(Integer::intValue)
						.sum());
	}
	public static @org.jetbrains.annotations.NotNull Pair<Lazy<Boolean>, Lazy<Integer>>
	oddPurchaseDate(@NotNull LocalDateTime purchaseDateTime) {
		var purchaseMonthDay = purchaseDateTime.getDayOfMonth();
		return Shorthand.makeLazyStatePair(
						() -> purchaseMonthDay % 2 == 1,
						() -> 6);
	}
	public static @org.jetbrains.annotations.NotNull Pair<Lazy<Boolean>, Lazy<Integer>>
	timeBetweenTwoAndFour(@NotNull LocalDateTime purchaseDateTime) {
		var purchaseTime = purchaseDateTime.toLocalTime();
		return Shorthand.makeLazyStatePair(
						() -> purchaseTime.isAfter(LocalTime.of(14, 0))
										&& purchaseTime.isBefore(LocalTime.of(16, 0)),
						() -> 10);
	}

	public static @Unmodifiable List<Pair<Lazy<Boolean>, Lazy<Integer>>>
	possibleStatesForReceiptPoints(@NotNull Receipt receipt, List<Item> receiptItems) {
		var retailerPurchaseTotal = receipt.getTotal();
		var purchaseDateTime = receipt.getPurchaseDateTime();
		return List.of(
						Points.retailerNameCount(receipt),
						Points.roundTotal(retailerPurchaseTotal),
						Points.quarterFractional(retailerPurchaseTotal),
						Points.pointsPerTwoItems(receiptItems),
						Points.itemPricePointsFromItemDescription(receiptItems),
						Points.oddPurchaseDate(purchaseDateTime),
						Points.timeBetweenTwoAndFour(purchaseDateTime));

	}
}
