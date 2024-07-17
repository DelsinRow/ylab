package com.sinaev.models.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) for User.
 * <p>
 * This record represents the data required to create or update a user. It includes
 * validation annotations to ensure that the necessary fields are provided and meet the specified constraints.
 * </p>
 *
 * @param username  the username of the user
 * @param password  the password of the user
 * @param admin     indicates if the user has admin privileges
 */
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
