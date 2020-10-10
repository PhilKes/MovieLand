package com.phil.movieland.data.entity;


import javax.persistence.*;
import java.util.HashMap;

import static com.phil.movieland.data.entity.Seat.Seat_Type.*;

@Entity
public class Seat extends EntityWithId{

    public enum Seat_Type {
        CHILD, STUDENT, ADULT, DISABLED
    }

    private static final HashMap<Seat_Type, Double> PRICE_MAP=new HashMap();

    static {
        PRICE_MAP.put(CHILD, 5.5);
        PRICE_MAP.put(STUDENT, 6.0);
        PRICE_MAP.put(ADULT, 7.0);
        PRICE_MAP.put(DISABLED, 5.5);
    }

    public static double getPrice(Seat_Type type) {
        if(type==null) {
            return 7.0;
        }
        return PRICE_MAP.get(type);
    }

    @ManyToOne
    private Reservation reservation;

    private int number;

    private Seat_Type type;

    public Seat_Type getType() {
        return type;
    }

    public void setType(Seat_Type type) {
        this.type=type;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number=number;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation=reservation;
    }
}
