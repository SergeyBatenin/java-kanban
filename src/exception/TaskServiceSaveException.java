package exception;

public class TaskServiceSaveException extends RuntimeException {
    public TaskServiceSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}
