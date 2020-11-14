package vsu.course2.game.exceptions;

public class MovementWhileAttackCanBeCarriedOutException extends GameProcessException {
    public MovementWhileAttackCanBeCarriedOutException() {
        super();
    }

    public MovementWhileAttackCanBeCarriedOutException(String message) {
        super(message);
    }

    public MovementWhileAttackCanBeCarriedOutException(String message, Throwable cause) {
        super(message, cause);
    }

    public MovementWhileAttackCanBeCarriedOutException(Throwable cause) {
        super(cause);
    }

    protected MovementWhileAttackCanBeCarriedOutException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
