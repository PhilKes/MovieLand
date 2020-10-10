package com.phil.movieland.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.phil.movieland.utils.DateUtils;
import com.phil.movieland.utils.TmdbApiService;
import info.movito.themoviedbapi.model.MovieDb;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Movie extends EntityWithId{

    private String name;

    private Date date;

    private String description;

    @Nullable
    private String posterUrl;

    @Nullable
    private Long length;

    private Long tmdbId;

    //Additional info, actors,... from TMDB
    @Transient //Ignore for Persistence in Database
    @JsonIgnore
    private MovieDb tmdbMovie;

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length=length;
    }

    public void setTmdbId(Long tmdbId) {
        this.tmdbId=tmdbId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name=name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date=date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description=description;
    }

    public Long getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(long tmdbId) {
        this.tmdbId=tmdbId;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl=posterUrl;
    }

    public MovieDb getTmdbMovie() {
        return tmdbMovie;
    }

    public void setTmdbMovie(MovieDb tmdbMovie) {
        this.tmdbMovie=tmdbMovie;
        this.tmdbId=(long) tmdbMovie.getId();
        this.posterUrl=TmdbApiService.POSTER_BASE_URL + tmdbMovie.getPosterPath();
        this.length=(long) tmdbMovie.getRuntime();
        //TODO pipe description

        this.description=tmdbMovie.getOverview();
        if(description.length()>120) {
            description=description.substring(0, 120) + "...";
        }
        if(tmdbMovie.getReleaseDate()!=null && !tmdbMovie.getReleaseDate().isEmpty()) {
            this.date=DateUtils.createDateFromDateString(tmdbMovie.getReleaseDate());
        }
        this.name=tmdbMovie.getTitle();
    }
}
