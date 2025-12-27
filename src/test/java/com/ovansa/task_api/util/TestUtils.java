package com.ovansa.task_api.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ovansa.task_api.domain.entities.Task;
import com.ovansa.task_api.domain.entities.User;
import com.ovansa.task_api.domain.request.LoginUserRequest;
import com.ovansa.task_api.domain.request.RegisterUserRequest;
import com.ovansa.task_api.enums.TaskStatus;
import com.ovansa.task_api.repository.TaskRepository;
import com.ovansa.task_api.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.ThreadLocalRandom;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TestUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper ();

    public static String randomAlphaNumeric(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /* Auth helpers */
    public static String loginAndGetToken(MockMvc mockMvc, String email, String password) throws Exception {
        LoginUserRequest loginRequest = new LoginUserRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        String response = mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("token").asText();
    }

    public static RegisterUserRequest randomRegisterRequest() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("user_" + randomAlphaNumeric(6));
        request.setEmail("user_" + randomAlphaNumeric(7) + "@example.com");
        request.setPassword("StrongPass1");
        return request;
    }

    /* User helpers */
    public static User saveUser(UserRepository userRepository) {
        String rawPassword = "StrongPass1";
        return saveUser(userRepository, rawPassword);
    }

    public static User saveUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        String email = "user_" + randomAlphaNumeric(6) + "@example.com";
        String username = "user_" + randomAlphaNumeric(6);
        String rawPassword = "StrongPass1";

        String encodedPassword = passwordEncoder.encode(rawPassword);

        User user = User.create(email, username, encodedPassword);
        return userRepository.save(user);
    }

    public static User saveUser(UserRepository userRepository, PasswordEncoder passwordEncoder, String rawPassword) {
        String email = "user_" + randomAlphaNumeric(6) + "@example.com";
        String username = "user_" + randomAlphaNumeric(6);
        String encodedPassword = passwordEncoder.encode(rawPassword);

        User user = User.create(email, username, encodedPassword);
        return userRepository.save(user);
    }

    public static User saveUser(UserRepository userRepository, String rawPassword) {
        User user = User.create(
                "user_" + randomAlphaNumeric(6) + "@example.com",
                "user_" + randomAlphaNumeric(6),
                rawPassword
        );
        return userRepository.save(user);
    }

    /* Task helpers */
    public static Task saveTask(
            TaskRepository taskRepository,
            User owner,
            String title
    ) {
        Task task = Task.create(
                title,
                owner,
                TaskStatus.PENDING
        );

        return taskRepository.save(task);
    }
}