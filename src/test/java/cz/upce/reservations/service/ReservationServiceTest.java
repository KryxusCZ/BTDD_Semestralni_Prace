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

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

        @Test
        void shouldThrowWhenCancellingCompletedReservation(){
            Reservation reservation = new Reservation();
            reservation.setStatus(ReservationStatus.COMPLETED);

            assertThrows(InvalidReservationStateException.class, () -> {
                reservationService.cancelReservation(reservation);
            });
        }

    @Test
    void shouldThrowWhenCancellingAlreadyCancelledReservation() {
        Reservation reservation = new Reservation();
        reservation.setStatus(ReservationStatus.CANCELLED);

        assertThrows(InvalidReservationStateException.class, () -> {
            reservationService.cancelReservation(reservation);
        });
    }

    @Test
    void shouldCalculateBasePriceForRegularUser() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setRole(Role.USER);
        
        Room room = new Room();
        room.setId(1L);
        room.setHourlyRate(new BigDecimal("100"));
        
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 1, 12, 0);
        
        // Act
        BigDecimal totalPrice = reservationService.calculatePrice(user, room, start, end);
        
        // Assert
        BigDecimal expectedPrice = new BigDecimal("200");
        assertEquals(0, expectedPrice.compareTo(totalPrice));
    }

    @Test
    void shouldCalculateBasePriceForAdminUser() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setRole(Role.ADMIN);

        Room room = new Room();
        room.setId(1L);
        room.setHourlyRate(new BigDecimal("100"));
        
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 1, 12, 0); // 2 hours
        
        // Act
        BigDecimal totalPrice = reservationService.calculatePrice(user, room, start, end);
        
        // Assert
        // Base: 100 * 2 = 200
        // ADMIN 20% discount: 200 * 0.8 = 160
        BigDecimal expectedPrice = new BigDecimal("160");
        assertEquals(0, expectedPrice.compareTo(totalPrice));
    }

    @Test
    void shouldCalculateDiscountPriceForUser() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setRole(Role.USER);

        Room room = new Room();
        room.setId(1L);
        room.setHourlyRate(new BigDecimal("100"));

        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 1, 14, 0);

        //Act
        BigDecimal totalPrice = reservationService.calculatePrice(user, room, start, end);

        //Assert
        //Base 100 * 4 = 400
        //User with and over 4 hours 10% Discount: 400 * 0.9 = 360
        BigDecimal expectedPrice = new BigDecimal("360");
        assertEquals(0, expectedPrice.compareTo(totalPrice));
    }

    @Test
    void shouldCalculateDiscountPriceForAdminUser() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setRole(Role.ADMIN);

        Room room = new Room();
        room.setId(1L);
        room.setHourlyRate(new BigDecimal("100"));

        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 1, 14, 0);

        //Act
        BigDecimal totalPrice = reservationService.calculatePrice(user, room, start, end);

        //Assert
        //Discount
        //Admin with over 4 hours 20% Discount + 10% Discount: 400 * 0.8 * 0.9 = 288
        BigDecimal expectedPrice = new BigDecimal("288");
        assertEquals(0, expectedPrice.compareTo(totalPrice));

    }

}
