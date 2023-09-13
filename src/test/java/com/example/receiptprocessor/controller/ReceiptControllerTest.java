package com.example.receiptprocessor.controller;

import com.example.receiptprocessor.controllers.ReceiptController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

// ref -> https://docs.spring.io/spring-boot/docs/1.5.2.RELEASE/reference/html/boot-features-testing.html
//@WebMvcTest(ReceiptController.class)
@SpringBootTest
//@AutoConfigureMockMvc
class ReceiptControllerTest {
	//	@Autowired
//	private MockMvc mockMvc;
	@Autowired
	private ReceiptController controller;

	@Test
	void recordReceiptTest() throws Exception {
		var objectMapper = new ObjectMapper();
		var goodJson = "{" +
						"\"retailer\": \"Target\"," +
						"\"purchaseDate\": \"2022-01-02\"," +
						"\"purchaseTime\": \"13:13\"," +
						"\"total\": \"1.25\"" + "," +
						"\"items\": [" +
						"{\"shortDescription\": \"Pepsi - 12-oz\", \"price\": \"1.25\"}" +
						"]" + "}";
		var goodJsonNode = objectMapper.readTree(goodJson);
		var goodOutcome = controller.recordReceipt(goodJsonNode);
		assertThat(goodOutcome.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		var badJson = "{" +
						"\"rtailer\": \"Target\"," +
						"\"purchaseDate\": \"2022-01-02\"," +
						"\"purchaseTime\": \"13:13\"," +
						"\"total\": \"1.25\"" + "," +
						"\"items\": [" +
						"{\"sortDescription\": \"Pepsi - 12-oz\", \"price\": \"1.25\"}" +
						"]" + "}";
		var badJsonNode = objectMapper.readTree(badJson);
		var badOutcome = controller.recordReceipt(badJsonNode);
		assertThat(badOutcome.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
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
