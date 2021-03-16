package backend.transaction.controller;

import backend.transaction.exception.InvalidTransactionException;
import backend.transaction.model.ErrorRecord;
import backend.transaction.TransactionError;
import backend.transaction.model.TransactionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class TransactionExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public TransactionResponse handleJsonProcessingException(HttpMessageNotReadableException ex) {
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

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(InvalidTransactionException.class)
    public TransactionResponse handleInvalidTransaction(InvalidTransactionException ex) {
        var errors = ex.getErrors();
        return createErrorResponse(errors);
    }

    public static TransactionResponse createErrorResponse(Map<TransactionError, ErrorRecord> errors) {
        TransactionResponse errorResponse = new TransactionResponse();

        var errorCode = errors.keySet().stream()
                              .map(TransactionError::name)
                              .collect(Collectors.joining(" _"));
        errorResponse.setResult(errorCode);
        errors.values().forEach(errorResponse::addError);

        return errorResponse;
    }

}
