package com.sinaev.models.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserDTO(
        @NotNull(message = "Username cannot be null")
        @Size(min = 1, max = 10, message = "Username must be between 1 and 10 characters")
        String username,

        @NotNull(message = "Password cannot be null")
        @Size(min = 3, message = "Password must be at least 6 characters long")
        String password,

        boolean admin
) {
}
