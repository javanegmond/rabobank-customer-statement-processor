package backend;

import backend.transaction.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestTransaction {

	static TransactionRequest CORRECT_TRANSACTION;
	static TransactionRequest INCORRECT_TRANSACTION;
	static TransactionRequest DUPLICATE_TRANSACTION;
	static TransactionRequest DUPLICATE_INCORRECT_TRANSACTION;

	@BeforeAll
	public static void createTransactions() {
		CORRECT_TRANSACTION = new TransactionRequest(1, "ab12", 10, 20, "", 30);
		INCORRECT_TRANSACTION = new TransactionRequest(2, "cd34", 10, 20, "", 40);
		DUPLICATE_TRANSACTION = new TransactionRequest(1, "fg56", 10, 20, "", 30);
		DUPLICATE_INCORRECT_TRANSACTION = new TransactionRequest(1, "hi78", 10, 20, "", 40);
	}

	@Test
	public void testIsBalanceCorrect() {
		assertTrue(TransactionService.isBalanceCorrect(10, 30, 20));
		assertTrue(TransactionService.isBalanceCorrect(10, -10, -20));

		assertTrue(TransactionService.isBalanceCorrect(0, 20, 20));
		assertTrue(TransactionService.isBalanceCorrect(20, 20, 0));
		assertTrue(TransactionService.isBalanceCorrect(20, 0, -20));

//		this causes an integer overflow so the result should be false
		assertFalse(TransactionService.isBalanceCorrect(Long.MAX_VALUE, Long.MAX_VALUE + Long.MAX_VALUE, Long.MAX_VALUE));
	}

	@Test
	public void testConcatenateResultStrings() {
		assertEquals("a _b", TransactionService.concatenateResultStrings("a", "b"));
		assertEquals("b", TransactionService.concatenateResultStrings(null, "b"));
		assertEquals("b", TransactionService.concatenateResultStrings("", "b"));
	}

	@Test
	public void testValidateBalanceCorrect() {
		Optional<ErrorRecord> correctResult = TransactionValidator.validateBalanceCorrect(CORRECT_TRANSACTION);
		assertTrue(correctResult.isEmpty());

		Optional<ErrorRecord> incorrectResult = TransactionValidator.validateBalanceCorrect(INCORRECT_TRANSACTION);
		assertTrue(incorrectResult.isPresent());
		assertEquals(INCORRECT_TRANSACTION.getAccountNumber(), incorrectResult.get().getAccountNumber());
		assertEquals(INCORRECT_TRANSACTION.getTransactionReference(), incorrectResult.get().getReference());
	}

	@Test
	public void testValidateReferenceUnused() {
		TransactionRepository mockRepository = mock(TransactionRepository.class);

		when(mockRepository.isTransactionReferenceUsed(CORRECT_TRANSACTION.getTransactionReference())).thenReturn(false);
		Optional<ErrorRecord> correctResult = TransactionValidator.validateReferenceUnused(CORRECT_TRANSACTION.getTransactionReference(), mockRepository);
		assertTrue(correctResult.isEmpty());

		when(mockRepository.isTransactionReferenceUsed(CORRECT_TRANSACTION.getTransactionReference())).thenReturn(true);
		when(mockRepository.getTransaction(CORRECT_TRANSACTION.getTransactionReference())).thenReturn(Optional.of(CORRECT_TRANSACTION));
		Optional<ErrorRecord> duplicateResult = TransactionValidator.validateReferenceUnused(DUPLICATE_TRANSACTION.getTransactionReference(), mockRepository);
		assertTrue(duplicateResult.isPresent());
		assertEquals(CORRECT_TRANSACTION.getAccountNumber(), duplicateResult.get().getAccountNumber());
		assertEquals(CORRECT_TRANSACTION.getTransactionReference(), duplicateResult.get().getReference());
	}

	@Test
	public void testValidateTransaction() {
		TransactionRepository mockRepository = mock(TransactionRepository.class);

		when(mockRepository.isTransactionReferenceUsed(CORRECT_TRANSACTION.getTransactionReference())).thenReturn(true);
		when(mockRepository.getTransaction(CORRECT_TRANSACTION.getTransactionReference())).thenReturn(Optional.of(CORRECT_TRANSACTION));

		Map<TransactionError, ErrorRecord> result = TransactionValidator.validateTransaction(DUPLICATE_INCORRECT_TRANSACTION, mockRepository);
		assertEquals(2, result.size());

		assertTrue(result.containsKey(TransactionError.INCORRECT_END_BALANCE));
		ErrorRecord recordIncorrectBalance = result.get(TransactionError.INCORRECT_END_BALANCE);
		assertEquals(DUPLICATE_INCORRECT_TRANSACTION.getTransactionReference(), recordIncorrectBalance.getReference());
		assertEquals(DUPLICATE_INCORRECT_TRANSACTION.getAccountNumber(), recordIncorrectBalance.getAccountNumber());

		assertTrue(result.containsKey(TransactionError.DUPLICATE_REFERENCE));
		ErrorRecord recordDuplicateBalance = result.get(TransactionError.DUPLICATE_REFERENCE);
		assertEquals(CORRECT_TRANSACTION.getTransactionReference(), recordDuplicateBalance.getReference());
		assertEquals(CORRECT_TRANSACTION.getAccountNumber(), recordDuplicateBalance.getAccountNumber());
	}
}
