package com.phil.movieland.data.entity;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
public class MovieShow extends EntityWithId{

    @ManyToOne
    private Movie movie;

    @DateTimeFormat(iso= DateTimeFormat.ISO.DATE_TIME)
    private Date date;

    //TODO list of seats, different cinemas
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date=date;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie=movie;
    }
}
