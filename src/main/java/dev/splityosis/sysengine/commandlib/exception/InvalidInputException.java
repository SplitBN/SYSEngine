package dev.splityosis.sysengine.commandlib.exception;

/**
 * This exception is thrown when a command argument fails to parse due to invalid input.
 * It serves as a generic indication that an argument was not provided in the expected format.
 */
public class InvalidInputException extends Exception {

    private String failReason;

    /**
     * Constructs a new {@code InvalidInputException} with a default message.
     * The default message indicates that invalid input was encountered during command parsing.
     * If this exception is triggered, it suggests an issue that should be addressed by the developer.
     *
     * @param failReason an internal communication path between the parse() and the onInvalidInput() methods, this can be used to transfer data across to minimize checks.
     */
    public InvalidInputException(String failReason) {
        super("Invalid argument input! You should never see this message, let the developer know.");
        this.failReason = failReason;
    }

    /**
     * Constructs a new {@code InvalidInputException} with a default message.
     * The default message indicates that invalid input was encountered during command parsing.
     * If this exception is triggered, it suggests an issue that should be addressed by the developer.
     */
    public InvalidInputException() {
        super("Invalid argument input! You should never see this message, let the developer know.");
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }
}
