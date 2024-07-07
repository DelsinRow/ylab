package com.sinaev.mappers;

import com.sinaev.models.dto.RoomDTO;
import com.sinaev.models.entities.Room;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RoomMapper {
    RoomMapper INSTANCE = Mappers.getMapper(RoomMapper.class);

    RoomDTO toDTO(Room room);


    Room toEntity(RoomDTO roomDTO);
}