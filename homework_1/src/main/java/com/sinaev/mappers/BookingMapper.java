package com.sinaev.mappers;

import com.sinaev.models.dto.BookingDTO;
import com.sinaev.models.entities.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for converting between Booking entities and BookingDTOs.
 * <p>
 * This interface uses MapStruct to automatically generate the implementation for mapping
 * between {@link Booking} and {@link BookingDTO} objects.
 * </p>
 */
@Mapper
public interface BookingMapper {
    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    /**
     * Converts a BookingDTO to a Booking entity.
     *
     * @param bookingDTO the BookingDTO to convert
     * @return the converted Booking entity
     */
    @Mapping(source = "roomName", target = "room.name")
    @Mapping(source = "username", target = "user.username")
    Booking toEntity(BookingDTO bookingDTO);

    /**
     * Converts a Booking entity to a BookingDTO.
     *
     * @param booking the Booking entity to convert
     * @return the converted BookingDTO
     */
    @Mapping(source = "room.name", target = "roomName")
    @Mapping(source = "user.username", target = "username")
    BookingDTO toDTO(Booking booking);

}
