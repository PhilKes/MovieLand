package com.phil.movieland.rest.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.phil.movieland.auth.jwt.entity.Role;
import com.phil.movieland.auth.jwt.entity.User;
import com.phil.movieland.data.entity.*;
import com.phil.movieland.data.repository.SeatRepository;
import com.phil.movieland.rest.controller.UserController;
import com.phil.movieland.rest.tasks.RunnableWithProgress;
import com.phil.movieland.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toMap;

@Service
public class StatisticsService {

    private final MovieShowService movieShowService;
    private final UserController userController;
    private final MovieService movieService;
    private final SeatRepository seatRepository;
    private final ReservationService reservationService;

    private final static HashMap<String, Statistics> precalculatedSummaries=new HashMap<>();

    private Logger log=LoggerFactory.getLogger(StatisticsService.class);

    @Autowired
    public StatisticsService(SeatRepository seatRepository, ReservationService reservationService,
                             MovieShowService movieShowService, MovieService movieService,
                             UserController userController) {
        this.seatRepository=seatRepository;
        this.reservationService=reservationService;
        this.movieShowService=movieShowService;
        this.movieService=movieService;
        this.userController=userController;
    }

    public RunnableWithProgress generateShowsBetweenTask(Date from, Date until) {
        return generateShowsBetweenTask(from, until, 10, 4);
    }

