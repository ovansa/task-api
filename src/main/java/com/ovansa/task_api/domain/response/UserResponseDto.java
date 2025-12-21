package com.ovansa.task_api.domain.response;

import com.ovansa.task_api.domain.entities.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class UserResponseDto {
    private UUID id;
    private String username;
    private String email;

    public static UserResponseDto from(User user) {
        return new UserResponseDto (
                user.getId (),
                user.getUsername (),
                user.getEmail ()
        );
    }
}
