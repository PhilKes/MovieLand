package com.phil.movieland.data.entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
public class MovieShow extends EntityWithId{

    @ManyToOne(fetch=FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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

    public MovieShowInfo getInfo(){
        return new MovieShowInfo()
                .setId(getId())
                .setDate(getDate());
    }

    public class MovieShowInfo{
        private Integer id;
        @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME)
        private Date date;

        public MovieShowInfo() {
        }

        public Integer getId() {
            return id;
        }

        public MovieShowInfo setId(Integer id) {
            this.id=id;
            return this;
        }

        public Date getDate() {
            return date;
        }

        public MovieShowInfo setDate(Date date) {
            this.date=date;
            return this;
        }
    }
}
