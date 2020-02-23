package com.phil.movieland.data.repository;

import com.phil.movieland.data.entity.Reservation;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface ReservationRepository extends CrudRepository<Reservation,Long> {
    List<Reservation> findAllByShowId(long showId);
    List<Reservation> findAll();

    List<Reservation> findAllByUserId(long userId);

    Long deleteAllByShowIdIn(List<Long> showIds);
}
