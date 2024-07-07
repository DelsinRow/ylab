package com.sinaev.mappers;

import com.sinaev.models.dto.UserDTO;
import com.sinaev.models.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);


    User toEntity(UserDTO userDTO);

    UserDTO toDTO(User user);
}
