package backend.transaction;

import backend.transaction.model.TransactionRequest;

import java.util.Optional;

public interface TransactionRepository {

    boolean isTransactionReferenceUsed(long reference);

    void saveTransaction(TransactionRequest transactionRequest);

    Optional<TransactionRequest> getTransaction(long reference);

}
