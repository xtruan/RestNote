/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xtruan.restnote;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class NoteControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private List<String> quotes;

    @Before
    public void initializeNotes() throws Exception {

        quotes = new ArrayList<>();
        quotes.add("I'm gonna make him an offer he can't refuse.");
        quotes.add("Toto, I've a feeling we're not in Kansas anymore.");
        quotes.add("Here's looking at you, kid.");
        quotes.add("Go ahead, make my day...");
        quotes.add("May the Force be with you.");

        for (final String quote : quotes) {
            this.mockMvc.perform(post("/api/notes").content(quote))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.body").value(quote));
        }
    }

    @Test
    public void testGetNotes() throws Exception {

        this.mockMvc.perform(get("/api/notes/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body").value(quotes.get(0)));

        this.mockMvc.perform(get("/api/notes/3"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body").value(quotes.get(2)));
    }

    @Test
    public void testListNotes() throws Exception {

        this.mockMvc.perform(get("/api/notes"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].body").value(quotes.get(0)))
                .andExpect(jsonPath("$[1].body").value(quotes.get(1)))
                .andExpect(jsonPath("$[2].body").value(quotes.get(2)))
                .andExpect(jsonPath("$[3].body").value(quotes.get(3)))
                .andExpect(jsonPath("$[4].body").value(quotes.get(4)));
    }

    @Test
    public void testQueryNotesOneWord() throws Exception {

        this.mockMvc.perform(get("/api/notes?query=kansas"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].body").value(quotes.get(1)));

        this.mockMvc.perform(get("/api/notes?query=you"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].body").value(quotes.get(2)))
                .andExpect(jsonPath("$[1].body").value(quotes.get(4)));
    }

    @Test
    public void testQueryNotesMultipleWords() throws Exception {

        // words are ANDed (default)
        this.mockMvc.perform(get("/api/notes?query=make him"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].body").value(quotes.get(0)));

        // words are ORed
        this.mockMvc.perform(get("/api/notes?query=make him&matchAny=true"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].body").value(quotes.get(0)))
                .andExpect(jsonPath("$[1].body").value(quotes.get(3)));
    }

}
