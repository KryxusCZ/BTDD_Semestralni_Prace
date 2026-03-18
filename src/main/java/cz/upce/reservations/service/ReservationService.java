package cz.upce.reservations.service;

import cz.upce.reservations.domain.*;
import cz.upce.reservations.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import cz.upce.reservations.domain.UnauthorizedActionException;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.time.temporal.ChronoUnit;


@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public Reservation createReservation(User user, Room room, LocalDateTime start, LocalDateTime end) {

        List<Reservation> existing = reservationRepository.findByRoom(room);

        // First check for overlaps with ANY reservation
        for (Reservation r : existing) {
            if (start.isBefore(r.getEndTime()) && end.isAfter(r.getStartTime())) {
                // If it's an exact duplicate (same user, same time), return it instead of throwing
                if (r.getUser().getId().equals(user.getId()) &&
                    r.getStartTime().equals(start) &&
                    r.getEndTime().equals(end)) {
                    return r;
                }
                // Otherwise, throw exception for overlap
                throw new RoomNotAvailableException("Room is already booked for the selected time slot.");
            }
        }

        Reservation newReservation = new Reservation();
        newReservation.setUser(user);
        newReservation.setRoom(room);
        newReservation.setStartTime(start);
        newReservation.setEndTime(end);
        newReservation.setStatus(ReservationStatus.CONFIRMED);
        return reservationRepository.save(newReservation);
    }

    public void cancelReservation(User user, Reservation reservation) {
        boolean isOwner = reservation.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new UnauthorizedActionException("You are not allowed to cancel this reservation.");
        }

        if (reservation.getStatus() == ReservationStatus.COMPLETED ||
            reservation.getStatus() == ReservationStatus.CANCELLED){
            throw new InvalidReservationStateException("Cannot cancel a completed reservation.");
        }
        
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
    }

    public BigDecimal calculatePrice(User user, Room room, LocalDateTime start, LocalDateTime end) {
        long hours = ChronoUnit.HOURS.between(start, end);

        BigDecimal basePrice = room.getHourlyRate().multiply(new BigDecimal(hours));


        BigDecimal finalPrice = basePrice;

        if (user.getRole() == Role.ADMIN) {
            finalPrice = finalPrice.multiply(new BigDecimal("0.80"));
        }

        if (hours > 3) {
            finalPrice = finalPrice.multiply(new BigDecimal("0.90"));
        }
        
        return finalPrice;
    }

    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id).orElseThrow(() ->
            new IllegalArgumentException("Reservation not found"));
    }
}
