package backend.transaction.model;

public class ErrorRecord {

    private final long reference;

    private final String accountNumber;

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
