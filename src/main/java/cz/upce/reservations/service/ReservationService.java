package cz.upce.reservations.service;

import cz.upce.reservations.domain.Room;
import cz.upce.reservations.domain.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class ReservationService {
    public void createReservation(User user, Room room, LocalDateTime start, LocalDateTime end) {

    }
}
