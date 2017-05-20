package com.xtruan.restnote.model;

/**
 * Model for Note objects
 */
public class Note implements IModel {

    /**
     * Note numeric id
     */
    private final int id;

    /**
     * Note body text
     */
    private final String body;

    /**
     * Constructor
     * 
     * @param id
     * @param body
     */
    public Note(final int id, final String body) {
        this.id = id;
        this.body = body;
    }

    /**
     * Getter for Note id
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Getter for Note body
     * @return body
     */
    public String getBody() {
        return body;
    }
}
