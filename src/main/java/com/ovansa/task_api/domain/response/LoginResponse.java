package com.ovansa.task_api.domain.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class LoginResponse {
    private String token;
    private UserResponseDto userResponseDto;
}
