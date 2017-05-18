package com.xtruan.restnote;

/**
 * Model for Note objects
 */
public class Note {

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
     * @param content
     */
    public Note(final int id, final String content) {
        this.id = id;
        this.body = content;
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
