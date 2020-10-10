package com.phil.movieland.rest.request;

import com.phil.movieland.data.entity.Seat;

import java.util.List;

public class ReservationRequest {
    private int show_id;
    private List<Seat> seats;

    public int getShow_id() {
        return show_id;
    }

    public void setShow_id(int show_id) {
        this.show_id=show_id;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats=seats;
    }
}
