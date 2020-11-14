package vsu.course2.game.exceptions;

public class CellIsNotFreeException extends GameProcessException {
    public CellIsNotFreeException() {
        super();
    }

    public CellIsNotFreeException(String message) {
        super(message);
    }

    public CellIsNotFreeException(String message, Throwable cause) {
        super(message, cause);
    }

    public CellIsNotFreeException(Throwable cause) {
        super(cause);
    }

    protected CellIsNotFreeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
