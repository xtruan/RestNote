package com.xtruan.restnote;

public class Note {

    private final int id;
    private final String body;

    public Note(int id, String content) {
        this.id = id;
        this.body = content;
    }

    public int getId() {
        return id;
    }
    public String getBody() {
        return body;
    }
}
