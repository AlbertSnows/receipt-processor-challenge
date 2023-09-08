package com.example.receiptprocessor.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping("/receipts")
public class ReceiptController {
	private static final Logger logger = LoggerFactory.getLogger(ReceiptController.class);
	@Autowired
	private final ObjectMapper objectMapper;

	public ReceiptController(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@PostMapping("/process")
	public ResponseEntity<String> recordReceipt(@RequestBody JsonNode receipt) {

		var json = "{" +
						"\"retailer\": \"Target\"," +
						"\"purchaseDate\": \"2022-01-02\"," +
						"\"purchaseTime\": \"13:13\"," +
						"\"total\": \"1.25\"," +
						"\"items\": [" +
						"{\"shortDescription\": \"Pepsi - 12-oz\", \"price\": \"1.25\"}" +
						"]" + "}";
//		var x = Map.of(
//						"retailer", "string",
//						"purchaseDate", "date",
//						"purchaseTime", "time",
//						"total", "currency",
//						"items", "array");
		// Specify the path to your JSON file
		var jsonFile = Paths.get("src/main/java/com/example/receiptprocessor/data/schemas/item.json");
		try {
			// Read JSON from the file and map it to a Java object
			var schemaString = Files.readString(jsonFile);
			JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
			var schema = factory.getSchema(schemaString);
//			var schema = JsonSchemaFactory.builder().build().getSchema(schemaString);
			var jsonTree = objectMapper.readTree(json);
			var result = schema.validate(jsonTree);
			logger.info(result.toString());
		} catch (Exception e) {
			logger.info(e.getMessage());
		}

//		try {
//			// Access and work with the JSON data using JsonNode
//			Receipt person = objectMapper.readValue(json, Receipt.class);
//			var result = objectMapper.readTree(json);
//			var thing = "";
//			//			return ResponseEntity.ok("Name: " + name + ", Age: " + age);
//		} catch (Exception e) {
//			return ResponseEntity.badRequest().body("Invalid JSON format");
//		}

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@GetMapping("/{id}/points")
	public ResponseEntity<Integer> getPoints(@PathVariable Long id) {
		// Implement retrieval logic here, e.g., fetch points for a receipt by ID
		// Replace 'Integer' with the actual type of the data you're returning
		Integer points = 100;
		return ResponseEntity.ok(points);
	}
}
