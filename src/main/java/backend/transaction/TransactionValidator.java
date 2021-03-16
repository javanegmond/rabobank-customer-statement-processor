package backend.transaction;

import backend.transaction.exception.InvalidTransactionException;
import backend.transaction.model.ErrorRecord;
import backend.transaction.model.TransactionRequest;
import backend.transaction.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import javax.annotation.PostConstruct;
import java.util.EnumMap;
import java.util.Map;

@Component
@RequestScope
public class TransactionValidator {

    @Autowired
    private TransactionRepository transactionRepository;

    private Map<TransactionError, ErrorRecord> errors;

    @PostConstruct
    private void initErrorMap() {
        errors = new EnumMap<>(TransactionError.class);
    }

    public void validateTransaction(TransactionRequest transactionToValidate) {

        validateUniqueReference(transactionToValidate);
        validateBalanceAfterTransaction(transactionToValidate);

        if (!errors.isEmpty()) {
            throw new InvalidTransactionException(errors);
        }
    }

    private void validateBalanceAfterTransaction(TransactionRequest transactionToValidate) {
        if (!isBalanceCorrect(transactionToValidate.getStartBalance(), transactionToValidate.getEndBalance(), transactionToValidate.getMutation())) {
            var errorRecord = errorRecordFrom(transactionToValidate);

            errors.put(TransactionError.INCORRECT_END_BALANCE, errorRecord);
        }
    }

    private void validateUniqueReference(TransactionRequest transactionToValidate) {
        long reference = transactionToValidate.getTransactionReference();
        if (transactionRepository.isTransactionReferenceUsed(reference)) {
            TransactionRequest duplicateTransaction = transactionRepository.getTransaction(reference).orElseThrow();
            var errorRecord = errorRecordFrom(duplicateTransaction);

            errors.put(TransactionError.DUPLICATE_REFERENCE, errorRecord);
        }
    }

    private ErrorRecord errorRecordFrom(TransactionRequest transaction) {
        return new ErrorRecord(transaction.getTransactionReference(), transaction.getAccountNumber());
    }

    public static boolean isBalanceCorrect(long startBalance, long endBalance, long mutation) {
        try {
            return endBalance == Math.addExact(startBalance, mutation);
        } catch (ArithmeticException ex) {
            return false;
        }
    }

}
