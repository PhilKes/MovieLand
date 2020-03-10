package com.phil.movieland.data.entity;

import javax.persistence.*;


@Entity
@Table(name="RESERVATION")
public class Reservation {

    @Id
    @Column(name="RESERVATION_ID")
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long resId;

    @Column(name="SHOW_ID")
    private long showId;

    @Column(name="USER_ID")
    private long userId;

    @Column(name="VALIDATED")
    private boolean validated=false;

    @Column(name="TOTAL_SUM")
    private Double totalSum;

    public long getResId() {
        return resId;
    }

    public void setResId(long resId) {
        this.resId=resId;
    }

    public long getShowId() {
        return showId;
    }

    public void setShowId(long showId) {
        this.showId=showId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId=userId;
    }

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
}
