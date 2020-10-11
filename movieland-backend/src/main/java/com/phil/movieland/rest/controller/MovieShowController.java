package com.phil.movieland.rest.controller;

import com.phil.movieland.data.entity.Movie;
import com.phil.movieland.data.entity.MovieShow;
import com.phil.movieland.rest.service.MovieService;
import com.phil.movieland.rest.service.MovieShowService;
import com.phil.movieland.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * REST Controller for MovieShows
 */
@RestController
@RequestMapping("/api/shows")
public class MovieShowController {
    private final MovieShowService movieShowService;
    private final MovieService movieService;
    private Logger log=LoggerFactory.getLogger(MovieShowController.class);

    @Autowired
    public MovieShowController(MovieShowService movieShowService, MovieService movieService) {
        this.movieShowService=movieShowService;
        this.movieService=movieService;
    }

    @GetMapping
    public Collection<MovieShow> getMoviesShows(
            @RequestParam(value="date", required=true) String dateString) {
        List<MovieShow> shows=null;
        Date date=DateUtils.createDateFromDateString(dateString);
        log.info("Query for " + date);
        shows=movieShowService.getShowsForDate(date,false);
        return shows;
    }

    @GetMapping("/infos")
    public Collection<MovieDetails> getMoviesShowsInfo(
            @RequestParam(value="date", required=true) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) Date date) {
        log.info("Query for " + date);
        List<MovieShow> shows=movieShowService.getShowsForDate(date,false);
        Map<Integer,List<MovieShow>> showMap=shows.stream().collect(groupingBy(show->show.getMovie().getId()));
        //List<Movie> movies= movieService.getMoviesFromShows(new ArrayList<>(showMap.keySet()));
        List<MovieDetails> showInfos=new ArrayList<>();
        showMap.keySet().forEach(movId->{
            Movie movie= showMap.get(movId).get(0).getMovie();
            MovieDetails info= new MovieDetails()
                    .setMovId(movId)
                    .setName(movie.getName())
                    .setPosterUrl(movie.getPosterUrl())
                    .setDate(movie.getDate())
                    .setShows(showMap.get(movId));
            showInfos.add(info);
        });
        return showInfos;
    }
    public class MovieDetails {
        Integer movId;
        String name;
        @DateTimeFormat(iso= DateTimeFormat.ISO.DATE_TIME)
        Date date;
        String posterUrl;

        List<MovieShow.MovieShowInfo> shows;

        public MovieDetails() {
        }

        public Integer getMovId() {
            return movId;
        }

        public MovieDetails setMovId(Integer movId) {
            this.movId=movId;
            return this;
        }

        public String getName() {
            return name;
        }

        public MovieDetails setName(String name) {
            this.name=name;
            return this;

        }

        public Date getDate() {
            return date;
        }

        public MovieDetails setDate(Date date) {
            this.date=date;
            return this;

        }

        public String getPosterUrl() {
            return posterUrl;
        }

        public MovieDetails setPosterUrl(String posterUrl) {
            this.posterUrl=posterUrl;
            return this;

        }

        public List<MovieShow.MovieShowInfo> getShows() {
            return shows;
        }

        public MovieDetails setShows(List<MovieShow> shows) {
            this.shows=shows.stream().map(MovieShow::getInfo).collect(Collectors.toList());
            return this;
        }
    }


    @GetMapping("/movies/{id}")
    public ResponseEntity<Collection<MovieShow>> getMovieShows(@PathVariable Integer id,
                                                               @RequestParam(value="date", required=false) String dateString) {
        List<MovieShow> shows=null;
        Date date=dateString==null ? new Date() : DateUtils.createDateFromDateString(dateString);
        log.info("Query for " + date);
        Optional<Movie> movie=movieService.queryMovie(id);
        if(!movie.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        //shows=movieShowService.getShowsForMovieDate(movie.get(),dateString);
        shows=movieShowService.getMovieShowsForWeekOf(movie.get(), date);
        return ResponseEntity.ok(shows);
    }

    @GetMapping("/week")
    public Collection<MovieShow> getMovieShowsWeek() {
        List<MovieShow> shows=null;
        shows=movieShowService.getShowsForWeekOf(new Date());
        return shows;
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieShow> getShow(@PathVariable Integer id) {
        Optional<MovieShow> show=movieShowService.queryShow(id);
        return show.map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<MovieShow> postMovieShow(@RequestBody MovieShow show) throws URISyntaxException {
        log.info("Post Show( MovId:" + show.getId() + " Date: " + show.getDate());
        MovieShow result=movieShowService.saveShow(show);
        return ResponseEntity.created(new URI("/api/show/" + result.getId()))
                .body(result);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    ResponseEntity<MovieShow> updateShow(@Valid @RequestBody MovieShow show) {
        MovieShow result=movieShowService.saveShow(show);
        return ResponseEntity.ok().body(result);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteShow(@PathVariable Integer id) {
        movieShowService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping
    public ResponseEntity<?> deleteShows() {
        movieShowService.deleteAllMovieShows();
        return ResponseEntity.ok().build();
    }
}
