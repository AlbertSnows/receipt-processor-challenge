package com.example.receiptprocessor.services;

import com.example.receiptprocessor.data.Constants;
import com.example.receiptprocessor.services.item.ItemRead;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Path;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class ItemTest {
	//todo: go write down accomplishments cv
	@Test
	void updatePriceNodeOrFailTest() throws JsonProcessingException {
		var objectMapper = new ObjectMapper();
		ObjectNode itemNode = (ObjectNode) objectMapper.readTree("{\"a\": \"foo\"}");
		var priceNode = objectMapper.readTree("\"1.01\"");
		var outcomeNode = objectMapper.readTree("{\"a\": \"foo\", \"price\": 1.01}");
		var outcome = ItemRead.updatePriceNodeOrFail(itemNode).apply(priceNode).get();
		assertThat(outcome.get("price").asText()).isEqualTo(outcomeNode.get("price").asText());
	}

	@Test
	void getAndUpsertPriceOrFailTest() throws JsonProcessingException {
		var objectMapper = new ObjectMapper();
		ObjectNode node = (ObjectNode) objectMapper.readTree("{\"a\": \"foo\"}");
		var outcome = ItemRead.getAndUpsertPriceOrFail(node);
		assertThat(outcome.isFailure()).isTrue();
	}

	@Test
	void convertPriceToNumberTest() throws JsonProcessingException {
		var objectMapper = new ObjectMapper();
		ObjectNode node = (ObjectNode) objectMapper.readTree("{\"price\": \"100.1\"}");
		var outcome = ItemRead.convertPriceToNumber(node);
		assertThat(outcome.get().get("price").asText()).isEqualTo("100.1");
	}

	@Test
	void validateItems() throws JsonProcessingException {
		var objectMapper = new ObjectMapper();
		var successJson = "{\"items\": [ " +
						"{\"shortDescription\": \"Pepsi - 12-oz\", \"price\": \"1.25\"}, " +
						"{\"shortDescription\": \"Dasani\", \"price\": \"1.40\"}" +
						"]}";
		var goodNode = objectMapper.readTree(successJson);
		var goodPath = Path.of("src/main/resources/schemas/item.json");
		var successCase = ItemRead.validateItems(goodPath, goodNode.get("items")).get();
		assertThat(successCase.stream().anyMatch(pair -> !pair.getFirst().equals(Constants.MATCHED_SCHEMA))).isFalse();
		var multipleProblemsJson = "{\"items\": [ " +
						"{\"hortDescription\": \"Pepsi - 12-oz\", \"price\": \"1.25\"}, " +
						"{\"shortDescription\": \"Dasani\", \"price\": \"t\"}" +
						"]}";
		var multiProblemNode = objectMapper.readTree(multipleProblemsJson);
		var multipleProblemsCase = ItemRead.validateItems(goodPath, multiProblemNode.get("items")).get();
		assertThat(multipleProblemsCase.size()).isEqualTo(2);

		// bonus: only valid, or only casting

		// would need to find an unhandled case
//		ObjectNode json = null;
//		var unknownProblemNode = objectMapper.readTree(multipleProblemsJson);
//		var unknownProblemCase = ItemRead.validateItems(goodPath, objectMapper.nullNode()).get();
//		assertThat(unknownProblemCase.size()).isEqualTo(1);

	}
}
