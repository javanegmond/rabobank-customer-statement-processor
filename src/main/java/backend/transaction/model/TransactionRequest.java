package backend.transaction.model;

public class TransactionRequest {

    //	We only accept integer values that fit in 64 bits.
    private long transactionReference;

    //	we are not checking if this is indeed a valid IBAN, some libraries do exist for this
//	see e.g. https://commons.apache.org/proper/commons-validator/apidocs/org/apache/commons/validator/routines/IBANValidator.html
    private String accountNumber;

    //	We only accept integer values now that fit in 64 bits. In more serious situations one should also be able to allow
//	for decimals
    private long startBalance;

    private long mutation;

    private String description;

    private long endBalance;

    public long getTransactionReference() {
        return transactionReference;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public long getStartBalance() {
        return startBalance;
    }

    public long getMutation() {
        return mutation;
    }

    public String getDescription() {
        return description;
    }

    public long getEndBalance() {
        return endBalance;
    }

}
