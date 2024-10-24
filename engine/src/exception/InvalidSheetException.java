package exception;

public class InvalidSheetException extends RuntimeException {

    // Constructor with a custom error message and a cause
    public InvalidSheetException(String message) {
        super(message);
    }

}
