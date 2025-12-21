package com.ovansa.task_api.domain.request;

import com.ovansa.task_api.domain.Messages;
import com.ovansa.task_api.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RegisterUserRequest {
    @NotBlank(message = Messages.EMAIL_REQUIRED)
    @Email(message = Messages.INVALID_EMAIL)
    private String email;

    @NotBlank(message = Messages.USERNAME_REQUIRED)
    private String username;

    @NotBlank(message = Messages.PASSWORD_REQUIRED)
    @ValidPassword
    private String password;
}
