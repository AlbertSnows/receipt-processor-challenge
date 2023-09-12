package com.example.receiptprocessor.deprecated;

import com.example.receiptprocessor.utility.Collections;
import io.vavr.Function1;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.example.receiptprocessor.controllers.ReceiptController.*;

/**
 * For documentation purposes
 */
@SuppressWarnings("java:S1133")
public class Utility {
	private Utility() {
		throw new IllegalStateException("Utility class");
	}


	/**
	 * @deprecated has weird casting behavior, generics are tough to work with
	 */
	@Contract(pure = true)
	@Deprecated(since="sept 11")
	public static <V> @NotNull Function1<Map<String, V>, V>
	actOnProcessReceiptValidationOutcomes(boolean validReceipt, boolean validItems) {
		return actions -> Collections.firstTrueEagerStateOf(List.of(
						Pair.of(validReceipt && validItems, actions.get(BOTH_VALID)),
						Pair.of(validReceipt, actions.get(RECEIPT_VALID)),
						Pair.of(validItems, actions.get(ITEM_VALID)),
						Pair.of(true, actions.get(NEITHER_VALID))));
	}

	/**
	 * @deprecated suspect, maybe just use stream?
	 * causes issues if trying to add to immutable lists
	 * streams seem to be better for this
	 */
	@Contract(pure = true)
	@Deprecated(since = "sept 11")
	public static <V> @NotNull Function<V, List<V>> add(List<V> list) {
		return item -> {
			list.add(item);
			return list;
		};
	}

	/**
	 * @deprecated not necessary?
	 */
	@Deprecated(since = "sept 11")
	public static @NotNull String makeStateString(@NotNull Pair<String, String> pair) {
		return "State pair: (" + pair.getFirst() + ", " + pair.getSecond() + ")\n";
	}
}
