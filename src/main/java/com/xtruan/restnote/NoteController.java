package com.xtruan.restnote;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for Notes
 */
@RestController
@RequestMapping("/api/notes")
public class NoteController {

    /**
     * Counter to provide a unique ID for each note
     */
    private final AtomicInteger counter = new AtomicInteger();

    /**
     * Map of note objects
     */
    private Map<Integer, Note> notes = Collections.synchronizedMap(new HashMap<>());

    /**
     * Map containing index of notes. This contains a mapping of every word to the notes it is present in.
     */
    private Map<String, Set<Integer>> index = Collections.synchronizedMap(new HashMap<>());

    /**
     * Function responsible for listing notes. Lists all notes with no argument, searches for notes matching query
     * if query parameter defined. Treats query terms as if they are ORed.
     * @param query
     * @return collection of notes
     */
    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody Collection<Note> listNotes(@RequestParam(value="query", required=false, defaultValue = "") final String query) {
        if (query.isEmpty()) {
            return notes.values();
        } else {
            Collection<Note> matchingNotes = new ArrayList<>();
            Set<Integer> matchingNoteIds = new HashSet<>();

            // get a cleaned up list of words in the query
            final Set<String> queryWords = splitAndProcessWords(query);

            // for each word, see if it is in the index, and add all occurrences to a set
            for (final String queryWord : queryWords) {
                if (index.containsKey(queryWord)) {
                    matchingNoteIds.addAll(index.get(queryWord));
                }
            }

            // build a collection of all the matched notes
            for (Integer noteId : matchingNoteIds) {
                matchingNotes.add(notes.get(noteId));
            }

            return matchingNotes;
        }
    }

    /**
     * Function to create a new note
     * @param noteBody
     * @return the new note
     */
    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody Note createNote(@RequestBody final String noteBody) {
        final int noteId = counter.incrementAndGet();
        final Note note = new Note(noteId, noteBody);
        notes.put(noteId, note);

        // get a cleaned up list of words in the note
        final Set<String> noteWords = splitAndProcessWords(noteBody);

        // add the words to the index
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

    /**
     * Function to get a specific note by ID
     * @param noteId
     * @return the note corresponding to the ID
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{noteId}")
    public @ResponseBody Note getNote(@PathVariable final Integer noteId) {
        return notes.get(noteId);
    }

    /**
     * Function to clean up and de-duplicate a string of words
     * @param words
     * @return a set of cleaned up words
     */
    private Set<String> splitAndProcessWords(final String words) {
        // trim the input string, erase all non-word characters except spaces, make lower case,
        // split on whitespace, and put in a set to remove duplicates
        return new HashSet<String>(
                Arrays.asList(
                        words.trim()
                                .replaceAll("[^a-zA-Z0-9 ]", "")
                                .toLowerCase()
                                .split("\\s+")));
    }
}
