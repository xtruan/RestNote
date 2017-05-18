package com.xtruan.restnote;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final AtomicInteger counter = new AtomicInteger();
    Map<Integer, Note> notes = Collections.synchronizedMap(new HashMap<>());

    @RequestMapping(method = RequestMethod.GET)
    public Collection<Note> listNotes() {
        return notes.values();
    }

    @RequestMapping(method = RequestMethod.POST)
    public void createNote(@RequestBody String noteBody) {
        final int noteId = counter.incrementAndGet();
        notes.put(noteId, new Note(noteId, noteBody));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{noteId}")
    public Note getNote(@PathVariable Integer noteId) {
        return notes.get(noteId);
    }
}
