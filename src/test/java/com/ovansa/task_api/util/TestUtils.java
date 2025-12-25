package com.ovansa.task_api.util;

import com.ovansa.task_api.domain.entities.User;
import com.ovansa.task_api.domain.request.RegisterUserRequest;
import com.ovansa.task_api.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.concurrent.ThreadLocalRandom;

public class TestUtils {
    public static String randomAlphaNumeric(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public static RegisterUserRequest randomRegisterRequest() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("user_" + randomAlphaNumeric(6));
        request.setEmail("user_" + randomAlphaNumeric(7) + "@example.com");
        request.setPassword("StrongPass1");
        return request;
    }

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
}