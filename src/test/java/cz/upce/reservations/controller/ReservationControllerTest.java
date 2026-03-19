package cz.upce.reservations.controller;

import cz.upce.reservations.domain.ReservationStatus;
import cz.upce.reservations.domain.Reservation;
import cz.upce.reservations.domain.Role;
import cz.upce.reservations.domain.Room;
import cz.upce.reservations.domain.User;
import cz.upce.reservations.repository.ReservationRepository;
import cz.upce.reservations.repository.RoomRepository;
import cz.upce.reservations.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ReservationControllerTest {

    private User createAndSaveUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setRole(Role.USER);
        return userRepository.save(user);
    }

    private Room createAndSaveRoom(String name, String location) {
        Room room = new Room();
        room.setName(name);
        room.setLocation(location);
        room.setCapacity(10);
        room.setHourlyRate(new BigDecimal("100"));
        room.setOpeningTime(LocalTime.of(8, 0));
        room.setClosingTime(LocalTime.of(18, 0));
        room.setActive(true);
        return roomRepository.save(room);
    }

    @BeforeEach
    public void setup() {
        reservationRepository.deleteAll();
        roomRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    void shouldCreateReservationAndReturn201() throws Exception {
        User user = createAndSaveUser("Dalibor", "dalibor@example.com");

        Room room = createAndSaveRoom("Room 1", "Building A");

        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "userId": %d,
                            "roomId": %d,
                            "startTime": "2027-01-01T10:00:00",
                            "endTime": "2027-01-01T12:00:00"
                        }
              """.formatted(user.getId(), room.getId())))
                .andExpect(status().isCreated());

    }

    @Test
    void shouldGetReservationAndReturn200() throws Exception {
        User user = createAndSaveUser("Fanda", "fanda@example.com");

        Room room = createAndSaveRoom("Room 2", "Building A");

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setRoom(room);
        reservation.setStartTime(LocalDateTime.of(2027, 1, 1, 10, 0));
        reservation.setEndTime(LocalDateTime.of(2027, 1, 1, 16, 0));
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation = reservationRepository.save(reservation);

        mockMvc.perform(get("/reservations/{id}", reservation.getId()))
                .andExpect(status().isOk());

    }

    @Test
    void shouldCancelReservationAndReturn204() throws Exception {
        User user = createAndSaveUser("Jana", "jana@example.com");

        Room room = createAndSaveRoom("Room 3", "Building B");

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setRoom(room);
        reservation.setStartTime(LocalDateTime.of(2027, 1, 1, 10, 0));
        reservation.setEndTime(LocalDateTime.of(2027, 1, 1, 16, 0));
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation = reservationRepository.save(reservation);

        mockMvc.perform(delete("/reservations/{id}", reservation.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(user.getId())))
                .andExpect(status().isNoContent());


    }

}
