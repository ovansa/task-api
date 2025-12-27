package com.ovansa.task_api.controller.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ovansa.task_api.domain.Messages;
import com.ovansa.task_api.domain.entities.Task;
import com.ovansa.task_api.domain.entities.User;
import com.ovansa.task_api.domain.request.CreateTaskRequest;
import com.ovansa.task_api.domain.request.UpdateTaskRequest;
import com.ovansa.task_api.repository.TaskRepository;
import com.ovansa.task_api.repository.UserRepository;
import com.ovansa.task_api.util.TestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith (SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TaskUpdateTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper ();

    @Test
    @DisplayName("Should update task successfully as authenticated owner")
    void shouldUpdateTaskAsAuthenticatedUser() throws Exception {
        String rawPassword = "Password@123";
        User user = TestUtils.saveUser(userRepository, passwordEncoder, rawPassword);
        String token = TestUtils.loginAndGetToken(mockMvc, user.getEmail(), rawPassword);

        Task task = TestUtils.saveTask(taskRepository, user, "Old title");

        UpdateTaskRequest request = new UpdateTaskRequest();
        request.setTitle("Updated title");

        mockMvc.perform(put("/task/{id}", task.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(Messages.TASK_UPDATED))
                .andExpect(jsonPath("$.data.id").value(task.getId().toString()))
                .andExpect(jsonPath("$.data.title").value("Updated title"));
    }

    @Test
    @DisplayName("Should fail to update task when user is not authenticated")
    void shouldFailToUpdateTaskWithoutAuthentication() throws Exception {

        UpdateTaskRequest request = new UpdateTaskRequest();
        request.setTitle("Updated title");

        mockMvc.perform(put("/task/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Not authorized"));
    }
}
