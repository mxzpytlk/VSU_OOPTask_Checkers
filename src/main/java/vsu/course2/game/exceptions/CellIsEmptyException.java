package vsu.course2.game.exceptions;

public class CellIsEmptyException extends GameProcessException {
    public CellIsEmptyException() {
        super();
    }

    public CellIsEmptyException(String message) {
        super(message);
    }

    public CellIsEmptyException(String message, Throwable cause) {
        super(message, cause);
    }

    public CellIsEmptyException(Throwable cause) {
        super(cause);
    }

    protected CellIsEmptyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
