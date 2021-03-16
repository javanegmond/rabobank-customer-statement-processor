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
		if (!errors.isEmpty()) {
			response = createErrorResponse(errors);
		} else {
			transactionRepository.saveTransaction(transactionToSave);
			response = new TransactionResponse();
			response.setResult("SUCCESSFUL");
		}
		return response;
	}

	public EnumMap<TransactionError, Map<String, String>> validateTransaction(TransactionRequest transactionToValidate) {
		EnumMap<TransactionError, Map<String, String>> errors = new EnumMap<>(TransactionError.class);

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
		TransactionResponse errorResponse = new TransactionResponse();

		errors.forEach((errorCode, parameters) -> {
			String oldResultString = errorResponse.getResult();
			String newResultString = concatenateResultStrings(oldResultString, errorCode.toString());
			errorResponse.setResult(newResultString);
			errorResponse.addError(Long.parseLong(parameters.get(REFERENCE_KEY)), parameters.get(ACCOUNT_NUMBER_KEY));
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
