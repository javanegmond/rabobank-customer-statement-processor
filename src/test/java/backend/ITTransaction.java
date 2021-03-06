package backend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

public class ITTransaction {

	static final String TRANSACTION_URL = "http://localhost:8080/transaction";
	static final RestTemplate REST_CLIENT = new RestTemplate();
	static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	static final HttpHeaders HEADERS = new HttpHeaders();

	@BeforeAll
	public static void setJsonContentType() {
		HEADERS.setContentType(MediaType.APPLICATION_JSON);
	}

	@Test
	public void alwaysTrueTest() {
		assertTrue(true);
	}

	@Test
	public void submitValidTransaction() throws JsonProcessingException {
		String validTransactionString = createJsonString(1, "NL123", 30, -5, "valid transaction", 25);
		HttpEntity<String> request = new HttpEntity<>(validTransactionString, HEADERS);
		ResponseEntity<String> response = REST_CLIENT.postForEntity(TRANSACTION_URL, request, String.class);
		assertEquals(200, response.getStatusCodeValue());

		String responseBodyString = response.getBody();
		JsonNode responseBody = OBJECT_MAPPER.readTree(responseBodyString);

		assertEquals("SUCCESSFUL", responseBody.get("result").asText());
		ArrayNode errors = (ArrayNode) responseBody.get("errorRecords");
		assertEquals(0, errors.size());
	}

	@Test
	public void submitDuplicateTransaction() throws JsonProcessingException {
		String originalTransactionString = createJsonString(2, "NL123", 30, -5, "valid transaction", 25);
		HttpEntity<String> request = new HttpEntity<>(originalTransactionString, HEADERS);
		ResponseEntity<String> response = REST_CLIENT.postForEntity(TRANSACTION_URL, request, String.class);
		assertEquals(200, response.getStatusCodeValue());

		String responseBodyString = response.getBody();
		JsonNode responseBody = OBJECT_MAPPER.readTree(responseBodyString);

		assertEquals("SUCCESSFUL", responseBody.get("result").asText());
		ArrayNode errors = (ArrayNode) responseBody.get("errorRecords");
		assertEquals(0, errors.size());

//		Now submit it again
		String duplicateTransactionString = createJsonString(2, "NL456", 30, -5, "duplicate transaction", 25);
		HttpEntity<String> request2 = new HttpEntity<>(duplicateTransactionString, HEADERS);
		ResponseEntity<String> response2 = REST_CLIENT.postForEntity(TRANSACTION_URL, request2, String.class);
		assertEquals(200, response2.getStatusCodeValue());

		String responseBodyString2 = response2.getBody();
		JsonNode responseBody2 = OBJECT_MAPPER.readTree(responseBodyString2);

		assertEquals("DUPLICATE_REFERENCE", responseBody2.get("result").asText());
		ArrayNode errors2 = (ArrayNode) responseBody2.get("errorRecords");
		assertEquals(1, errors2.size());
		assertEquals(2, errors2.get(0).get("reference").asLong());
		assertEquals("NL123", errors2.get(0).get("accountNumber").asText());
	}

	@Test
	public void submitIncorrectBalance() throws JsonProcessingException {
		String incorrectBalanceString = createJsonString(3, "NL123", 30, -5, "incorrect balance", 15);
		HttpEntity<String> request = new HttpEntity<>(incorrectBalanceString, HEADERS);
		ResponseEntity<String> response = REST_CLIENT.postForEntity(TRANSACTION_URL, request, String.class);
		assertEquals(200, response.getStatusCodeValue());

		String responseBodyString = response.getBody();
		JsonNode responseBody = OBJECT_MAPPER.readTree(responseBodyString);

		assertEquals("INCORRECT_END_BALANCE", responseBody.get("result").asText());
		ArrayNode errors = (ArrayNode) responseBody.get("errorRecords");
		assertEquals(1, errors.size());
		assertEquals(3, errors.get(0).get("reference").asLong());
		assertEquals("NL123", errors.get(0).get("accountNumber").asText());
	}

	@Test
	public void submitDuplicateAndIncorrectBalance() throws JsonProcessingException {
		String originalTransactionString = createJsonString(4, "NL123", 30, -5, "valid transaction", 25);
		HttpEntity<String> request = new HttpEntity<>(originalTransactionString, HEADERS);
		ResponseEntity<String> response = REST_CLIENT.postForEntity(TRANSACTION_URL, request, String.class);
		assertEquals(200, response.getStatusCodeValue());

		String responseBodyString = response.getBody();
		JsonNode responseBody = OBJECT_MAPPER.readTree(responseBodyString);

		assertEquals("SUCCESSFUL", responseBody.get("result").asText());
		ArrayNode errors = (ArrayNode) responseBody.get("errorRecords");
		assertEquals(0, errors.size());

//		Now submit it again
		String duplicateAndIncorrectBalanceTransactionString = createJsonString(4, "NL456", 30, -5, "duplicate and incorrect balance", 15);
		HttpEntity<String> request2 = new HttpEntity<>(duplicateAndIncorrectBalanceTransactionString, HEADERS);
		ResponseEntity<String> response2 = REST_CLIENT.postForEntity(TRANSACTION_URL, request2, String.class);
		assertEquals(200, response2.getStatusCodeValue());

		String responseBodyString2 = response2.getBody();
		JsonNode responseBody2 = OBJECT_MAPPER.readTree(responseBodyString2);

		assertEquals("DUPLICATE_REFERENCE _INCORRECT_END_BALANCE", responseBody2.get("result").asText());
		ArrayNode errors2 = (ArrayNode) responseBody2.get("errorRecords");
		assertEquals(2, errors2.size());
		assertEquals(4, errors2.get(0).get("reference").asLong());
		assertEquals("NL123", errors2.get(0).get("accountNumber").asText());
		assertEquals(4, errors2.get(1).get("reference").asLong());
		assertEquals("NL456", errors2.get(1).get("accountNumber").asText());
	}

	@Test
	public void submitIncorrectJson() throws JsonProcessingException {
		String incorrectJsonString = "{I am missing a closing brace";
		RestClientResponseException expectedResponse = null;

		try {
			HttpEntity<String> request = new HttpEntity<>(incorrectJsonString, HEADERS);
			ResponseEntity<String> response = REST_CLIENT.postForEntity(TRANSACTION_URL, request, String.class);
		} catch (RestClientResponseException ex) {
			expectedResponse = ex;
		}
		assertNotNull(expectedResponse);
		assertEquals(400, expectedResponse.getRawStatusCode());

		String responseBodyString = expectedResponse.getResponseBodyAsString();
		JsonNode responseBody = OBJECT_MAPPER.readTree(responseBodyString);

		assertEquals("BAD_REQUEST", responseBody.get("result").asText());
		ArrayNode errors = (ArrayNode) responseBody.get("errorRecords");
		assertEquals(0, errors.size());
	}

	private String createJsonString(long transactionReference, String accountNumber, long startBalance,
									long mutation, String description, long endBalance) {
		ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
		objectNode.put("transactionReference", transactionReference);
		objectNode.put("accountNumber", accountNumber);
		objectNode.put("startBalance", startBalance);
		objectNode.put("mutation", mutation);
		objectNode.put("description", description);
		objectNode.put("endBalance", endBalance);
		return objectNode.toString();
	}
}
