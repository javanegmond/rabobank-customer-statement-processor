package backend.transaction;

import backend.transaction.model.TransactionRequest;
import backend.transaction.model.TransactionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    public static final String REFERENCE_KEY = "reference";

    public static final String ACCOUNT_NUMBER_KEY = "accountNumber";

    private final TransactionRepository transactionRepository;

    @Autowired
    private TransactionValidator transactionValidator;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public TransactionResponse save(TransactionRequest transactionToSave) {
        TransactionResponse response;

        transactionValidator.validateTransaction(transactionToSave);

        transactionRepository.saveTransaction(transactionToSave);
        response = new TransactionResponse();
        response.setResult("SUCCESSFUL");
        return response;
    }

}
