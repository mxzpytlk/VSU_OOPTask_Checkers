package vsu.course2.game.exceptions;

public class CellsAreEqualsException extends GameProcessException{
    public CellsAreEqualsException() {
        super();
    }

    public CellsAreEqualsException(String message) {
        super(message);
    }

    public CellsAreEqualsException(String message, Throwable cause) {
        super(message, cause);
    }

    public CellsAreEqualsException(Throwable cause) {
        super(cause);
    }

    protected CellsAreEqualsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
