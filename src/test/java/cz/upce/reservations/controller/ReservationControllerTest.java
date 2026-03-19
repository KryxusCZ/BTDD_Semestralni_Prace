package cz.upce.reservations.controller;

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
        User user = new User();
        user.setName("Honza");
        user.setEmail("rendla@example.com");
        user.setRole(Role.USER);
        user = userRepository.save(user);

        Room room = new Room();
        room.setName("Room 1");
        room.setLocation("Building A");
        room.setCapacity(10);
        room.setHourlyRate(new BigDecimal("100"));
        room.setOpeningTime(LocalTime.of(8, 0));
        room.setClosingTime(LocalTime.of(18, 0));
        room.setActive(true);
        room = roomRepository.save(room);

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
        User user = new User();
        user.setName("Dalibor");
        user.setEmail("dalibor@example.com");
        user.setRole(Role.USER);
        user = userRepository.save(user);

        Room room = new Room();
        room.setName("Room 2");
        room.setLocation("Building A");
        room.setCapacity(10);
        room.setHourlyRate(new BigDecimal("100"));
        room.setOpeningTime(LocalTime.of(8, 0));
        room.setClosingTime(LocalTime.of(18, 0));
        room.setActive(true);
        room = roomRepository.save(room);

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setRoom(room);
        reservation.setStartTime(LocalDateTime.of(2027, 1, 1, 10, 0));
        reservation.setEndTime(LocalDateTime.of(2027, 1, 1, 16, 0));
       // reservation.setTotalPrice(new BigDecimal("600"));
        reservation.setStatus(cz.upce.reservations.domain.ReservationStatus.CONFIRMED);
        reservation = reservationRepository.save(reservation);

        mockMvc.perform(get("/reservations/{id}", reservation.getId()))
                .andExpect(status().isOk());

    }

    @Test
    void shouldCancelReservationAndReturn204() throws Exception {
        User user = new User();
        user.setName("Jana");
        user.setEmail("jana@example.com");
        user.setRole(Role.USER);
        user = userRepository.save(user);

        Room room = new Room();
        room.setName("Room 3");
        room.setLocation("Building B");
        room.setCapacity(10);
        room.setHourlyRate(new BigDecimal("100"));
        room.setOpeningTime(LocalTime.of(8, 0));
        room.setClosingTime(LocalTime.of(18, 0));
        room.setActive(true);
        room = roomRepository.save(room);

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setRoom(room);
        reservation.setStartTime(LocalDateTime.of(2027, 1, 1, 10, 0));
        reservation.setEndTime(LocalDateTime.of(2027, 1, 1, 16, 0));
        reservation.setStatus(cz.upce.reservations.domain.ReservationStatus.CONFIRMED);
       // reservation.setTotalPrice(new BigDecimal("600"));
        reservation = reservationRepository.save(reservation);

        mockMvc.perform(delete("/reservations/{id}", reservation.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(user.getId())))
                .andExpect(status().isNoContent());


    }



}
