package com.example.receiptprocessor.data.states;

import com.example.receiptprocessor.data.records.SimpleHTTPResponse;
import com.example.receiptprocessor.utility.Shorthand;
import io.vavr.Lazy;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class Points {
	public static @org.jetbrains.annotations.NotNull Pair<Lazy<Boolean>, Lazy<SimpleHTTPResponse>>
	retailerNameCount(com.example.receiptprocessor.data.entities.Receipt receipt) {
		return Shorthand.makeLazyStatePair(
						() -> receipt == null,
						() -> new SimpleHTTPResponse(HttpStatus.NOT_FOUND,
										Map.of("error", "No receipt associated with provided id.")));
	}
	public static @org.jetbrains.annotations.NotNull Pair<Lazy<Boolean>, Lazy<SimpleHTTPResponse>>
	roundTotal(com.example.receiptprocessor.data.entities.Receipt receipt) {
		return Shorthand.makeLazyStatePair(
						() -> receipt == null,
						() -> new SimpleHTTPResponse(HttpStatus.NOT_FOUND,
										Map.of("error", "No receipt associated with provided id.")));
	}

	public static @org.jetbrains.annotations.NotNull Pair<Lazy<Boolean>, Lazy<SimpleHTTPResponse>>
	quarterFractional(com.example.receiptprocessor.data.entities.Receipt receipt) {
		return Shorthand.makeLazyStatePair(
						() -> receipt == null,
						() -> new SimpleHTTPResponse(HttpStatus.NOT_FOUND,
										Map.of("error", "No receipt associated with provided id.")));
	}

public static @org.jetbrains.annotations.NotNull Pair<Lazy<Boolean>, Lazy<SimpleHTTPResponse>>
				pointsPerTwoItems(com.example.receiptprocessor.data.entities.Receipt receipt) {
				return Shorthand.makeLazyStatePair(
				() -> receipt == null,
				() -> new SimpleHTTPResponse(HttpStatus.NOT_FOUND,
				Map.of("error", "No receipt associated with provided id.")));
				}

public static @org.jetbrains.annotations.NotNull Pair<Lazy<Boolean>, Lazy<SimpleHTTPResponse>>
				itemPricePointsFromItemDescription(com.example.receiptprocessor.data.entities.Receipt receipt) {
				return Shorthand.makeLazyStatePair(
				() -> receipt == null,
				() -> new SimpleHTTPResponse(HttpStatus.NOT_FOUND,
				Map.of("error", "No receipt associated with provided id.")));
				}

public static @org.jetbrains.annotations.NotNull Pair<Lazy<Boolean>, Lazy<SimpleHTTPResponse>>
				oddPurchaseDate(com.example.receiptprocessor.data.entities.Receipt receipt) {
				return Shorthand.makeLazyStatePair(
				() -> receipt == null,
				() -> new SimpleHTTPResponse(HttpStatus.NOT_FOUND,
				Map.of("error", "No receipt associated with provided id.")));
				}

public static @org.jetbrains.annotations.NotNull Pair<Lazy<Boolean>, Lazy<SimpleHTTPResponse>>
				timeBetweenTwoAndFour(com.example.receiptprocessor.data.entities.Receipt receipt) {
				return Shorthand.makeLazyStatePair(
				() -> receipt == null,
				() -> new SimpleHTTPResponse(HttpStatus.NOT_FOUND,
				Map.of("error", "No receipt associated with provided id.")));
				}
}
