package cz.upce.reservations.controller;

import cz.upce.reservations.domain.InvalidReservationStateException;
import cz.upce.reservations.domain.RoomNotAvailableException;
import cz.upce.reservations.domain.UnauthorizedActionException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RoomNotAvailableException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleRoomNotAvailable(RoomNotAvailableException ex) {
        return ex.getMessage();

    }

    @ExceptionHandler(InvalidReservationStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleInvalidReservationState(InvalidReservationStateException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(UnauthorizedActionException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleUnauthorizedAction(UnauthorizedActionException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleIllegalArgumentException(IllegalArgumentException ex) {
        return ex.getMessage();
    }



}
