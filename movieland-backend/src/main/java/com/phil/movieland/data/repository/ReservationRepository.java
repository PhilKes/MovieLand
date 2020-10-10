package com.phil.movieland.data.repository;

import com.phil.movieland.data.entity.Reservation;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends CrudRepository<Reservation,Integer> {

    Optional<Reservation> findByIdAndUserId(int resId, long userId);
    List<Reservation> findAllByShowId(int showId);
    List<Reservation> findAll();

    List<Reservation> findAllByUserId(long userId);

    @Query("SELECT r FROM  Reservation r,MovieShow ms WHERE r.id=:userId AND r.id=ms.id " +
            "AND ms.date >= :currDate")
    List<Reservation> findFutureAllByUserId(@Param("userId") long userId, @Param("currDate") Date date);

    Long deleteAllByIdIn(List<Integer> showIds);
}
