package com.example.receiptprocessor.data.records;

import org.springframework.http.HttpStatus;

import java.util.Map;

public record SimpleHTTPResponse(HttpStatus statusCode, Map<String, String> body) { }