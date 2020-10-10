package com.phil.movieland.data.repository;

import com.phil.movieland.data.entity.IntermediateStatistic;
import com.phil.movieland.data.entity.Reservation;
import com.phil.movieland.data.entity.Seat;
import com.phil.movieland.rest.service.StatisticsService;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface SeatRepository extends CrudRepository<Seat, Long> {
    List<Seat> findAllById(long resId);

    void deleteAllById(int resId);

    //@Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Company c WHERE c.name = :companyName")
    @Query("SELECT s FROM Seat s,Reservation r WHERE s.number=:number AND s.id= r.id " +
            "AND r.id = :showId")
    List<Seat> findSeatDuplicates(@Param("number")int number, @Param("showId") int showId);

    @Query("SELECT s FROM Seat s,Reservation r,MovieShow sh WHERE  s.id= r.id " +
            "AND r.id = :showId AND sh.id=:showId ORDER BY s.type")
    List<Seat> findSeatsOfShow(@Param("showId") int showId);

    //TODO
    @Query("SELECT " +
            "new com.phil.movieland.data.entity.IntermediateStatistic(" +
            "ms,m,r,s) " +
            "FROM MovieShow ms, Reservation r, Seat s, Movie m WHERE " +
            "(ms.date BETWEEN :startDate and :endDate) and m.id = ms.id and" +
            " r.id = ms.id and r.id= s.reservation.id GROUP BY YEAR(ms.date)," +
            "MONTH(ms.date),DAY(ms.date)")
    List<IntermediateStatistic> findDailyStatisticsBetweenDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

}
