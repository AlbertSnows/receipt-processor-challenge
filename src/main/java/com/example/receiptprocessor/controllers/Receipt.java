package com.example.receiptprocessor.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/receipts")
public class Receipt {
	@PostMapping("/process")
	public ResponseEntity<Void> recordReceipt(@RequestBody Receipt receipt) {
		// Implement logic to store the receipt data in the database
		// You can use a service class to encapsulate this logic
		// Return appropriate HTTP response (e.g., 201 Created)
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