    /**
     * Return Runnable for Show Generation Task
     */
    public RunnableWithProgress generateShowsBetweenTask(Date from, Date until, int moviesPerDay, int showsPerMovie) {
        return new RunnableWithProgress() {
            @Override
            public void run() {
                setProgress(0);
                long days=ChronoUnit.DAYS.between(from.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                        , until.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                setProgressMax((int) days + 1);

                Calendar countDate=Calendar.getInstance();
                countDate.setTime(from);
                /** Generate shows for each day until Date until is reached*/
                Random rand=new Random();
                /** Get random Movies*/
                List<Movie> movies=movieService.getAllMovies().stream()
                        .collect(Collectors.collectingAndThen(Collectors.toList(), collected -> {
                            Collections.shuffle(collected);
                            return collected.stream();
                        }))
                        .limit(moviesPerDay)
                        .collect(Collectors.toList());

                while(countDate.getTime().before(until)) {
                    /** For each movie generate shows every day */
                    for(Movie movie : movies) {
                        List<MovieShow> movieShows=new ArrayList<>();
                        List<Integer> hours=new ArrayList<>();
                        /** Between 2 - 5 shows every day per movie*/
                        //int amtShows=2 + rand.nextInt(3);
                        int amtShows=showsPerMovie;
                        for(int i=0; i<amtShows; i++) {
                            Calendar showTime=Calendar.getInstance();
                            showTime.setTime(countDate.getTime());
                            MovieShow movieShow=new MovieShow();
                            movieShow.setId(movie.getId());
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
                    log.info("Generated shows for: " + countDate.getTime());
                    countDate.add(Calendar.DATE, 1);
                    incProgress(1);
                }
                precalculatedSummaries.clear();
            }
        };
    }

    /**
     * Return Runnable for Reseravtion Generation Task
     */
    public RunnableWithProgress generateReservationsBetweenTask(Date from, Date until, int resPerShow) {
        return new RunnableWithProgress() {
            @Override
            public void run() {
                setProgress(0);
                log.info("Start generation of reservations for: " + DateUtils.getDateStringFromDate(from) + " until: " + DateUtils.getDateStringFromDate(until));
                //TODO List<MovieShow> shows=movieShowService.getShowsForDateBetween(from,until);
                // Iterate through all Shows, not the days -> change ProgressMax
                /** Generate reservations for each show until Date until is reached*/
                Random rand=new Random();
                List<User> userIds=userController.getAllUserOfRole(Role.RoleName.ROLE_USER);
                // while(countDate.getTime().before(until)) {
                List<MovieShow> shows=movieShowService.getShowsForBetween(from, until);
                setProgressMax(shows.size());
                /** For each show generate reservations */
                for(MovieShow show : shows) {
                    List<ReservationWithSeats> reservations=new ArrayList<>();
                    List<User> users=new ArrayList<>(userIds);
                    /** Get All free seats*/
                    List<Seat> showsTakenSeats=reservationService.getAllSeatsOfShow(show.getId());
                    List<Integer> freeSeats=IntStream.range(0, 160).boxed().collect(Collectors.toList());
                    freeSeats.removeAll(showsTakenSeats.stream()
                            .map(Seat::getNumber).collect(Collectors.toList()));
                    /** Between 2 - 4 reservations per show*/
                    //int amtReservations=2 + rand.nextInt(2);
                    int amtReservations=Math.min(resPerShow, userIds.size());

                    for(int i=0; i<amtReservations; i++) {
                        Reservation reservation=new Reservation();
                        reservation.setId(show.getId());
                        User user=null;
                        /** Determine user (never 2 Reservations of same user for same show)*/
                        user=users.remove(rand.nextInt(users.size()));

                        reservation.setUser(user);
                        ReservationWithSeats reservationWithSeats=new ReservationWithSeats();
                        reservationWithSeats.setReservation(reservation);
                        /** Determine Seats of reservation*/
                        List<Seat> seatList=new ArrayList<>();
                        // do {
                        //seatList.clear();
                        int amtSeats=2 + rand.nextInt(4);
                        if(amtSeats>freeSeats.size()) {
                            break;
                        }
                        for(int j=0; j<amtSeats; j++) {
                            Seat seat=new Seat();
                            seat.setId(reservation.getId());
                            seat.setNumber(freeSeats.remove(0));
                            int type=rand.nextInt(11);
                            if(type<=4) {
                                seat.setType(Seat.Seat_Type.ADULT);
                            }
                            else if(type<=7) {
                                seat.setType(Seat.Seat_Type.STUDENT);
                            }
                            else if(type<=9) {
                                seat.setType(Seat.Seat_Type.CHILD);
                            }
                            else {
                                seat.setType(Seat.Seat_Type.DISABLED);
                            }
                            seatList.add(seat);
                        }
                        //} while(!reservationService.areSeatsAvailable(show.getShowId(), seatList));
                        reservationWithSeats.setSeats(seatList);
                        reservations.add(reservationWithSeats);
                    }
                    reservationService.saveReservationsWithSeats(reservations);
                    incProgress(1);
                }

                //}
                log.info("Finished generation of reservations");
                precalculatedSummaries.clear();
            }
        };
    }

    public void generateShowsBetween(Date from, Date until) {
        generateShowsBetween(from, until, 10, 4);
    }

    public void generateShowsBetween(Date from, Date until, int moviesPerDay, int showsPerMovie) {
        Calendar countDate=Calendar.getInstance();
        countDate.setTime(from);
        /** Generate shows for each day until Date until is reached*/
        Random rand=new Random();
        /** Get random Movies*/
        List<Movie> movies=movieService.getAllMovies().stream()
                .collect(Collectors.collectingAndThen(Collectors.toList(), collected -> {
                    Collections.shuffle(collected);
                    return collected.stream();
                }))
                .limit(moviesPerDay)
                .collect(Collectors.toList());
        ;

        while(countDate.getTime().before(until)) {
            /** For each movie generate shows every day */
            for(Movie movie : movies) {
                List<MovieShow> movieShows=new ArrayList<>();
                List<Integer> hours=new ArrayList<>();
                /** Between 2 - 5 shows every day per movie*/
                //int amtShows=2 + rand.nextInt(3);
                int amtShows=showsPerMovie;
                for(int i=0; i<amtShows; i++) {
                    Calendar showTime=Calendar.getInstance();
                    showTime.setTime(countDate.getTime());
                    MovieShow movieShow=new MovieShow();
                    movieShow.setId(movie.getId());
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
            log.info("Generated shows for: " + countDate.getTime());
            countDate.add(Calendar.DATE, 1);
        }
        precalculatedSummaries.clear();
    }

    public void generateReservationsBetween(Date from, Date until) {
        generateReservationsBetween(from, until, 5);
    }

    public void generateReservationsBetween(Date from, Date until, Integer resPerShow) {
        Calendar countDate=Calendar.getInstance();
        countDate.setTime(from);
        /** Generate reservations for each show until Date until is reached*/
        Random rand=new Random();
        List<User> userIds=userController.getAllUserOfRole(Role.RoleName.ROLE_USER);
        while(countDate.getTime().before(until)) {
            List<MovieShow> shows=movieShowService.getShowsForDate(countDate.getTime(),false);
            /** For each show generate reservations */
            for(MovieShow show : shows) {
                List<ReservationWithSeats> reservations=new ArrayList<>();
                List<User> users=new ArrayList<>();
                /** Between 2 - 4 reservations per show*/
                //int amtReservations=2 + rand.nextInt(2);
                int amtReservations=resPerShow;
                for(int i=0; i<amtReservations; i++) {
                    Calendar showTime=Calendar.getInstance();
                    showTime.setTime(countDate.getTime());
                    Reservation reservation=new Reservation();
                    reservation.setId(show.getId());
                    User user=null;
                    while(true) {
                        /** Determine user (never 2 Reservations of same user for same show)*/
                        user=userIds.get(rand.nextInt(userIds.size()));
                        final User fuser=user;
                        if(users.stream().noneMatch(u -> u.getId()==fuser.getId())) {
                            break;
                        }
                    }
                    users.add(user);
                    reservation.setUser(user);
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
                            seat.setId(reservation.getId());
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
                    } while(!reservationService.areSeatsAvailable(show.getId(), seatList));
                    reservationWithSeats.setSeats(seatList);
                    reservations.add(reservationWithSeats);
                }
                reservationService.saveReservationsWithSeats(reservations);
            }
            log.info("Generated reservations for: " + countDate.getTime());
            countDate.add(Calendar.DATE, 1);
        }
        precalculatedSummaries.clear();

    }

    //TODO speed up with sql
    public void deleteStatisticsBetween(Date from, Date until) {
        log.info("Deleting Statistics from: " + from + " until " + until);
        List<Integer> showIds=movieShowService.getShowsForBetween(from, until).stream().map(MovieShow::getId).collect(Collectors.toList());
        long deletedRes=reservationService.deleteReservationsOfShows(showIds);
        log.info("Deleted " + deletedRes + " Reservations");
        long deletedShows=movieShowService.deleteShowsByIds(showIds);
        log.info("Deleted " + deletedShows + " Shows");
        precalculatedSummaries.clear();

    }

    /**
     * Calculates income based on reservation from Date to Date until
     */
    public double calculateIncomeBetween(Date from, Date until) {
        List<MovieShow> shows=movieShowService.getShowsForBetween(from, until);
        double sum=0.0;
        for(MovieShow show : shows) {
            List<Seat> seats=seatRepository.findSeatsOfShow(show.getId());
            sum+=seats.stream().mapToDouble(seat -> Seat.getPrice(seat.getType())).sum();
        }
        return sum;
    }

    /**
     * Calculates income based on reservation from Date to Date until
     */
    public Statistics calculateStatistics(Date from, Date until) {
        String summaryKey=DateUtils.getDateRangeStringFromDate(from, until);
        if(precalculatedSummaries.containsKey(summaryKey)) {
            log.info("Already calculated statistics for: " + from + " until: " + until);
            return precalculatedSummaries.get(summaryKey);
        }
        log.info("Calculating statistics for: " + from + " until: " + until);
        Statistics statistics=new Statistics();
        long amtShows=0, amtMovies=0, amtSeats=0, amtWatchedMins=0, income=0;
        List<MovieShow> shows=movieShowService.getShowsForBetween(from, until);
        HashMap<String, Integer> dailyStats=new HashMap<>();
        Calendar countDate=Calendar.getInstance();
        countDate.setTime(from);
        countDate.set(Calendar.HOUR_OF_DAY, 1);
        while(countDate.getTime().before(until)) {
            dailyStats.put(DateUtils.getDateStringFromDate(countDate.getTime()), 0);
            countDate.add(Calendar.DATE, 1);
        }

        LinkedHashMap<Long, MovieStats> movieGrossing=new LinkedHashMap<>();
        HashMap<Integer, Movie> movies=getMoviesFromShows(shows);
        amtShows=shows.size();
        amtMovies=movies.size();
        for(MovieShow show : shows) {
            List<Seat> seats=seatRepository.findSeatsOfShow(show.getId());
            /** Split seats into Seat Types*/
            Map<Seat.Seat_Type, List<Seat>> seatsMap=seats.stream()
                    .collect(Collectors.groupingBy(s -> s.getType()));
            /** Add amount of seats to types Statistics*/
            seatsMap.entrySet().forEach(seatType ->
                    statistics.addSeatsDistribution(Map.entry(seatType.getKey(), seatType.getValue().size()))
            );

            Calendar showDay=Calendar.getInstance();
            showDay.setTime(show.getDate());
            showDay.set(Calendar.HOUR_OF_DAY,1);
            /*showDay.set(Calendar.MINUTE,0);
            showDay.set(Calendar.SECOND,0);*/
            //showDay.set(Calendar.MILLISECOND,0);
            Date date=showDay.getTime();
            int daySeats=seats.size();
            amtSeats+=daySeats;
            /** Update daily stats*/
            if(!dailyStats.containsKey(DateUtils.getDateStringFromDate(date))) {
                dailyStats.put(DateUtils.getDateStringFromDate(date), daySeats);
            }else{
                dailyStats.put(DateUtils.getDateStringFromDate(date), dailyStats.get(DateUtils.getDateStringFromDate(date)) + daySeats);
            }
            double seatsIncome=seats.stream().mapToDouble(seat -> Seat.getPrice(seat.getType())).sum();
            income+=seatsIncome;
            long movId=show.getId();
            /** Update Movie income stats*/
            if(!movieGrossing.containsKey(movId)) {
                movieGrossing.put(movId, new MovieStats(movId,movies.get(movId).getPosterUrl(),seatsIncome,seats.size()));
            }else{
                movieGrossing.get(movId).addGrossing(seatsIncome);
                movieGrossing.get(movId).addVisitors(seats.size());
            }
            amtWatchedMins+=movies.get(movId).getLength();
        }

        statistics.setAmtMovies(amtMovies);
        statistics.setAmtSeats(amtSeats);
        statistics.setAmtShows(amtShows);
        statistics.setIncome(income);
        statistics.setAmtWatchedMins(amtWatchedMins);
        statistics.setDailyStats(dailyStats);
        statistics.setMovieStats(movieGrossing);
        precalculatedSummaries.put(summaryKey, statistics);
        log.info("Finished calculating statistics for: " + from + " until: " + until);
        return statistics;
    }

    public List<IntermediateStatistic> getIntermediates(Date from, Date until) {
        return seatRepository.findDailyStatisticsBetweenDate(from,until);
    }

    private HashMap<Integer, Movie> getMoviesFromShows(List<MovieShow> shows) {
        HashMap<Integer, Movie> movies=new HashMap<>();
        shows.stream().mapToLong(EntityWithId::getId).distinct().forEach(
                mov -> movieService.queryMovie(mov).ifPresent(movie -> movies.put(movie.getId(), movie))
        );
        return movies;
    }

    public static class Statistics {
        private long amtShows, amtMovies, amtSeats;
        private long amtWatchedMins;
        private long income;
        private HashMap<String, Integer> dailyStats;
        private Map<Seat.Seat_Type, Integer> seatsDistribution;
        private LinkedHashMap<Long, MovieStats> movieStats;


        public Statistics() {
            seatsDistribution=new HashMap<>();
            for(Seat.Seat_Type type : Seat.Seat_Type.values()) {
                seatsDistribution.put(type, 0);
            }
        }

        //TODO SHOW in REACT
        //TODO USE Highest & LowestGrossing to determine bounds for Chart
        public MovieStats getHighestGrossingMovie(){
            return movieStats.values().stream().max(Comparator.comparing(MovieStats::getGrossing)).orElse(new MovieStats());
        }

        public MovieStats getLowestGrossingMovie(){
            return movieStats.values().stream().min(Comparator.comparing(MovieStats::getGrossing)).orElse(new MovieStats());
        }

        public Map<Seat.Seat_Type, Integer> getSeatsDistribution() {
            return seatsDistribution;
        }

        public void setSeatsDistribution(Map<Seat.Seat_Type, Integer> seatsDistribution) {
            this.seatsDistribution=seatsDistribution;
        }

        public void addSeatsDistributions(Map<Seat.Seat_Type, Integer> seatsDistribution) {
            seatsDistribution.entrySet().forEach(entry -> {
                this.seatsDistribution.put(entry.getKey(), this.seatsDistribution.get(entry.getKey()) + entry.getValue());
            });
        }

        public void addSeatsDistribution(Map.Entry<Seat.Seat_Type, Integer> entry) {
            this.seatsDistribution.put(entry.getKey(), this.seatsDistribution.get(entry.getKey()) + entry.getValue());
        }

        @Transient
        @JsonIgnore
        public Map<Long, MovieStats> getMovieStats() {
            return movieStats;
        }

        /** Sorts and sets moveStats Map*/
        public void setMovieStats(LinkedHashMap<Long, MovieStats> movieStats) {
            LinkedHashMap<Long, MovieStats> sorted =  movieStats.entrySet().stream()
                    .sorted((e1,e2)->Double.compare(e1.getValue().getGrossing(),e2.getValue().getGrossing())*-1)
                    .collect(toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (a, b) -> { throw new AssertionError();},
                            LinkedHashMap::new
                    ));
            this.movieStats=sorted;
        }

       /* public List<Integer> getSortedDailyStats(){
            return dailyStats.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey))
                    .map(e->e.getValue()).collect(Collectors.toList());
        }*/

        public HashMap<String, Integer> getDailyStats() {
            return dailyStats;
        }

        public void setDailyStats(HashMap<String, Integer> dailyStats) {
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

    //TODO
    public static class DayStatistic {
        private long amtShows, amtMovies, amtSeats;
        private long amtWatchedMins;
        private long income;
        private Map<Seat.Seat_Type, Integer> seatsDistribution;
        private LinkedHashMap<Long, MovieStats> movieStats;

        public DayStatistic() {
            seatsDistribution=new HashMap<>();
            for(Seat.Seat_Type type : Seat.Seat_Type.values()) {
                seatsDistribution.put(type, 0);
            }
        }

        public DayStatistic(long amtShows, long amtMovies, long amtSeats) {

        }

        //TODO SHOW in REACT
        //TODO USE Highest & LowestGrossing to determine bounds for Chart
        public MovieStats getHighestGrossingMovie() {
            return movieStats.values().stream().max(Comparator.comparing(MovieStats::getGrossing)).orElse(new MovieStats());
        }

        public MovieStats getLowestGrossingMovie() {
            return movieStats.values().stream().min(Comparator.comparing(MovieStats::getGrossing)).orElse(new MovieStats());
        }

        public Map<Seat.Seat_Type, Integer> getSeatsDistribution() {
            return seatsDistribution;
        }

        public void setSeatsDistribution(Map<Seat.Seat_Type, Integer> seatsDistribution) {
            this.seatsDistribution=seatsDistribution;
        }

        public void addSeatsDistributions(Map<Seat.Seat_Type, Integer> seatsDistribution) {
            seatsDistribution.entrySet().forEach(entry -> {
                this.seatsDistribution.put(entry.getKey(), this.seatsDistribution.get(entry.getKey()) + entry.getValue());
            });
        }

        public void addSeatsDistribution(Map.Entry<Seat.Seat_Type, Integer> entry) {
            this.seatsDistribution.put(entry.getKey(), this.seatsDistribution.get(entry.getKey()) + entry.getValue());
        }

        @Transient
        @JsonIgnore
        public Map<Long, MovieStats> getMovieStats() {
            return movieStats;
        }

        /**
         * Sorts and sets moveStats Map
         */
        public void setMovieStats(LinkedHashMap<Long, MovieStats> movieStats) {
            LinkedHashMap<Long, MovieStats> sorted=movieStats.entrySet().stream()
                    .sorted((e1, e2) -> Double.compare(e1.getValue().getGrossing(), e2.getValue().getGrossing()) * -1)
                    .collect(toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (a, b) -> {
                                throw new AssertionError();
                            },
                            LinkedHashMap::new
                    ));
            this.movieStats=sorted;
        }

       /* public List<Integer> getSortedDailyStats(){
            return dailyStats.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey))
                    .map(e->e.getValue()).collect(Collectors.toList());
        }*/

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


    public static class MovieStats {
        private long movId=-1;
        private String posterPath;
        private Double grossing;
        private Integer visitors;

        public MovieStats() {
        }

        public MovieStats(long movId, String posterPath, double grossing, int visitors) {
            this.movId=movId;
            this.posterPath=posterPath;
            this.grossing=grossing;
            this.visitors=visitors;
        }

        public Integer getVisitors() {
            return visitors;
        }

        public void setVisitors(Integer visitors) {
            this.visitors=visitors;
        }

        public void addVisitors(Integer visitors) {
            this.visitors+=visitors;
        }

        public long getMovId() {
            return movId;
        }

        public void setMovId(long movId) {
            this.movId=movId;
        }

        public String getPosterPath() {
            return posterPath;
        }

        public void setPosterPath(String posterPath) {
            this.posterPath=posterPath;
        }

        public Double getGrossing() {
            return grossing;
        }

        public void setGrossing(Double grossing) {
            this.grossing=grossing;
        }

        public void addGrossing(double grossing) {
            this.grossing+=grossing;
        }


    }

    public static class ReservationWithSeats {
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
