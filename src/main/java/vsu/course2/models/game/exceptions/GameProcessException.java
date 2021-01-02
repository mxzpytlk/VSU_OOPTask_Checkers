package vsu.course2.models.game.exceptions;

public class GameProcessException extends Exception {
    public GameProcessException() {
        super();
    }

    public GameProcessException(String message) {
        super(message);
    }

    public GameProcessException(String message, Throwable cause) {
        super(message, cause);
    }

    public GameProcessException(Throwable cause) {
        super(cause);
    }

    protected GameProcessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
