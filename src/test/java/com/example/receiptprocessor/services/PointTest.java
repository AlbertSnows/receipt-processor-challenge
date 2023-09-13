package com.example.receiptprocessor.services;

import com.example.receiptprocessor.data.entities.Item;
import com.example.receiptprocessor.data.entities.Receipt;
import com.example.receiptprocessor.services.points.PointWrite;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class PointTest {
	@Test
	void calculatePointsTest() {
		String dateTimeString = "2023-09-09T13:00";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
		LocalDateTime localDateTime = LocalDateTime.parse(dateTimeString, formatter);
		var receipt = new Receipt(
						"foobar",
						localDateTime,
						new BigDecimal("10.00"));
		var receiptItems = List.of(
						new Item("blah", new BigDecimal("3.00")));
		var outcome = PointWrite.calculatePoints(receipt, receiptItems);
		assertThat(outcome).isEqualTo(37);
	}

	void getsertPointOutcomeTest() {
		// getsert does a few things
		// it gets a cacheable points entity or
		// it calculates points
		// and then it evaluates the different states
		// we've already tested firstTrueStateOf
		// and we've tested calc points above
		// everything else is just vavr or
		// spring boot boilerplate
	}
}
