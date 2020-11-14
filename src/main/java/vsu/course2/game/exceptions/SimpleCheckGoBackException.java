package vsu.course2.game.exceptions;

public class SimpleCheckGoBackException extends GameProcessException{
    public SimpleCheckGoBackException() {
        super();
    }

    public SimpleCheckGoBackException(String message) {
        super(message);
    }

    public SimpleCheckGoBackException(String message, Throwable cause) {
        super(message, cause);
    }

    public SimpleCheckGoBackException(Throwable cause) {
        super(cause);
    }

    protected SimpleCheckGoBackException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
