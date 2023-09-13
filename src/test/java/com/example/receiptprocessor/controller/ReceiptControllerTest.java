package com.example.receiptprocessor.controller;

import com.example.receiptprocessor.controllers.ReceiptController;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class ReceiptControllerTest {
	@Autowired
	private ReceiptController controller;

	@Test
	void recordReceiptTest(@RequestBody JsonNode receipt) {
//		var receiptOutcomes = receiptRead.validateReceipt(receipt);
//		var itemOutcomes = itemRead.tryValidatingItems(receipt);
//		var validReceipt = Boolean.TRUE.equals(matchedSchema(receiptOutcomes));
//		var validItems = itemOutcomes.stream()
//						.noneMatch(itemOutcome -> Boolean.FALSE.equals(matchedSchema(itemOutcome)));
//		Stream<Pair<String, String>> allOutcomes = Stream.concat(
//						Stream.of(receiptOutcomes),
//						itemOutcomes.stream());
//		List<Pair<String, String>> invalidOutcomes = Collections.firstTrueEagerStateOf(List.of(
//						Pair.of(validReceipt && validItems, List.of()),
//						Pair.of(validReceipt, itemOutcomes),
//						Pair.of(validItems, List.of(receiptOutcomes)),
//						Pair.of(true, allOutcomes.toList())));
//		var canSave = invalidOutcomes.isEmpty();
//		var receiptId = canSave ? receiptWrite.runProcessQueries(receipt) : null;
//		var responseInfo = receiptId != null ?
//						new SimpleHTTPResponse(HttpStatus.CREATED, Map.of("id", receiptId)) :
//						com.example.receiptprocessor.data.states.Receipt.errorState(invalidOutcomes);
//		return ResponseEntity.status(responseInfo.statusCode()).body(responseInfo.body());
	}

	@Test
	void listAllDataTest() {
		var outcome = controller.listAllData();
		assertThat(outcome.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	/**
	 * Turns out, yes you can have spring autowire dependencies in
	 * that's pretty cool
	 */
	@Test
	void getPointsTest() {
		var exampleUUID = UUID.randomUUID();
		var outcome = controller.getPoints(String.valueOf(exampleUUID));
		assertThat(outcome.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		// could also test ok status
	}
}
