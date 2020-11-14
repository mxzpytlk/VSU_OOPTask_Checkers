package vsu.course2.graph;

public class GraphException extends Exception {
    public GraphException() {
        super();
    }

    public GraphException(String message) {
        super(message);
    }

    public GraphException(String message, Throwable cause) {
        super(message, cause);
    }

    public GraphException(Throwable cause) {
        super(cause);
    }

    protected GraphException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
