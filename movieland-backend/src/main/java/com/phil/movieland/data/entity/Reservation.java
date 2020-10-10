package com.phil.movieland.data.entity;

import com.phil.movieland.auth.jwt.entity.User;
import com.phil.movieland.rest.request.ReservationValidationRequest;

import javax.persistence.*;


@Entity
public class Reservation extends EntityWithId{

    @ManyToOne
    private MovieShow show;

    @ManyToOne
    private User user;

    private boolean validated=false;

    private Double totalSum;

    private ReservationValidationRequest.PaymentMethod method=ReservationValidationRequest.PaymentMethod.CASH;

    @ManyToOne
    private User cashier;

    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean validated) {
        this.validated=validated;
    }

    public Double getTotalSum() {
        return totalSum;
    }

    public void setTotalSum(Double totalSum) {
        this.totalSum=totalSum;
    }

    public ReservationValidationRequest.PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(ReservationValidationRequest.PaymentMethod method) {
        this.method=method;
    }

    public MovieShow getShow() {
        return show;
    }

    public void setShow(MovieShow show) {
        this.show=show;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user=user;
    }

    public User getCashier() {
        return cashier;
    }

    public void setCashier(User cashier) {
        this.cashier=cashier;
    }
}
