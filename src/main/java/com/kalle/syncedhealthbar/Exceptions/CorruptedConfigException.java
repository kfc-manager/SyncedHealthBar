package com.kalle.syncedhealthbar.Exceptions;

/**
 * An exception that is used when an error occurs with the plugin's config.yml.
 */
public class CorruptedConfigException extends Exception{

    /**
     * A class constructor to pass the error message.
     * @param errorMessage error message which contains information about what went wrong
     */
    public CorruptedConfigException(String errorMessage) {
        super(errorMessage);
    }

}
