package com.phil.movieland.rest.controller;

import com.phil.movieland.data.entity.Seat;
import com.phil.movieland.rest.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
public class SeatController {
    private final ReservationService reservationService;

    @Autowired
    public SeatController(ReservationService reservationService) {
        this.reservationService=reservationService;
    }

    @GetMapping("/reservations/{resId}")
    public List<Seat> getSeatsOfReservation(@PathVariable Long resId) {
        return reservationService.getAllSeatsOfReservation(resId);
    }

    @GetMapping("/shows/{showId}")
    public List<Seat> getSeatsOfShow(@PathVariable Integer showId) {
        return reservationService.getAllSeatsOfShow(showId);
    }

}
