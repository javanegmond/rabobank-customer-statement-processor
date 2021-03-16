package backend.exceptions;

public class NonUniqueIDException
        extends RuntimeException {

    public NonUniqueIDException(String message) {
        super(message);
    }

}
