package com.ovansa.task_api.controller.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ovansa.task_api.domain.Messages;
import com.ovansa.task_api.domain.entities.User;
import com.ovansa.task_api.domain.request.CreateTaskRequest;
import com.ovansa.task_api.domain.request.LoginUserRequest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith (SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TaskCreateTests {
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
    @DisplayName("Should create task successfully as authenticated user")
    void shouldCreateTaskAsAuthenticatedUser() throws Exception {
        String rawPassword = "Password@123";
        User user = TestUtils.saveUser(userRepository, passwordEncoder, rawPassword);

        String token = TestUtils.loginAndGetToken(mockMvc, user.getEmail(), rawPassword);

        CreateTaskRequest request = new CreateTaskRequest ();
        request.setTitle ("Eat");

        mockMvc.perform(post("/task")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value(Messages.TASK_CREATED))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.title").value(request.getTitle()))
                .andExpect(jsonPath("$.data.status").value("PENDING"));
    }
}
