package com.xtruan.restnote;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
     * if query parameter defined. Treats query terms as if they are ANDed unless matchAny is set to true.
     * @param query
     * @param matchAny
     * @return collection of notes
     */
    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<Collection<Note>> listNotes(@RequestParam(value="query", required=false, defaultValue = "") final String query,
                                                    @RequestParam(value="matchAny", required=false, defaultValue = "false") final String matchAny) {
        if (query.isEmpty()) {
            // return all notes if no query specified
            return new ResponseEntity<>(notes.values(), HttpStatus.OK);
        } else {
            Collection<Note> matchingNotes = new ArrayList<>();
            Set<Integer> matchingNoteIds = new HashSet<>();

            // get a cleaned up list of words in the query
            final Set<String> queryWords = splitAndProcessWords(query);

            // for each word, see if it is in the index
            boolean firstWord = true;
            for (final String queryWord : queryWords) {
                if (index.containsKey(queryWord)) {
                    // always add all words found in the index for the first query word
                    if (firstWord || matchAny.equals("true")) {
                        // add all occurrences to a set
                        matchingNoteIds.addAll(index.get(queryWord));
                     } else {
                        // keep only items in the set which are already there
                        matchingNoteIds.retainAll(index.get(queryWord));
                    }
                }
                firstWord = false;
            }

            // build a collection of all the matched notes
            for (Integer noteId : matchingNoteIds) {
                matchingNotes.add(notes.get(noteId));
            }

            return new ResponseEntity<>(matchingNotes, HttpStatus.OK);
        }
    }

    /**
     * Function to create a new note
     * @param noteBody
     * @return the new note
     */
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<Note> createNote(@RequestBody final Map<String, String> noteBody) {
        if (!noteBody.containsKey("body")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        final int noteId = counter.incrementAndGet();
        final Note note = new Note(noteId, noteBody.get("body"));
        notes.put(noteId, note);

        // get a cleaned up list of words in the note
        final Set<String> noteWords = splitAndProcessWords(noteBody.get("body"));

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

        return new ResponseEntity<>(note, HttpStatus.OK);
    }

    /**
     * Function to get a specific note by ID
     * @param noteId
     * @return the note corresponding to the ID
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{noteId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<Note> getNote(@PathVariable final Integer noteId) {
        if (!notes.containsKey(noteId)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(notes.get(noteId), HttpStatus.OK);
        }
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
