package dev.splityosis.sysengine.guilib.exceptions;

public class UnsupportedPaneOperationException extends RuntimeException {

    public UnsupportedPaneOperationException() {
    }

    public UnsupportedPaneOperationException(String message) {
        super(message);
    }

    public UnsupportedPaneOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedPaneOperationException(Throwable cause) {
        super(cause);
    }

    public UnsupportedPaneOperationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
