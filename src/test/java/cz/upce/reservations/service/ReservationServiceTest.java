package cz.upce.reservations.service;

import cz.upce.reservations.domain.*;
import cz.upce.reservations.repository.ReservationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    ReservationRepository reservationRepository;

    @InjectMocks
    ReservationService reservationService;


    @Test
    void shouldThrowWhenRoomIsAlreadyBooked(){
        Room room = new Room();
        room.setId(1L);

        User user = new User();
        user.setId(1L);

        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 1, 12, 0);

        Reservation existing = new Reservation();
        existing.setRoom(room);
        existing.setStartTime(start);
        existing.setEndTime(end);
        existing.setStatus(ReservationStatus.CONFIRMED);

        when(reservationRepository.findByRoom(room))
            .thenReturn(List.of(existing));


        assertThrows(RoomNotAvailableException.class, () -> {
            reservationService.createReservation(user, room, start, end);
        });
    }

}
