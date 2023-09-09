package com.example.receiptprocessor.data.states;

import com.example.receiptprocessor.data.records.SimpleHTTPResponse;
import com.example.receiptprocessor.utility.Shorthand;
import com.networknt.schema.ValidationMessage;
import io.vavr.Lazy;
import io.vavr.control.Try;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.Set;

import static org.hibernate.validator.internal.metadata.core.ConstraintHelper.MESSAGE;

/**
 * This class encompasses, currently all recognized states we could encounter when
 * specifically working with our receipts data
 */
public class Receipt {
	private Receipt() {
		throw new IllegalStateException("Utility class");
	}
	public static Pair<Lazy<Boolean>, Lazy<SimpleHTTPResponse>> created(Try<Set<ValidationMessage>> validationResultOrFailure) {
		return Shorthand.makeLazyStatePair(
						() -> validationResultOrFailure.isSuccess() && validationResultOrFailure.get().isEmpty(),
						() -> new SimpleHTTPResponse(HttpStatus.CREATED, Map.of(MESSAGE, "created")));
	}
}
