package com.ovansa.task_api.service;

import com.ovansa.task_api.domain.Messages;
import com.ovansa.task_api.domain.entities.User;
import com.ovansa.task_api.domain.request.LoginUserRequest;
import com.ovansa.task_api.domain.request.RegisterUserRequest;
import com.ovansa.task_api.domain.response.LoginResponse;
import com.ovansa.task_api.domain.response.RegisterResponse;
import com.ovansa.task_api.domain.response.UserResponseDto;
import com.ovansa.task_api.exception.DuplicateResourceException;
import com.ovansa.task_api.exception.InvalidCredentialsException;
import com.ovansa.task_api.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    /**
     * Register a new user
     */
    public RegisterResponse registerUser(@Valid RegisterUserRequest userDto) {
        log.info("Attempting to register user: email={}, username={}",
                userDto.getEmail(), userDto.getUsername());

        validateUserRegistration(userDto);

        User user = User.create(
                userDto.getEmail(),
                userDto.getUsername(),
                passwordEncoder.encode(userDto.getPassword())
        );

        User savedUser = userRepository.save(user);

        log.info("Successfully registered user: id={}, email={}",
                savedUser.getId(), savedUser.getEmail());

        return new RegisterResponse (Messages.USER_REGISTERED_SUCCESSFULLY,
                UserResponseDto.from(savedUser));
    }

    /**
     * Login user with email and password
     */
    public LoginResponse loginUser(@Valid LoginUserRequest loginUserRequest) {
        log.info("User attempting login: email={}", loginUserRequest.getEmail());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginUserRequest.getEmail(), loginUserRequest.getPassword()
                    )
            );

            User user = userRepository.findByEmail(loginUserRequest.getEmail())
                    .orElseThrow(InvalidCredentialsException::new);

            String token = jwtTokenService.generateToken(loginUserRequest.getEmail());
            log.info("User logged in successfully: id={}, email={}", user.getId(), user.getEmail());

            return new LoginResponse(token, UserResponseDto.from(user));

        } catch (Exception e) {
            log.warn("Failed login attempt for email={}: {}", loginUserRequest.getEmail(), e.getMessage());
            throw new InvalidCredentialsException();
        }
    }

    /**
     * Validate that email and username are unique
     */
    private void validateUserRegistration(RegisterUserRequest userDto) {
        if (userRepository.existsByUsername(userDto.getUsername())) {
            log.warn("Duplicate username registration attempt: username={}", userDto.getUsername());
            throw new DuplicateResourceException(Messages.USERNAME_EXISTS);
        }

        if (userRepository.existsByEmail(userDto.getEmail())) {
            log.warn("Duplicate email registration attempt: email={}", userDto.getEmail());
            throw new DuplicateResourceException(Messages.EMAIL_EXISTS);
        }
    }
}