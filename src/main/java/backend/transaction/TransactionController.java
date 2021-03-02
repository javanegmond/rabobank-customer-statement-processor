package backend.transaction;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/transaction")
public class TransactionController {

	@Autowired
	private TransactionService transactionService;

	@PostMapping
	public ResponseEntity<TransactionResponse> saveTransaction(@RequestBody String body) {

		TransactionRequest transactionRequest;
		ResponseEntity<TransactionResponse> response;
		try {
			transactionRequest = jsonToTransactionRequest(body);
			TransactionResponse responseBody = transactionService.save(transactionRequest);
			response = new ResponseEntity<>(responseBody, HttpStatus.OK);
//			why are we returning 200 on errors, it should be 4xx
		} catch (RuntimeException ex) {
			TransactionResponse responseBody = new TransactionResponse();
			responseBody.setResult("INTERNAL_SERVER_ERROR");
			response = new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (JsonProcessingException ex) {
			TransactionResponse responseBody = new TransactionResponse();
			responseBody.setResult("BAD_REQUEST");
			response = new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
		}

		return response;
	}

	//	We need this in order to give the correct response when there is incorrect json,
	//	although a @ControllerAdvice could have been used instead
	private TransactionRequest jsonToTransactionRequest(String json) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(json, TransactionRequest.class);
	}
}
