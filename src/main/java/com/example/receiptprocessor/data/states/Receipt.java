package com.example.receiptprocessor.data.states;

import com.example.receiptprocessor.data.entities.Points;
import com.example.receiptprocessor.data.records.SimpleHTTPResponse;
import com.example.receiptprocessor.utility.Shorthand;
import io.vavr.Function0;
import io.vavr.Lazy;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

/**
 * This class encompasses, currently, all recognized states we could encounter when
 * specifically working with our receipts data
 */
public class Receipt {

	// get already calculated points
	public static @org.jetbrains.annotations.NotNull Pair<Lazy<Boolean>, Lazy<SimpleHTTPResponse>>
	getPoints(@org.jetbrains.annotations.NotNull Function0<Points> getPoints) {
		var points = getPoints.get();
		return Shorthand.makeLazyStatePair(
						() -> points != null,
						() -> new SimpleHTTPResponse(HttpStatus.OK,
										Map.of("points", points.getTotalPointsForReceipt().toString())));
	}

	// calc points for receipt
	public static @org.jetbrains.annotations.NotNull Pair<Lazy<Boolean>, Lazy<SimpleHTTPResponse>>
	calculatePoints(Function0<Points> points, Lazy<Integer> calcPoints) {
		return Shorthand.makeLazyStatePair(
						() -> points.get() == null,
						() -> new SimpleHTTPResponse(HttpStatus.OK,
										Map.of("points", calcPoints.get().toString())));
	}

	// No receipt id
	public static @org.jetbrains.annotations.NotNull Pair<Lazy<Boolean>, Lazy<SimpleHTTPResponse>>
	idNotFound(com.example.receiptprocessor.data.entities.Receipt receipt) {
		return Shorthand.makeLazyStatePair(
						() -> receipt == null,
						() -> new SimpleHTTPResponse(HttpStatus.NOT_FOUND,
										Map.of("error", "No receipt associated with provided id.")));
	}

	public static @NotNull SimpleHTTPResponse
	errorState(@org.jetbrains.annotations.NotNull List<Pair<String, String>> invalidData) {
		var listOfStateStrings = invalidData.stream().map(Pair::toString).toList();
		var errors = Map.of("errors", listOfStateStrings.toString());
		return new SimpleHTTPResponse(HttpStatus.BAD_REQUEST, errors);
	}
}
