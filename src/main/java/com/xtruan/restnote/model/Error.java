package com.xtruan.restnote.model;

/**
 * Model for Error objects
 */
public class Error implements IModel {

    /**
     * HTTP error status
     */
    private final int status;

    /**
     * Error text
     */
    private final String error;

    /**
     * Constructor
     *
     * @param status
     * @param error
     */
    public Error(final int status, final String error) {
        this.status = status;
        this.error = error;
    }

    /**
     * Getter for HTTP error status
     * @return status
     */
    public int getStatus() {
        return status;
    }

    /**
     * Getter for Error text
     * @return error
     */
    public String getError() {
        return error;
    }
}
