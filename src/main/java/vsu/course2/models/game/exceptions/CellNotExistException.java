package vsu.course2.models.game.exceptions;

public class CellNotExistException extends GameProcessException {
    public CellNotExistException() {
        super();
    }

    public CellNotExistException(String message) {
        super(message);
    }

    public CellNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public CellNotExistException(Throwable cause) {
        super(cause);
    }

    protected CellNotExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
