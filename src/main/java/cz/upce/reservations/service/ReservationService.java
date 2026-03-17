package cz.upce.reservations.service;

import cz.upce.reservations.domain.Reservation;
import cz.upce.reservations.domain.Room;
import cz.upce.reservations.domain.RoomNotAvailableException;
import cz.upce.reservations.domain.User;
import cz.upce.reservations.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public void createReservation(User user, Room room, LocalDateTime start, LocalDateTime end) {


        List<Reservation> existing = reservationRepository.findByRoom(room);

        for (Reservation r : existing) {
            if (start.isBefore(r.getEndTime()) && end.isAfter(r.getStartTime())) {
                throw new RoomNotAvailableException("Room is already booked for the selected time slot.");
            }
        }

    }

    public void cancelReservation(Reservation reservation) {

    }
}
