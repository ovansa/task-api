package com.ovansa.task_api.controller.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ovansa.task_api.domain.Messages;
import com.ovansa.task_api.domain.entities.Task;
import com.ovansa.task_api.domain.entities.User;
import com.ovansa.task_api.domain.request.UpdateTaskStatusRequest;
import com.ovansa.task_api.enums.TaskStatus;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TaskStatusUpdateTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Should update task status to COMPLETED for authenticated user")
    void shouldUpdateTaskStatusSuccessfully() throws Exception {
        String rawPassword = "Password@123";
        User user = TestUtils.saveUser(userRepository, passwordEncoder, rawPassword);
        Task task = TestUtils.saveTask(taskRepository, user, "Buy food");

        String token = TestUtils.loginAndGetToken(mockMvc, user.getEmail(), rawPassword);

        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest();
        request.setStatus(TaskStatus.COMPLETED);

        mockMvc.perform(
                        patch("/task/{taskId}/status", task.getId())
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(Messages.TASK_UPDATED))
                .andExpect(jsonPath("$.data.status").value("COMPLETED"))
                .andExpect(jsonPath("$.data.completedAt").exists());
    }

    @Test
    @DisplayName("Should return 401 when updating task status without authentication")
    void shouldFailToUpdateTaskStatusWithoutAuthentication() throws Exception {
        // given
        User user = TestUtils.saveUser(userRepository, passwordEncoder);
        Task task = TestUtils.saveTask(taskRepository, user, "Wash car");

        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest();
        request.setStatus(TaskStatus.COMPLETED);

        // when / then
        mockMvc.perform(
                        patch("/task/{taskId}/status", task.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Not authorized"));
    }
}