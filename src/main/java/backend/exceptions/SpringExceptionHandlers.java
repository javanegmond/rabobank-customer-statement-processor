package backend.exceptions;

import backend.transaction.TransactionResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class SpringExceptionHandlers {

	@ResponseBody
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public TransactionResponse handleHttpMessageNotReadableException() {
		TransactionResponse transactionResponse = new TransactionResponse();
		transactionResponse.setResult("BAD_REQUEST");
		return transactionResponse;
	}

	@ResponseBody
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(RuntimeException.class)
	public TransactionResponse handleTransactionResponse(RuntimeException ex) {
		ex.printStackTrace();
		TransactionResponse transactionResponse = new TransactionResponse();
		transactionResponse.setResult("INTERNAL_SERVER_ERROR");
		return transactionResponse;
	}
}
