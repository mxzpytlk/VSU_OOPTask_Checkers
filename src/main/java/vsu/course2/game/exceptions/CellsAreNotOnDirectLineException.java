package vsu.course2.game.exceptions;

public class CellsAreNotOnDirectLineException extends GameProcessException{
    public CellsAreNotOnDirectLineException() {
        super();
    }

    public CellsAreNotOnDirectLineException(String message) {
        super(message);
    }

    public CellsAreNotOnDirectLineException(String message, Throwable cause) {
        super(message, cause);
    }

    public CellsAreNotOnDirectLineException(Throwable cause) {
        super(cause);
    }

    protected CellsAreNotOnDirectLineException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
