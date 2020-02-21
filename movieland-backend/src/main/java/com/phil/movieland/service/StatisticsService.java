package com.phil.movieland.service;

import com.phil.movieland.auth.AuthenticationController;
import com.phil.movieland.data.entity.Movie;
import com.phil.movieland.data.entity.MovieShow;
import com.phil.movieland.data.entity.Reservation;
import com.phil.movieland.data.entity.Seat;
import com.phil.movieland.data.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    private final MovieShowService movieShowService;
    private final AuthenticationController authenticationController;
    private final MovieService movieService;
    private final SeatRepository seatRepository;
    private final ReservationService reservationService;

    @Autowired
    public StatisticsService(SeatRepository seatRepository, ReservationService reservationService,
                             MovieShowService movieShowService, MovieService movieService,
                             AuthenticationController authenticationController) {
        this.seatRepository=seatRepository;
        this.reservationService=reservationService;
        this.movieShowService=movieShowService;
        this.movieService=movieService;
        this.authenticationController=authenticationController;
    }


    public void generateShowsBetween(Date from, Date until) {
        Calendar countDate=Calendar.getInstance();
        countDate.setTime(from);
        /** Generate shows for each day until Date until is reached*/
        Random rand=new Random();
        List<Movie> movies=movieService.getAllMovies();
        while(countDate.getTime().before(until)) {
            /** For each movie generate shows every day */
            for(Movie movie : movies) {
                List<MovieShow> movieShows=new ArrayList<>();
                List<Integer> hours=new ArrayList<>();
                /** Between 2 - 5 shows every day per movie*/
                int amtShows=2 + rand.nextInt(3);
                for(int i=0; i<amtShows; i++) {
                    Calendar showTime=Calendar.getInstance();
                    showTime.setTime(countDate.getTime());
                    MovieShow movieShow=new MovieShow();
                    movieShow.setMovId(movie.getMovId());
                    int hour=-1;
                    while(true) {
                        /** Between 11-23 h*/
                        hour=11 + rand.nextInt(13);
                        final int fhour=hour;
                        if(hours.stream().noneMatch(h -> h==fhour)) {
                            break;
                        }
                    }
                    hours.add(hour);
                    showTime.set(Calendar.HOUR, hour);
                    movieShow.setDate(showTime.getTime());
                    movieShows.add(movieShow);
                }
                movieShowService.saveShows(movieShows);
            }
            System.out.println("Generated shows for: " + countDate.getTime());
            countDate.add(Calendar.DATE, 1);
        }
    }

    public void generateReservationsBetween(Date from, Date until) {
        Calendar countDate=Calendar.getInstance();
        countDate.setTime(from);
        /** Generate reservations for each show until Date until is reached*/
        Random rand=new Random();
        List<Long> userIds=authenticationController.getAllUserIds();
        while(countDate.getTime().before(until)) {
            List<MovieShow> shows=movieShowService.getShowsForDate(countDate.getTime());
            /** For each show generate reservations */
            for(MovieShow show : shows) {
                List<ReservationWithSeats> reservations=new ArrayList<>();
                List<Long> users=new ArrayList<>();
                /** Between 2 - 4 reservations per show*/
                int amtReservations=2 + rand.nextInt(2);
                for(int i=0; i<amtReservations; i++) {
                    Calendar showTime=Calendar.getInstance();
                    showTime.setTime(countDate.getTime());
                    Reservation reservation=new Reservation();
                    reservation.setShowId(show.getShowId());
                    Long user=-1L;
                    while(true) {
                        /** Determine user (never 2 Reservations of same user for same show)*/
                        user=userIds.get(rand.nextInt(userIds.size()));
                        final long fuser=user;
                        if(users.stream().noneMatch(u -> u==fuser)) {
                            break;
                        }
                    }
                    users.add(user);
                    reservation.setUserId(user);
                    ReservationWithSeats reservationWithSeats=new ReservationWithSeats();
                    reservationWithSeats.setReservation(reservation);
                    /** Determine Seats of reservation*/
                    List<Seat> seatList=new ArrayList<>();
                    do {
                        seatList.clear();
                        int amtSeats=2 + rand.nextInt(4);
                        int startSeat=rand.nextInt(160) - amtSeats;
                        if(startSeat<0) {
                            startSeat=0;
                        }

                        for(int j=0; j<amtSeats; j++) {
                            Seat seat=new Seat();
                            seat.setResId(reservation.getResId());
                            seat.setNumber(startSeat + j);
                            int type=rand.nextInt(4);
                            switch(type) {
                                case 0:
                                    seat.setType(Seat.Seat_Type.CHILD);
                                    break;
                                case 1:
                                    seat.setType(Seat.Seat_Type.STUDENT);
                                    break;
                                case 2:
                                    seat.setType(Seat.Seat_Type.ADULT);
                                    break;
                                case 3:
                                    seat.setType(Seat.Seat_Type.DISABLED);
                                    break;
                                default:
                                    seat.setType(Seat.Seat_Type.ADULT);
                                    break;
                            }
                            seatList.add(seat);
                        }
                    } while(!reservationService.areSeatsAvailable(show.getShowId(), seatList));
                    reservationWithSeats.setSeats(seatList);
                    reservations.add(reservationWithSeats);
                }
                reservationService.saveReservationsWithSeats(reservations);
            }
            System.out.println("Generated reservations for: " + countDate.getTime());
            countDate.add(Calendar.DATE, 1);
        }
    }

    /**
     * Calculates income based on reservation from Date to Date until
     */
    public double calculateIncomeBetween(Date from, Date until) {
        List<MovieShow> shows=movieShowService.getShowsForBetween(from, until);
        double sum=0.0;
        for(MovieShow show : shows) {
            List<Seat> seats=seatRepository.findSeatsOfShow(show.getShowId());
            sum+=seats.stream().mapToDouble(seat -> Seat.getPrice(seat.getType())).sum();
        }
        return sum;
    }

    //TODO MOST WATCHED MOVIE/HIGHEST GROSSING
    //TODO CURVE (see Users Behavior inf react app dashbaord)

    /**
     * Calculates income based on reservation from Date to Date until
     */
    public Statistics calculateStatistics(Date from, Date until) {
        long amtShows=0, amtMovies=0, amtSeats=0, amtWatchedMins=0, income=0;
        List<MovieShow> shows=movieShowService.getShowsForBetween(from, until);
        HashMap<Date, Integer> dailyStats=new HashMap<>();
        HashMap<Long, Movie> movies=getMoviesFromShows(shows);
        amtShows=shows.size();
        amtMovies=movies.size();
        for(MovieShow show : shows) {
            List<Seat> seats=seatRepository.findSeatsOfShow(show.getShowId());
            Calendar showDay=Calendar.getInstance();
            showDay.setTime(show.getDate());
            showDay.set(Calendar.HOUR_OF_DAY,0);
            showDay.set(Calendar.MINUTE,0);
            showDay.set(Calendar.SECOND,0);
            showDay.set(Calendar.MILLISECOND,0);
            Date date=showDay.getTime();
            int daySeats=seats.size();
            amtSeats+=daySeats;
            if(!dailyStats.containsKey(date)) {
                dailyStats.put(date, daySeats);
            }else{
                dailyStats.put(date, dailyStats.get(date)+daySeats);
            }
            income+=seats.stream().mapToDouble(seat -> Seat.getPrice(seat.getType())).sum();
            amtWatchedMins+=movies.get(show.getMovId()).getLength();
        }
        Statistics statistics=new Statistics();
        statistics.setAmtMovies(amtMovies);
        statistics.setAmtSeats(amtSeats);
        statistics.setAmtShows(amtShows);
        statistics.setIncome(income);
        statistics.setAmtWatchedMins(amtWatchedMins);
        statistics.setDailyStats(dailyStats);
        return statistics;
    }

    private HashMap<Long, Movie> getMoviesFromShows(List<MovieShow> shows) {
        HashMap<Long, Movie> movies=new HashMap<>();
        shows.stream().mapToLong(show -> show.getMovId()).distinct().forEach(
                mov -> movieService.queryMovie(mov).ifPresent(movie -> movies.put(movie.getMovId(), movie))
        );
        return movies;
    }

    public static class Statistics {
        private long amtShows, amtMovies, amtSeats;
        private long amtWatchedMins;
        private long income;
        private HashMap<Date,Integer> dailyStats= new HashMap<>();

        public List<Integer> getSortedDailyStats(){
            return dailyStats.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey))
                    .map(e->e.getValue()).collect(Collectors.toList());
        }

        public HashMap<Date, Integer> getDailyStats() {
            return dailyStats;
        }

        public void setDailyStats(HashMap<Date, Integer> dailyStats) {
            this.dailyStats=dailyStats;
        }

        public long getAmtShows() {
            return amtShows;
        }

        public void setAmtShows(long amtShows) {
            this.amtShows=amtShows;
        }

        public long getAmtMovies() {
            return amtMovies;
        }

        public void setAmtMovies(long amtMovies) {
            this.amtMovies=amtMovies;
        }

        public long getAmtSeats() {
            return amtSeats;
        }

        public void setAmtSeats(long amtSeats) {
            this.amtSeats=amtSeats;
        }

        public long getAmtWatchedMins() {
            return amtWatchedMins;
        }

        public void setAmtWatchedMins(long amtWatchedMins) {
            this.amtWatchedMins=amtWatchedMins;
        }

        public long getIncome() {
            return income;
        }

        public void setIncome(long income) {
            this.income=income;
        }
    }

    public class ReservationWithSeats {
        private Reservation reservation;
        private List<Seat> seats;

        public Reservation getReservation() {
            return reservation;
        }

        public void setReservation(Reservation reservation) {
            this.reservation=reservation;
        }

        public List<Seat> getSeats() {
            return seats;
        }

        public void setSeats(List<Seat> seats) {
            this.seats=seats;
        }
    }
}
