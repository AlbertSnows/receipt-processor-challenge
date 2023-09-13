package com.example.receiptprocessor.state;

import com.example.receiptprocessor.data.entities.Item;
import com.example.receiptprocessor.data.entities.Receipt;
import com.example.receiptprocessor.data.states.Points;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class PointTest {
	@Test
	void retailerNameCountTest() {
		var receipt = new Receipt("""
						ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789
						!"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~""",
						LocalDateTime.now(),
						new BigDecimal("1.23")
		);
		var retailerNameCount = Points.retailerNameCount(receipt);
		assertThat(retailerNameCount.getSecond().get()).isEqualTo(62);
	}

	@Test
	void itemPricePointsFromItemDescriptionTest() {
		var receiptItems = List.of(
						new Item("first", new BigDecimal("1.00")),
						new Item("second", new BigDecimal("10.00")));
		var outcome = Points.itemPricePointsFromItemDescription(receiptItems);
		assertThat(outcome.getSecond().get()).isEqualTo(2);
	}
}
