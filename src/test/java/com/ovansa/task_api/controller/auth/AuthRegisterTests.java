/**
 * Integration tests for user registration.

 * TODO (Future test coverage ideas to consider):
 * 1. Rejects registration when required fields are missing (email / username / password)
 * 2. Rejects registration when email format is invalid
 * 3. Rejects registration when password does not meet security rules
 * 4. Trims and normalizes input (e.g. email casing) before saving
 * 5. Ensures password is stored hashed, not in plain text
 * 6. Sets default user state correctly (active = true on registration)
 * 7. Prevents duplicate registration under concurrent requests
 * 8. Returns consistent error response structure for validation failures
 * 9. Verifies createdAt and updatedAt are set on successful registration
 * 10. Does not expose sensitive fields (password, internal flags) in response
 */
package com.ovansa.task_api.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ovansa.task_api.domain.Messages;
import com.ovansa.task_api.domain.entities.User;
import com.ovansa.task_api.domain.request.RegisterUserRequest;
import com.ovansa.task_api.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static com.ovansa.task_api.util.TestUtils.randomRegisterRequest;
import static com.ovansa.task_api.util.TestUtils.saveUser;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthRegisterTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Registers a new user successfully with valid details")
    void shouldRegisterUserSuccessfully() throws Exception {
        RegisterUserRequest registerUserRequest = randomRegisterRequest();

        mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerUserRequest))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value(Messages.USER_REGISTERED_SUCCESSFULLY))
                .andExpect(jsonPath("$.user.id").exists())
                .andExpect(jsonPath("$.user.username").value(registerUserRequest.getUsername()))
                .andExpect(jsonPath("$.user.email").value(registerUserRequest.getEmail()));
    }

    @Test
    @DisplayName("Rejects registration when username already exists")
    void shouldRejectRegistrationWhenUsernameExists() throws Exception {
        User existing = saveUser(userRepository);
        RegisterUserRequest request = randomRegisterRequest();
        request.setUsername(existing.getUsername());

        mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(Messages.USERNAME_EXISTS));
    }

    @Test
    @DisplayName("Rejects registration when email already exists")
    void shouldRejectRegistrationWhenEmailExists() throws Exception {
        User existing = saveUser(userRepository);
        RegisterUserRequest request = randomRegisterRequest();
        request.setEmail(existing.getEmail());

        mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(Messages.EMAIL_EXISTS));
    }
}