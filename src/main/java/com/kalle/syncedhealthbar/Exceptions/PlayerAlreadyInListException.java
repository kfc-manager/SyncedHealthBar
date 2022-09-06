package com.kalle.syncedhealthbar.Exceptions;

/**
 * An exception that is used when a player is already assigned to a health bar.
 */
public class PlayerAlreadyInListException extends Exception {

    /**
     * A class constructor to pass the error message.
     * @param errorMessage error message which contains information about what went wrong
     */
    public PlayerAlreadyInListException(String errorMessage) {
        super(errorMessage);
    }

}
