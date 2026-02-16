package com.alessandro.backend.order_management.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createUser_shouldReturn201() throws Exception {
        Map<String, String> request = Map.of(
                "email", "mario@test.com",
                "name", "Mario"
        );

        mockMvc.perform(post("/users").
                    contentType(MediaType.APPLICATION_JSON).
                    content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("mario@test.com"))
                .andExpect(jsonPath("$.name").value("Mario"));
    }

    @Test
    void listUsers_shouldReturnPagedResponse() throws Exception {

        for (int i = 1; i <= 12; i++) {
            Map<String, String> req = Map.of(
                    "email", "u"+i+"@test.com",
                    "name", "User "+i
            );
            mockMvc.perform(post("/users").
                        contentType(MediaType.APPLICATION_JSON).
                        content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isCreated());
        }
        mockMvc.perform(get("/users?page=0&size=10&sort=id,asc")).
                andExpect(status().isOk()).
                andExpect(jsonPath("$.items").isArray()).
                andExpect(jsonPath("$.items.length()").value(10)).
                andExpect(jsonPath("$.page").value(0)).
                andExpect(jsonPath("$.size").value(10)).
                andExpect(jsonPath("$.totalItems").value(12));
    }

    @Test
    void createUser_invalidInput_shouldReturn400() throws Exception {
        Map<String, String> request = Map.of(
                "email", "",
                "name", ""
        );

        mockMvc.perform(post("/users").
                    contentType(MediaType.APPLICATION_JSON).
                    content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.email").exists())
                .andExpect(jsonPath("$.validationErrors.name").exists());
    }

    @Test
    void createUser_duplicateEmail_shouldReturn409() throws Exception {
        Map<String, String> request = Map.of(
                "email", "dup@test.com",
                "name", "Mario"
        );

        mockMvc.perform(post("/users").
                    contentType(MediaType.APPLICATION_JSON).
                    content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/users").
                    contentType(MediaType.APPLICATION_JSON).
                    content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void getUser_existing_shouldReturn200() throws Exception {
        Map<String, String> request = Map.of(
                "email", "get@test.com",
                "name", "GetUser"
        );

        String response = mockMvc.perform(post("/users").
                    contentType(MediaType.APPLICATION_JSON).
                    content(objectMapper.writeValueAsString(request)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(response);
        long id = json.get("id").asLong();

        mockMvc.perform(get("/users/"+id)).
                andExpect(status().isOk()).
                andExpect(jsonPath("$.id").value(id)).
                andExpect(jsonPath("$.name").value("GetUser")).
                andExpect(jsonPath("$.email").value("get@test.com"));
    }

    @Test
    void getUser_invalidPath_shouldReturn400() throws Exception {
        mockMvc.perform(get("/users/abc"))
                .andExpect(status().isBadRequest());
    }
}
