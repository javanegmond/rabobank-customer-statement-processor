package backend.transaction.exception;

public class NonUniqueIDException
        extends RuntimeException {

    public NonUniqueIDException(String message) {
        super(message);
    }

}
