package com.sinaev.mappers;

import com.sinaev.models.dto.UserDTO;
import com.sinaev.models.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

/**
 * Mapper interface for converting between User entities and UserDTOs.
 * <p>
 * This interface uses MapStruct to automatically generate the implementation for mapping
 * between {@link User} and {@link UserDTO} objects.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Converts a UserDTO to a User entity.
     *
     * @param userDTO the UserDTO to convert
     * @return the converted User entity
     */
    User toEntity(UserDTO userDTO);

    /**
     * Converts a User entity to a UserDTO.
     *
     * @param user the User entity to convert
     * @return the converted UserDTO
     */
    UserDTO toDTO(User user);
}
