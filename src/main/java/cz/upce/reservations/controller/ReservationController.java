package cz.upce.reservations.controller;

import cz.upce.reservations.dto.ReservationRequest;
import cz.upce.reservations.domain.Reservation;
import cz.upce.reservations.domain.Room;
import cz.upce.reservations.domain.User;
import cz.upce.reservations.repository.RoomRepository;
import cz.upce.reservations.repository.UserRepository;
import cz.upce.reservations.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/reservations")
public class ReservationController {
    
    private final ReservationService reservationService;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    
    // Constructor for dependency injection
    public ReservationController(ReservationService reservationService,
                                UserRepository userRepository,
                                RoomRepository roomRepository) {
        this.reservationService = reservationService;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
    }
    
    // POST /reservations - create a reservation
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Reservation createReservation(@RequestBody @Valid ReservationRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> 
            new IllegalArgumentException("User not found"));
        Room room = roomRepository.findById(request.getRoomId()).orElseThrow(() -> 
            new IllegalArgumentException("Room not found"));
        
        return reservationService.createReservation(user, room, request.getStartTime(), request.getEndTime());
    }
    
    // GET /reservations/{id} - get a reservation by id
    @GetMapping("/{id}")
    public Reservation getReservation(@PathVariable Long id) {
        return reservationService.getReservationById(id);
    }
    
    // DELETE /reservations/{id} - cancel a reservation
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelReservation(@PathVariable Long id,
                                  @RequestBody Long userId) {
        Reservation reservation = reservationService.getReservationById(id);
        User user = userRepository.findById(userId).orElseThrow(() -> 
            new IllegalArgumentException("User not found"));
        
        reservationService.cancelReservation(user, reservation);
    }
}
