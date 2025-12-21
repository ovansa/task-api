package com.ovansa.task_api.controller;

import com.ovansa.task_api.domain.request.LoginUserRequest;
import com.ovansa.task_api.domain.request.RegisterUserRequest;
import com.ovansa.task_api.domain.response.LoginResponse;
import com.ovansa.task_api.domain.response.RegisterResponse;
import com.ovansa.task_api.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginUserRequest loginUserRequest) {
        LoginResponse loginResponse = authenticationService.loginUser (loginUserRequest);
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterUserRequest registerUserRequest) {
        RegisterResponse registerResponse = authenticationService.registerUser (registerUserRequest);
        return new ResponseEntity<> (registerResponse, HttpStatus.CREATED);
    }
}
