package exception;

import java.io.IOException;

public class TaskServerStartException extends RuntimeException {
    public TaskServerStartException(String msg, IOException e) {
        super(msg, e);
    }
}
