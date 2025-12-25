/**
 * TODO: Additional login test cases to consider:

 * 1. shouldFailLoginWhenEmailIsMissing
 * 2. shouldFailLoginWhenPasswordIsMissing
 * 3. shouldFailLoginWhenUserIsInactive
 * 4. shouldHandleMultipleFailedLoginAttemptsGracefully (rate limiting / account lock)
 * 5. shouldLoginSuccessfullyWithCaseInsensitiveEmail
 * 6. shouldFailLoginWithSQLInjectionOrXSSInInput
 */

package com.ovansa.task_api.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ovansa.task_api.domain.Messages;
import com.ovansa.task_api.domain.entities.User;
import com.ovansa.task_api.domain.request.LoginUserRequest;
import com.ovansa.task_api.domain.request.RegisterUserRequest;
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

import java.util.concurrent.ThreadLocalRandom;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith (SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthLoginTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper ();

    @Test
    @DisplayName("Should log in user successfully with valid details")
    void shouldLoginUserSuccessfullyWithValidDetails() throws Exception {
        String rawPassword = "Password@1234$";
        User user = TestUtils.saveUser(userRepository, passwordEncoder, rawPassword);

        LoginUserRequest request = new LoginUserRequest ();
        request.setEmail(user.getEmail());
        request.setPassword(rawPassword);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.id").value(user.getId().toString()))
                .andExpect(jsonPath("$.user.email").value(user.getEmail()))
                .andExpect(jsonPath("$.user.password").doesNotExist());;
    }

    @Test
    @DisplayName("Should fail login when password is incorrect")
    void shouldFailLoginWhenPasswordIsIncorrect() throws Exception {
        User user = TestUtils.saveUser(userRepository, passwordEncoder);

        LoginUserRequest request = new LoginUserRequest ();
        request.setEmail(user.getEmail());
        request.setPassword("WrongPassword");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(Messages.INVALID_CREDENTIALS));
    }

    @Test
    @DisplayName("Should fail login when email does not exist")
    void shouldFailLoginWhenEmailDoesNotExist() throws Exception {
        LoginUserRequest request = new LoginUserRequest ();
        request.setEmail("nonexistent@example.com");
        request.setPassword("StrongPass1");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(Messages.INVALID_CREDENTIALS));
    }
}
