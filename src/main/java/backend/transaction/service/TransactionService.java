package backend.transaction.service;

import backend.transaction.model.TransactionRequest;
import backend.transaction.model.TransactionResponse;
import backend.transaction.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

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
