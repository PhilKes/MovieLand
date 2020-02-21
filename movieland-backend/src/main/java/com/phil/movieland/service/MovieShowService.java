package com.phil.movieland.service;

import com.phil.movieland.data.entity.Movie;
import com.phil.movieland.data.entity.MovieShow;
import com.phil.movieland.data.repository.MovieRepository;
import com.phil.movieland.data.repository.MovieShowRepository;
import com.phil.movieland.utils.DateUtils;
import com.phil.movieland.utils.TmdbApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/** Service to interface movieShowRepository */
@Service
public class MovieShowService {

    private final MovieShowRepository movieShowRepository;

    @Autowired
    public MovieShowService(MovieShowRepository movieShowRepository) {
        this.movieShowRepository=movieShowRepository;
    }

    public List<MovieShow> getShowsForMovie(Movie movie){
        List<MovieShow> shows= movieShowRepository.findAllByMovId(movie.getMovId());
        for(MovieShow show : shows) {
            System.out.println("Show at: "+show.getDate());
        }
        return shows;
    }

    public List<MovieShow> getShowsForMovieDate(Movie movie,String dateString){
        Date date=DateUtils.createDateFromDateString(dateString);
        Date[] betweenDates=getBetweenDates(date);
        List<MovieShow> shows= movieShowRepository.findAllByMovIdAndDateBetween(movie.getMovId(),betweenDates[0],betweenDates[1]);
        for(MovieShow show : shows) {
            System.out.println("Show at: "+show.getDate());
        }
        return shows;
    }

    public void postMovieShow(Long movieId,Date date){
        MovieShow show=new MovieShow();
        show.setMovId(movieId);
        show.setDate(date);

        System.out.println("ADD NEW SHOW");
        System.out.println("MovId: "+show.getMovId());
        System.out.println("DateTime: "+show.getDate());
        movieShowRepository.save(show);
    }

    public void deleteMovieShow(Long showid){
        System.out.println("Deleting: "+showid);
        movieShowRepository.deleteById(showid);
    }
     public void deleteAllMovieShows(){
            System.out.println("Deleting all Shows");
            movieShowRepository.deleteAll();
        }

    public List<MovieShow> getShowsForDate(Date date) {
        System.out.println("Getting MovieShows for: "+date);
        Date[] betweenDates=getBetweenDates(date);
        List<MovieShow> shows=movieShowRepository.findAllByDateBetween(betweenDates[0],betweenDates[1]);
        return shows;
    }

    /**
     * Return dates from today 00:00 and 23:59
     */
    private Date[] getBetweenDates(Date date){
        Date dateStart=new Date(date.getTime());
        dateStart.setHours(0);
        dateStart.setMinutes(0);
        dateStart.setSeconds(0);
        Date dateEnd=new Date(date.getTime());
        dateEnd.setHours(23);
        dateEnd.setMinutes(59);
        dateEnd.setSeconds(59);
        return new Date[]{dateStart,dateEnd};
    }

    public MovieShow saveShow(MovieShow show) {
        return movieShowRepository.save(show);
    }

    public Optional<MovieShow> queryShow(Long id) {
        Optional<MovieShow> show=movieShowRepository.findById(id);
        return show;
    }

    public void deleteById(Long id) {
        movieShowRepository.deleteById(id);
    }

    public List<MovieShow> getShowsForWeekOf(Date date) {
        date.setHours(0);
        date.setMinutes(0);
        date.setSeconds(0);
        Calendar in7Days=Calendar.getInstance();
        in7Days.setTime(date);
        in7Days.set(Calendar.HOUR_OF_DAY, 23);
        in7Days.set(Calendar.MINUTE, 59);
        in7Days.set(Calendar.SECOND,59);
        in7Days.add(Calendar.DATE, 7);
        System.out.println("Looking for shows between: " + date + " and " + in7Days.getTime());
        return movieShowRepository.findAllByDateBetween(date, in7Days.getTime());
    }

    public void saveShows(List<MovieShow> movieShows) {
        movieShows.stream().sorted(Comparator.comparing(MovieShow::getDate)).forEach(this::saveShow);
    }

    public List<MovieShow> getShowsForBetween(Date from, Date until) {
        return movieShowRepository.findAllByDateBetween(from,until);
    }
}
