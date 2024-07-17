package com.sinaev.mappers;

import com.sinaev.models.dto.RoomDTO;
import com.sinaev.models.entities.Room;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for converting between Room entities and RoomDTOs.
 * <p>
 * This interface uses MapStruct to automatically generate the implementation for mapping
 * between {@link Room} and {@link RoomDTO} objects.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface RoomMapper {

    /**
     * Converts a Room entity to a RoomDTO.
     *
     * @param room the Room entity to convert
     * @return the converted RoomDTO
     */
    RoomDTO toDTO(Room room);

    /**
     * Converts a RoomDTO to a Room entity.
     *
     * @param roomDTO the RoomDTO to convert
     * @return the converted Room entity
     */
    Room toEntity(RoomDTO roomDTO);
}