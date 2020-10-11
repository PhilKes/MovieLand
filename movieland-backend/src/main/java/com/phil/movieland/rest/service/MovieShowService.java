package com.phil.movieland.rest.service;

import com.phil.movieland.data.entity.Movie;
import com.phil.movieland.data.entity.MovieShow;
import com.phil.movieland.data.repository.MovieShowRepository;
import com.phil.movieland.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service to interface movieShowRepository
 */
@Service
public class MovieShowService {

    private final MovieShowRepository movieShowRepository;

    private Logger log=LoggerFactory.getLogger(MovieShowService.class);

    @Autowired
    public MovieShowService(MovieShowRepository movieShowRepository) {
        this.movieShowRepository=movieShowRepository;
    }

    public List<MovieShow> getShowsForMovie(Movie movie) {
        List<MovieShow> shows=movieShowRepository.findAllById(movie.getId());
        for(MovieShow show : shows) {
            log.info("Show at: " + show.getDate());
        }
        return shows;
    }

    public List<MovieShow> getShowsForMovieDate(Movie movie, String dateString) {
        return getShowsForMovieDate((int) movie.getId(), dateString);
    }

    public List<MovieShow> getShowsForMovieDate(Integer movId, String dateString) {
        Date date=DateUtils.createDateFromDateString(dateString);
        Date[] betweenDates=getBetweenDates(date);
        List<MovieShow> shows=movieShowRepository.findAllByMovieIdAndDateBetweenOrderByDate(movId, betweenDates[0], betweenDates[1]);
        for(MovieShow show : shows) {
            log.info("Show at: " + show.getDate());
        }
        return shows;
    }

    public void postMovieShow(Integer movieId, Date date) {
        MovieShow show=new MovieShow();
        show.setId(movieId);
        show.setDate(date);

        log.info("ADD NEW SHOW");
        log.info("MovId: " + show.getId());
        log.info("DateTime: " + show.getDate());
        movieShowRepository.save(show);
    }

    public void deleteMovieShow(Integer showid) {
        log.info("Deleting: " + showid);
        movieShowRepository.deleteById(showid);
    }

    public void deleteAllMovieShows() {
        log.info("Deleting all Shows");
        movieShowRepository.deleteAll();
    }

    public List<MovieShow> getShowsForDate(Date date, boolean groupByMovId) {
        log.info("Getting MovieShows for: " + date);
        Date[] betweenDates=getBetweenDates(date);
        List<MovieShow> shows=groupByMovId==false ?
                movieShowRepository.findAllByDateBetween(betweenDates[0], betweenDates[1])
                : movieShowRepository.findAllByDateBetween(betweenDates[0], betweenDates[1]);

        return shows;
    }

    /**
     * Return dates from today 00:00 and 23:59
     */
    private Date[] getBetweenDates(Date date) {
        Date dateStart=new Date(date.getTime());
        dateStart.setHours(0);
        dateStart.setMinutes(0);
        dateStart.setSeconds(0);
        Date dateEnd=new Date(date.getTime());
        dateEnd.setHours(23);
        dateEnd.setMinutes(59);
        dateEnd.setSeconds(59);
        return new Date[]{dateStart, dateEnd};
    }

    public MovieShow saveShow(MovieShow show) {
        return movieShowRepository.save(show);
    }

    public Optional<MovieShow> queryShow(Integer id) {
        Optional<MovieShow> show=movieShowRepository.findById(id);
        return show;
    }

    public void deleteById(Integer id) {
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
        in7Days.set(Calendar.SECOND, 59);
        in7Days.add(Calendar.DATE, 7);
        log.info("Looking for shows between: " + date + " and " + in7Days.getTime());
        return movieShowRepository.findAllByDateBetween(date, in7Days.getTime());
    }

    public List<MovieShow> getMovieShowsForWeekOf(Movie movie, Date date) {
        date.setHours(0);
        date.setMinutes(0);
        date.setSeconds(0);
        Calendar in7Days=Calendar.getInstance();
        in7Days.setTime(date);
        in7Days.set(Calendar.HOUR_OF_DAY, 23);
        in7Days.set(Calendar.MINUTE, 59);
        in7Days.set(Calendar.SECOND, 59);
        in7Days.add(Calendar.DATE, 7);
        return movieShowRepository.findAllByMovieIdAndDateBetweenOrderByDate(movie.getId(), date, in7Days.getTime());
    }

    public void saveShows(List<MovieShow> movieShows) {
        movieShows.stream().sorted(Comparator.comparing(MovieShow::getDate)).forEach(this::saveShow);
    }

    public List<MovieShow> getShowsForBetween(Date from, Date until) {
        return movieShowRepository.findAllByDateBetween(from, until);
    }

    public long deleteShowsByIds(List<Integer> showIds) {
        return movieShowRepository.deleteAllByIdIn(showIds);
    }


}
