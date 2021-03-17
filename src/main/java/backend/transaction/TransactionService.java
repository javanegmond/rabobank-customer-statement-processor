package backend.transaction;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TransactionService {

	private TransactionRepository transactionRepository;


	public TransactionService(TransactionRepository transactionRepository) {
		this.transactionRepository = transactionRepository;
	}

	public TransactionResponse save(TransactionRequest transactionToSave) {
		TransactionResponse response;

		Map<TransactionError, ErrorRecord> errors = TransactionValidator.validateTransaction(transactionToSave, transactionRepository);
		if (!errors.isEmpty()) {
			response = createErrorResponse(errors);
		} else {
			transactionRepository.saveTransaction(transactionToSave);
			response = new TransactionResponse();
			response.setResult("SUCCESSFUL");
		}
		return response;
	}

	public static boolean isBalanceCorrect(long startBalance, long endBalance, long mutation) {
		try {
			return endBalance == Math.addExact(startBalance, mutation);
		} catch (ArithmeticException ex) {
			return false;
		}
	}

	public static TransactionResponse createErrorResponse(Map<TransactionError, ErrorRecord> errors) {
		TransactionResponse errorResponse = new TransactionResponse();

		errors.forEach((errorCode, parameters) -> {
			String oldResultString = errorResponse.getResult();
			String newResultString = concatenateResultStrings(oldResultString, errorCode.toString());
			errorResponse.setResult(newResultString);
			errorResponse.addError(parameters);
		});

		return errorResponse;
	}

	public static String concatenateResultStrings(String oldResult, String resultToAdd) {
		if (oldResult == null || oldResult.isEmpty()) {
			return resultToAdd;
		}

		return String.join(" _", oldResult, resultToAdd);
	}
}
