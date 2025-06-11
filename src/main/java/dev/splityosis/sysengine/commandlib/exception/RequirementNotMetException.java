package dev.splityosis.sysengine.commandlib.exception;

/**
 * This exception is thrown when a command requirement is not met.
 * It serves as a generic indication that a specific requirement was not fulfilled.
 */
public class RequirementNotMetException extends Exception {

    /**
     * Constructs a new {@code RequirementNotMetException} with a default message.
     * The default message indicates that a command requirement was not met.
     * If this exception is triggered, it suggests an unmet requirement for the command to proceed.
     *
     * @param failReason an internal communication path between the isMet() and onNotMet() methods,
     *                   this can be used to provide more specific feedback or context for the failure.
     */
    public RequirementNotMetException(String failReason) {
        super(failReason);
    }

    /**
     * Constructs a new {@code RequirementNotMetException} with a default message.
     * The default message indicates that a command requirement was not met.
     * If this exception is triggered, it suggests an unmet requirement for the command to proceed.
     */
    public RequirementNotMetException() {

    }

    /**
     * Returns the reason the requirement was not met.
     *
     * @return a string representing the reason for failure.
     */
    public String getFailReason() {
        return getMessage();
    }

//    /**
//     * Sets the reason the requirement was not met.
//     *
//     * @param failReason a string representing the reason for failure.
//     */
//    public void setFailReason(String failReason) {
//
//    }
}
