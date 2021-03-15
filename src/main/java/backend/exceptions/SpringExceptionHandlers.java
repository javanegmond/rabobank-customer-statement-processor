package backend.exceptions;

import backend.transaction.TransactionResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class SpringExceptionHandlers {

	//	This doesn't get executed, even when the method in the controller is commented out
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(JsonProcessingException.class)
	public TransactionResponse handleJsonProcessingException(JsonProcessingException ex) {
		System.out.println("###########################");
		TransactionResponse transactionResponse = new TransactionResponse();
		transactionResponse.setResult("BAD_REQUEST");
		return transactionResponse;
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(RuntimeException.class)
	public TransactionResponse handleTransactionResponse(RuntimeException ex) {
		TransactionResponse transactionResponse = new TransactionResponse();
		transactionResponse.setResult("INTERNAL_SERVER_ERROR");
		return transactionResponse;
	}
}
