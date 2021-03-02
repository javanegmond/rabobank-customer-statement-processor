package backend.transaction;

public class ErrorRecord {

	private long reference;
	private String accountNumber;

	public ErrorRecord(long reference, String accountNumber) {
		this.reference = reference;
		this.accountNumber = accountNumber;
	}

	public long getReference() {
		return reference;
	}

	public String getAccountNumber() {
		return accountNumber;
	}
}
