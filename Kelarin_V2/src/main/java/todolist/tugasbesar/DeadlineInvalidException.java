public class DeadlineInvalidException extends Exception {
    
    public DeadlineInvalidException(String message) {
        super(message);
    }
    
    public DeadlineInvalidException(String message, Throwable cause) {
        super(message, cause);
    }
}