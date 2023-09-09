package com.example.receiptprocessor.utility;

import io.vavr.Lazy;
import org.springframework.data.util.Pair;

import java.util.List;

public class Collections {
	private Collections() {
		throw new IllegalStateException("Utility class");
	}
	public static <V> V getFirstTrue(List<Pair<Lazy<Boolean>, Lazy<V>>> stateMap, Lazy<V> failCase) {
		for (Pair<Lazy<Boolean>, Lazy<V>> statePair : stateMap) {
			var value = statePair.getFirst().get();
			if (Boolean.TRUE.equals(value)) {
				return statePair.getSecond().get();
			}
		}
		return failCase.get();
	}
}
