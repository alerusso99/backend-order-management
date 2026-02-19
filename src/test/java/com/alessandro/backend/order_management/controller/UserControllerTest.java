package com.alessandro.backend.order_management.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // -----------------------
    // TESTS
    // -----------------------

    @Test
    void createUser_shouldReturn201() throws Exception {
        postUser("mario@test.com", "Mario")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("mario@test.com"))
                .andExpect(jsonPath("$.name").value("Mario"));
    }

    @Test
    void listUsers_shouldReturnPagedResponse() throws Exception {
        createUsers(12);

        mockMvc.perform(get("/users?page=0&size=10&sort=id,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(10))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalItems").value(12));
    }

    @Test
    void listUsers_sizeToLarge_shouldBeClamped() throws Exception {
        createUsers(60);

        mockMvc.perform(get("/users?page=0&size=100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(50))
                .andExpect(jsonPath("$.size").value(50));
    }

    @Test
    void createUser_invalidInput_shouldReturn400() throws Exception {
        // qui bypassiamo il helper "postUser(email,name)" perché vogliamo inviare valori vuoti
        Map<String, String> request = Map.of(
                "email", "",
                "name", ""
        );

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.email").exists())
                .andExpect(jsonPath("$.validationErrors.name").exists());
    }

    @Test
    void createUser_duplicateEmail_shouldReturn409() throws Exception {
        postUser("dup@test.com", "Mario")
                .andExpect(status().isCreated());

        postUser("dup@test.com", "Mario")
                .andExpect(status().isConflict());
    }

    @Test
    void getUser_existing_shouldReturn200() throws Exception {
        long id = createUserAndReturnId("get@test.com", "GetUser");

        mockMvc.perform(get("/users/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("GetUser"))
                .andExpect(jsonPath("$.email").value("get@test.com"));
    }

    @Test
    void getUser_invalidPath_shouldReturn400() throws Exception {
        mockMvc.perform(get("/users/abc"))
                .andExpect(status().isBadRequest());
    }

    // -----------------------
    // HELPERS
    // -----------------------

    /**
     * Helper "base": fa POST /users e ritorna ResultActions
     * così nei test puoi decidere tu se aspettarti 201 / 400 / 409 ecc.
     */
    private ResultActions postUser(String email, String name) throws Exception {
        Map<String, String> req = Map.of("email", email, "name", name);

        return mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));
    }


    private long createUserAndReturnId(String email, String name) throws Exception {
        String responseBody = postUser(email, name)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(responseBody);
        return json.get("id").asLong();
    }


    private void createUsers(int count) throws Exception {
        for (int i = 1; i <= count; i++) {
            postUser("u" + i + "@test.com", "User " + i)
                    .andExpect(status().isCreated());
        }
    }
}
