package backend.transaction.exception;

import backend.transaction.model.ErrorRecord;
import backend.transaction.TransactionError;

import java.util.Map;

public class InvalidTransactionException
        extends RuntimeException {

    private final Map<TransactionError, ErrorRecord> errors;

    public InvalidTransactionException(Map<TransactionError, ErrorRecord> errors) {
        this.errors = errors;
    }

    public Map<TransactionError, ErrorRecord> getErrors() {
        return errors;
    }

}
