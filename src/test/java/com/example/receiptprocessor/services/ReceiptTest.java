package com.example.receiptprocessor.services;

import com.example.receiptprocessor.services.receipt.ReceiptRead;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class ReceiptTest {

	@Test
	void saveTest() {
		// question: is there a way to test persisting data?
		assertThat(1 + 1).isEqualTo(2);
	}

	@Test
	void hydrateTest() throws JsonProcessingException {
		var json = "{" +
						"\"retailer\": \"Target\"," +
						"\"purchaseDate\": \"2022-01-02\"," +
						"\"purchaseTime\": \"13:13\"," +
						"\"total\": \"1.25\"" +
						// "," +
//						"\"items\": [" +
//						"{\"shortDescription\": \"Pepsi - 12-oz\", \"price\": \"1.25\"}" +
//						"]" +
						"}";
		var objMapper = new ObjectMapper();
		var testJsonAsNode = objMapper.readTree(json);
		var hydrateGoodJson = Try.of(() -> ReceiptRead.hydrateJson(testJsonAsNode));
		assertThat(hydrateGoodJson.isSuccess()).isTrue();
		var hydrateBadJson = Try.of(() -> ReceiptRead.hydrateJson(objMapper.nullNode()));
		assertThat(hydrateBadJson.isFailure()).isTrue();
	}
}
