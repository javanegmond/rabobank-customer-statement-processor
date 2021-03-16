package backend.transaction.repository;

import backend.transaction.exception.NonUniqueIDException;
import backend.transaction.model.TransactionRequest;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

//for the sake of this assignment, we don't use some persistent storage
@Repository
public class InMemoryTransactionRepository
        implements TransactionRepository {

    private static final Map<Long, TransactionRequest> references = new HashMap<>();

    @Override
    public boolean isTransactionReferenceUsed(long reference) {
        return references.containsKey(reference);
    }

    @Override
    public void saveTransaction(TransactionRequest transactionRequest) {
        long reference = transactionRequest.getTransactionReference();
        if (isTransactionReferenceUsed(reference)) {
            throw new NonUniqueIDException("The transaction reference " + reference + " is already used");
        }

        references.put(reference, transactionRequest);
    }

    @Override
    public Optional<TransactionRequest> getTransaction(long reference) {
        return Optional.ofNullable(references.get(reference));
    }

}
