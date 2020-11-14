package vsu.course2.game.exceptions;

public class PlayerNotHaveCheckException extends GameProcessException{
    public PlayerNotHaveCheckException() {
        super();
    }

    public PlayerNotHaveCheckException(String message) {
        super(message);
    }

    public PlayerNotHaveCheckException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlayerNotHaveCheckException(Throwable cause) {
        super(cause);
    }

    protected PlayerNotHaveCheckException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
