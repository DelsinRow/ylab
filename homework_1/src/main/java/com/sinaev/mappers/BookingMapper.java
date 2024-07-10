package com.sinaev.mappers;

import com.sinaev.models.dto.BookingDTO;
import com.sinaev.models.entities.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BookingMapper {
    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    @Mapping(source = "roomName", target = "room.name")
    @Mapping(source = "username", target = "user.username")
    Booking toEntity(BookingDTO bookingDTO);

    @Mapping(source = "room.name", target = "roomName")
    @Mapping(source = "user.username", target = "username")
    BookingDTO toDTO(Booking booking);

}
