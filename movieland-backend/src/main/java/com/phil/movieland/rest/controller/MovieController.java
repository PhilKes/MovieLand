package com.phil.movieland.rest.controller;

import com.phil.movieland.data.entity.Movie;
import com.phil.movieland.rest.service.MovieService;
import com.phil.movieland.rest.service.MovieShowService;
import com.phil.movieland.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/** REST Controller for Movies*/
@RestController
@RequestMapping("/api/movies")
public class MovieController {
    private final MovieService movieService;
    private final MovieShowService movieShowService;
    private Logger log=LoggerFactory.getLogger(MovieController.class);

    @Autowired
    public MovieController(MovieService movieService,MovieShowService movieShowService) {
        this.movieService=movieService;
        this.movieShowService=movieShowService;
    }

    @GetMapping
    public Collection<Movie> getMovies(
            @RequestParam(value="name",required=false)String search){
        List<Movie> movies= null;
        if(null==search) {
            //TODO PAGING
            log.info("No query entered");
            movies=movieService.getAllMovies();
        }else{
            log.info("Searched for: " + search);
            movies=movieService.queryAllMovies(search);
        }
        return movies;
    }

    @GetMapping("/page/{page}")
    public ResponseEntity<Collection<Movie>> getMoviesPaged(
            @RequestParam(value="name", required=false) String search,
            @PathVariable(value="page") Integer page) {
        HttpHeaders responseHeaders=new HttpHeaders();
        Slice<Movie> slice;
        if(null==search) {
            log.info("No query entered");
            slice=movieService.getAllMoviesPaged(page, 10);

        }
        else {
            log.info("Searched for: " + search);
            slice=movieService.queryAllMoviesPaged(search, page, 10);
        }
        if(slice.hasNext()) {
            responseHeaders.set("hasMore", "true");
        }
        else {
            responseHeaders.set("hasMore", "false");
        }
        return ResponseEntity.ok().headers(responseHeaders).body(slice.getContent());
    }

    @GetMapping("/ids")
    public Map<Integer, Movie> getMoviesList(
            @RequestParam(value="ids") List<Integer> movIds) {
        List<Movie> movies=movieService.queryMoviesByIds(movIds);
        Map<Integer, Movie> moviesMap=movies.stream()
                .collect(Collectors.toMap(Movie::getId, Function.identity()));
        log.info("Movies by ids:", Utils.printMap(moviesMap));
        return moviesMap;
    }


    @GetMapping("/tmdb")
    public Collection<Movie> getTmdbMovies(
            @RequestParam(value="name",required=false)String search){
        List<Movie> movies= null;
        if(null==search) {
            log.info("No query entered");
            movies=new ArrayList<>();
        }else{
            log.info("Searched for: " + search);
            movies=movieService.queryTmdbMovies(search);
        }
        return movies;
    }

    @GetMapping("/tmdb/top")
    public Collection<Movie> getTmdbTopMovies(){
        return movieService.querTmdbTop10Movies();
    }

    @GetMapping("/tmdb/images")
    public HashMap<Integer, String> getTmdbImages(@RequestParam(value="ids") List<Integer> movIds) {
        HashMap<Integer, String> backdrops=new HashMap<>();
        log.info("Requesting backdrops...");
        for(Integer movId : movIds) {
            //log.info("Mov: "+movId);
            backdrops.put(movId, movieService.getBackdrop(movId));
        }
        return backdrops;
    }

    @GetMapping("/{id}")
    ResponseEntity<Movie> getMovie(@PathVariable Integer id) {
        Optional<Movie> movie = movieService.queryMovie(id);
        return movie.map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/trailer/{movId}")
    ResponseEntity<String> getMovieTrailer(@PathVariable Integer movId) {
        Optional<String> trailer=movieService.getTrailer(movId);
        return trailer.map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    ResponseEntity<?> createMovie(@RequestBody Movie movie) throws URISyntaxException {
        try {
            Movie result=movieService.saveMovie(movie);
            return ResponseEntity.created(new URI("/api/movie/" + result.getId()))
                    .body(result);
        }catch(Exception e){
            e.getMessage();
            return ResponseEntity.badRequest().body("{\"msg\":\""+e.getMessage()+"\"}");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    ResponseEntity<Movie> updateMovie(@Valid @RequestBody Movie movie) {
        Movie result =null;
        try {
            result=movieService.saveMovie(movie);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().body(result);
    }

    //TODO PREAUTHORIZATION??
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMovie(@PathVariable Integer id) {
        movieService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping
    public ResponseEntity<?> deleteAllMovies() {
        movieService.deleteAll();
        return ResponseEntity.ok().build();
    }

}
