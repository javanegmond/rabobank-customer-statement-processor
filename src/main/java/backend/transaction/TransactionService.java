package backend.transaction;

import org.springframework.stereotype.Service;

@Service
public class TransactionService {

	private TransactionRepository transactionRepository;

	private static final String DUPLICATE_REFERENCE_RESULT = "DUPLICATE_REFERENCE";
	private static final String INCORRECT_BALANCE_RESULT = "INCORRECT_END_BALANCE";
	//	is there supposed to be a space in this string?
	private static final String DUPLICATE_REFERENCE_INCORRECT_BALANCE_RESULT = "DUPLICATE_REFERENCE _INCORRECT_END_BALANCE";

	public TransactionService(TransactionRepository transactionRepository) {
		this.transactionRepository = transactionRepository;
	}

	public TransactionResponse save(TransactionRequest transactionToSave) {
		TransactionResponse response = new TransactionResponse();
		boolean anyError = false;

		long reference = transactionToSave.getTransactionReference();

		if (transactionRepository.isTransactionReferenceUsed(reference)) {
			TransactionRequest duplicateTransaction = transactionRepository.getTransaction(reference).orElseThrow();
			response.addError(duplicateTransaction.getTransactionReference(), duplicateTransaction.getAccountNumber());
			response.setResult(DUPLICATE_REFERENCE_RESULT);
			anyError = true;
		}

		if (!isBalanceCorrect(transactionToSave.getStartBalance(), transactionToSave.getEndBalance(), transactionToSave.getMutation())) {
			response.addError(reference, transactionToSave.getAccountNumber());
			anyError = true;
			if (response.getResult() != null) {
				response.setResult(DUPLICATE_REFERENCE_INCORRECT_BALANCE_RESULT);
			} else {
				response.setResult(INCORRECT_BALANCE_RESULT);
			}
		}

		if (!anyError) {
			transactionRepository.saveTransaction(transactionToSave);
			response.setResult("SUCCESSFUL");
		}
		return response;
	}

	private boolean isBalanceCorrect(long startBalance, long endBalance, long mutation) {
		try {
			return endBalance == Math.addExact(startBalance, mutation);
		} catch (ArithmeticException ex) {
			return false;
		}
	}
}
