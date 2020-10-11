package com.phil.movieland.data.repository;

import com.phil.movieland.data.entity.Movie;
import com.phil.movieland.data.entity.MovieShow;
import com.phil.movieland.rest.controller.MovieShowController;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovieShowRepository extends CrudRepository<MovieShow,Integer> {
    Optional<MovieShow> findById(int movid);
    List<MovieShow> findAllByOrderByDate();
    List<MovieShow> findAllById(int movid);

    List<MovieShow> findAllByMovieIdAndDateBetweenOrderByDate(int movid, Date dateStart, Date dateEnd);
    List<MovieShow> findAllByDateBetween(Date dateStart,Date dateEnd);

    Long deleteAllByIdIn(List<Integer> showIds);
}
