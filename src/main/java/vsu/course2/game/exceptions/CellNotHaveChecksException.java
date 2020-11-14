package vsu.course2.game.exceptions;

public class CellNotHaveChecksException extends GameProcessException {
    public CellNotHaveChecksException() {
        super();
    }

    public CellNotHaveChecksException(String message) {
        super(message);
    }

    public CellNotHaveChecksException(String message, Throwable cause) {
        super(message, cause);
    }

    public CellNotHaveChecksException(Throwable cause) {
        super(cause);
    }

    protected CellNotHaveChecksException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
