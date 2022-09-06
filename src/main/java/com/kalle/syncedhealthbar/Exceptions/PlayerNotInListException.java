package com.kalle.syncedhealthbar.Exceptions;

/**
 * An exception that is used when a player is not assigned to any health bar.
 */
public class PlayerNotInListException extends Exception{

    /**
     * A class constructor to pass the error message.
     * @param errorMessage error message which contains information about what went wrong
     */
    public PlayerNotInListException(String errorMessage) {
        super(errorMessage);
    }

}
