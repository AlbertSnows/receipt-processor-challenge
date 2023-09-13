package com.example.receiptprocessor.utility;

import com.example.receiptprocessor.data.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;

import java.nio.file.Path;
import java.util.List;

import static com.example.receiptprocessor.utility.Validation.validateJsonSchemaFrom;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CoreTest {
	@Test
	void combineTest() {
		var input = List.of(List.of(1, 2), List.of(3));
		var outcome = Collections.combine(input).toList();
		assertThat(outcome.size()).isEqualTo(3);
	}

	@Test
	void sanityTest() {
		assertThat(1 + 1).isEqualTo(2);
	}

	@Test
	void firstTrueStateOfTest() {
		var stateOne = Collections.firstTrueStateOf(List.of(
						Shorthand.makeLazyStatePair(() -> true, () -> "case A"),
						Shorthand.makeLazyStatePair(() -> true, () -> "case B"))).get();
		assertThat(stateOne).isEqualTo("case A");
		var stateTwo = Try.of(() -> Collections.firstTrueStateOf(List.of(
						Shorthand.makeLazyStatePair(() -> false, () -> "case C"),
						Shorthand.makeLazyStatePair(() -> false, () -> "cade D"))));
		assertThat(stateTwo.isFailure()).isTrue();
		var stateThree = Collections.firstTrueStateOf(List.of(
						Shorthand.makeLazyStatePair(() -> false, () -> "case E"),
						Shorthand.makeLazyStatePair(() -> true, () -> "case F"))).get();
		assertThat(stateThree).isEqualTo("case F");
	}

	@Test
	void firstTrueEagerStateOfTest() {
		var stateOne = Collections.firstTrueEagerStateOf(List.of(
						Pair.of(true, "case A"),
						Pair.of(true, "case B")));
		assertThat(stateOne).isEqualTo("case A");
		var stateTwo = Try.of(() -> Collections.firstTrueEagerStateOf(List.of(
						Pair.of(false, "case C"),
						Pair.of(false, "cade D"))));
		assertThat(stateTwo.isFailure()).isTrue();
		var stateThree = Collections.firstTrueEagerStateOf(List.of(
						Pair.of(false, "case E"),
						Pair.of(true, "case F")));
		assertThat(stateThree).isEqualTo("case F");
	}

	@Test
	void validateJsonSchemaFromTest() throws JsonProcessingException {
		var objMapper = new ObjectMapper();

		var goodPath = Path.of("src/main/resources/schemas/item.json");
		var validateFromGoodFile = validateJsonSchemaFrom(goodPath);
		var matchesSchema = "{\"shortDescription\": \"Pepsi - 12-oz\", \"price\": 1}";
		var goodJsonNode = objMapper.readTree(matchesSchema);
		var happyPath = validateFromGoodFile.apply(goodJsonNode).get();
		assertThat(happyPath.getFirst()).isEqualTo(Constants.MATCHED_SCHEMA);

		var failsSchemaJson = "{\"shortDescriptio\": \"Pepsi - 12-oz\", \"price\": \"1.25\"}";
		var doesNotMatchSchemaNode = objMapper.readTree(failsSchemaJson);
		var doesNotMatchSchema = validateFromGoodFile.apply(doesNotMatchSchemaNode).get();
		assertThat(doesNotMatchSchema.getFirst()).isEqualTo(Constants.INVALID_SCHEMA);

		var badPath = Path.of("foo");
		var badFileCase = validateJsonSchemaFrom(badPath);
		var noSchemaFile = badFileCase.apply(goodJsonNode).get();
		assertThat(noSchemaFile.getFirst()).isEqualTo(Constants.NO_FILE);
		// is there a way to get invalid json into the function?
//		var malformedJson = "{name: \"John\", age: 30}\n";
//		var malformedJsonNode = objMapper.readTree(malformedJson);
		// 		var malformed = validateFromGoodFile.apply(malformedJsonNode);
		var otherIssue = validateFromGoodFile.apply(null).get();
		assertThat(otherIssue.getFirst()).isEqualTo(Constants.UNRECOGNIZED_PROBLEM);
	}
}
