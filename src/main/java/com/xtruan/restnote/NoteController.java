package com.xtruan.restnote;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final AtomicInteger counter = new AtomicInteger();
    private Map<Integer, Note> notes = Collections.synchronizedMap(new HashMap<>());
    private Map<String, Set<Integer>> index = Collections.synchronizedMap(new HashMap<>());

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody Collection<Note> listNotes(@RequestParam(value="query", required=false, defaultValue = "") final String query) {
        if (query.isEmpty()) {
            return notes.values();
        } else {
            Collection<Note> matchingNotes = new ArrayList<>();
            Set<Integer> matchingNoteIds = new HashSet<>();
            final String[] queryWords = splitAndProcessWords(query);
            for (final String queryWord : queryWords) {
                if (index.containsKey(queryWord)) {
                    matchingNoteIds.addAll(index.get(queryWord));
                }
            }

            for (Integer noteId : matchingNoteIds) {
                matchingNotes.add(notes.get(noteId));
            }

            return matchingNotes;
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody Note createNote(@RequestBody final String noteBody) {
        final int noteId = counter.incrementAndGet();
        final Note note = new Note(noteId, noteBody);
        notes.put(noteId, note);

        final String[] noteWords = splitAndProcessWords(noteBody);
        for (final String noteWord : noteWords) {
            if (index.containsKey(noteWord)) {
                index.get(noteWord).add(noteId);
            } else {
                Set<Integer> indexedNoteIds = new HashSet<>();
                indexedNoteIds.add(noteId);
                index.put(noteWord, indexedNoteIds);
            }
        }

        return note;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{noteId}")
    public @ResponseBody Note getNote(@PathVariable final Integer noteId) {
        return notes.get(noteId);
    }

    private String[] splitAndProcessWords(final String words) {
        return words.trim().replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase().split("\\s+");
    }
}
