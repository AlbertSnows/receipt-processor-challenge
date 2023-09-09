package com.example.receiptprocessor.data.states;

import com.example.receiptprocessor.data.records.SimpleHTTPResponse;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class Database {
	private Database() {
		throw new IllegalStateException("Utility class");
	}

	public static SimpleHTTPResponse couldNotWrite() {
		var message = "Could not write entry to the db.";
		return new SimpleHTTPResponse(HttpStatus.INTERNAL_SERVER_ERROR, Map.of("message", message));
	}
}
