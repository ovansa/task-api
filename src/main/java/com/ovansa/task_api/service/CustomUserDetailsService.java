package com.ovansa.task_api.service;

import com.ovansa.task_api.domain.CustomUserDetails;
import com.ovansa.task_api.domain.entities.User;
import com.ovansa.task_api.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername (String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail (email).orElseThrow (() -> new UsernameNotFoundException ("Email not found"));
        return new CustomUserDetails (user);
    }

    public User getUserById(UUID userId) {
        return userRepository.findById (userId).orElseThrow (() -> new EntityNotFoundException ("User with id " + userId + " not found"));
    }

//    public User getUserByEmail
}
