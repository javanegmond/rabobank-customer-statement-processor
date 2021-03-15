package backend.transaction;

import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

@Service
public class TransactionService {

	private TransactionRepository transactionRepository;

	public static final String REFERENCE_KEY = "reference";
	public static final String ACCOUNT_NUMBER_KEY = "accountNumber";

	public TransactionService(TransactionRepository transactionRepository) {
		this.transactionRepository = transactionRepository;
	}

	public TransactionResponse save(TransactionRequest transactionToSave) {
		TransactionResponse response;

		Map<TransactionError, Map<String, String>> errors = validateTransaction(transactionToSave);
		if (errors.size() > 0) {
			response = createErrorResponse(errors);
		} else {
			transactionRepository.saveTransaction(transactionToSave);
			response = new TransactionResponse();
			response.setResult("SUCCESSFUL");
		}
		return response;
	}

	public Map<TransactionError, Map<String, String>> validateTransaction(TransactionRequest transactionToValidate) {
		Map<TransactionError, Map<String, String>> errors = new EnumMap<>(TransactionError.class);

		long reference = transactionToValidate.getTransactionReference();
		if (transactionRepository.isTransactionReferenceUsed(reference)) {
			TransactionRequest duplicateTransaction = transactionRepository.getTransaction(reference).orElseThrow();
			Map<String, String> parametersDuplicateTransaction = new HashMap<>();
			parametersDuplicateTransaction.put(REFERENCE_KEY, Long.toString(duplicateTransaction.getTransactionReference()));
			parametersDuplicateTransaction.put(ACCOUNT_NUMBER_KEY, duplicateTransaction.getAccountNumber());

			errors.put(TransactionError.DUPLICATE_REFERENCE, parametersDuplicateTransaction);
		}
		if (!isBalanceCorrect(transactionToValidate.getStartBalance(), transactionToValidate.getEndBalance(), transactionToValidate.getMutation())) {
			Map<String, String> parametersIncorrectBalanceTransaction = new HashMap<>();
			parametersIncorrectBalanceTransaction.put(REFERENCE_KEY, Long.toString(transactionToValidate.getTransactionReference()));
			parametersIncorrectBalanceTransaction.put(ACCOUNT_NUMBER_KEY, transactionToValidate.getAccountNumber());

			errors.put(TransactionError.INCORRECT_END_BALANCE, parametersIncorrectBalanceTransaction);
		}

		return errors;
	}

	public static boolean isBalanceCorrect(long startBalance, long endBalance, long mutation) {
		try {
			return endBalance == Math.addExact(startBalance, mutation);
		} catch (ArithmeticException ex) {
			return false;
		}
	}

	public static TransactionResponse createErrorResponse(Map<TransactionError, Map<String, String>> errors) {
		TransactionResponse result = new TransactionResponse();
		if (errors.containsKey(TransactionError.DUPLICATE_REFERENCE)) {
			result.setResult(TransactionError.DUPLICATE_REFERENCE.toString());
			Map<String, String> parameters = errors.get(TransactionError.DUPLICATE_REFERENCE);
			result.addError(Long.parseLong(parameters.get(REFERENCE_KEY)), parameters.get(ACCOUNT_NUMBER_KEY));
		}
		if (errors.containsKey(TransactionError.INCORRECT_END_BALANCE)) {
			String oldResultString = result.getResult();
			String newResultString = concatenateResultStrings(oldResultString, TransactionError.INCORRECT_END_BALANCE.toString());
			result.setResult(newResultString);
			Map<String, String> parameters = errors.get(TransactionError.INCORRECT_END_BALANCE);
			result.addError(Long.parseLong(parameters.get(REFERENCE_KEY)), parameters.get(ACCOUNT_NUMBER_KEY));
		}
		return result;
	}

	public static String concatenateResultStrings(String oldResult, String resultToAdd) {
		if (oldResult == null || oldResult.isEmpty()) {
			return resultToAdd;
		}

		return String.join(" _", oldResult, resultToAdd);
	}
}
