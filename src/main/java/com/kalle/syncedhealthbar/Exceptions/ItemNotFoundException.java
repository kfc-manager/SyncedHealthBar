package com.kalle.syncedhealthbar.Exceptions;

/**
 * An exception that is used when an object in the plugin is not found.
 */
public class ItemNotFoundException extends Exception {

    /**
     * A class constructor to pass the error message.
     * @param errorMessage error message which contains information about what went wrong
     */
    public ItemNotFoundException(String errorMessage) {
        super(errorMessage);
    }

}
