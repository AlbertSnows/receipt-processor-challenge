package com.example.receiptprocessor.data.states;

import com.example.receiptprocessor.data.records.SimpleHTTPResponse;
import com.example.receiptprocessor.utility.Shorthand;
import com.networknt.schema.ValidationMessage;
import io.vavr.Lazy;
import io.vavr.control.Try;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hibernate.validator.internal.metadata.core.ConstraintHelper.MESSAGE;

/**
 * This class encompasses, currently, all recognized states we could encounter when
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

	/**
	 * A list of all recognized states that could happen when trying
	 * to process a receipt
	 * @param validationResultOrFailure A Try (i.e. Option) type that represents either
	 *                                  A) the result of trying to validate the incoming json
	 *                                  B) one of the many possible failure cases that could happen
	 *                                     when trying to parse a json request
	 * @return A list of endpoint states
	 * It's meant to encompass all expected states of,
	 *  in this case, the processReceipt request
	 */
	@org.jetbrains.annotations.Unmodifiable
	@org.jetbrains.annotations.Contract("_ -> new")
	public static List<Pair<Lazy<Boolean>, Lazy<SimpleHTTPResponse>>>
	processReceiptStatesOn(Try<Set<ValidationMessage>> validationResultOrFailure) {
		return List.of(
						Receipt.created(validationResultOrFailure),
						Json.invalid(validationResultOrFailure),
						Json.noSchemaFile(validationResultOrFailure),
						Json.malformed(validationResultOrFailure),
						Json.unknownProblem(validationResultOrFailure));
	}
}
