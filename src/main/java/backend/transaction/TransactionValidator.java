package backend.transaction;

import java.util.EnumMap;
import java.util.Optional;

public class TransactionValidator {

	public static EnumMap<TransactionError, ErrorRecord> validateTransaction(TransactionRequest transactionToValidate, TransactionRepository transactionRepository) {
		EnumMap<TransactionError, ErrorRecord> errors = new EnumMap<>(TransactionError.class);

		long reference = transactionToValidate.getTransactionReference();
		validateReferenceUnused(reference, transactionRepository).ifPresent(errorRecord -> {
			errors.put(TransactionError.DUPLICATE_REFERENCE, errorRecord);
		});

		validateBalanceCorrect(transactionToValidate).ifPresent(errorRecord -> {
			errors.put(TransactionError.INCORRECT_END_BALANCE, errorRecord);
		});

		return errors;
	}

	public static Optional<ErrorRecord> validateReferenceUnused(long reference, TransactionRepository transactionRepository) {
		ErrorRecord error = null;
		if (transactionRepository.isTransactionReferenceUsed(reference)) {
			TransactionRequest duplicateTransaction = transactionRepository.getTransaction(reference).orElseThrow();
			error = new ErrorRecord(duplicateTransaction.getTransactionReference(), duplicateTransaction.getAccountNumber());
		}
		return Optional.ofNullable(error);
	}

	public static Optional<ErrorRecord> validateBalanceCorrect(TransactionRequest transactionToValidate) {
		ErrorRecord error = null;
		if (!TransactionService.isBalanceCorrect(transactionToValidate.getStartBalance(), transactionToValidate.getEndBalance(), transactionToValidate.getMutation())) {
			error = new ErrorRecord(transactionToValidate.getTransactionReference(), transactionToValidate.getAccountNumber());
		}
		return Optional.ofNullable(error);
	}
}
