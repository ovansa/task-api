package com.ovansa.task_api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PasswordConstraintValidator  implements ConstraintValidator<ValidPassword, String> {
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$"
    );

    @Override
    public boolean isValid (String password, ConstraintValidatorContext constraintValidatorContext) {
        if (password == null) {
            return false;
        }

        return PASSWORD_PATTERN.matcher (password).matches ();
    }
}
