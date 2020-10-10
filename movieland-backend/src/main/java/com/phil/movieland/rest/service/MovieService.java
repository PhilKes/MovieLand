package com.phil.movieland.rest.service;

import com.phil.movieland.data.entity.Movie;
import com.phil.movieland.data.repository.MovieRepository;
import com.phil.movieland.utils.TmdbApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service to interface movieRepository + TmdbApi
 */
@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final TmdbApiService tmdbApiService;

    private Logger log=LoggerFactory.getLogger(MovieService.class);

    @Autowired
    public MovieService(MovieRepository movieRepository, TmdbApiService tmdbApiService) {
        this.movieRepository=movieRepository;
        this.tmdbApiService=tmdbApiService;
    }

    public List<Movie> getAllMovies() {
        List<Movie> movies=movieRepository.findAllByOrderByName();
        return loadTmdbMovies(movies);
    }

    public Slice<Movie> getAllMoviesPaged(int page, int size) {
        Slice<Movie> movies=movieRepository.findAllByOrderByName(PageRequest.of(page, size));
        loadTmdbMovies(movies.getContent());
        return movies;
    }

    public List<Movie> queryAllMovies(String queryName) {
        List<Movie> movies=movieRepository.findAllByNameContainsOrderByName(queryName);
        return loadTmdbMovies(movies);
    }

    public Slice<Movie> queryAllMoviesPaged(String queryName, int page, int size) {
        Slice<Movie> movies=movieRepository.findAllByNameContainsOrderByName(queryName, PageRequest.of(page, size));
        loadTmdbMovies(movies.getContent());
        return movies;
    }

    public Optional<Movie> queryMovie(long movieId) {
        Optional<Movie> movie=movieRepository.findById(movieId);
        if(movie.isPresent()) {
            setTmdbData(movie.get(),true);
        }
        return movie;
    }

    public List<Movie> queryTmdbMovies(String name){
        return tmdbApiService.getMoviesFromTmdb(name).stream().map(tmdbMovie->{
           Movie mov= new Movie();
           mov.setTmdbMovie(tmdbMovie);
           return mov;
        }).collect(Collectors.toList());
    }

    public List<Movie> querTmdbTop10Movies(){
        return tmdbApiService.getTop10Movies().stream().map(tmdbMovie -> {
            Movie mov= new Movie();
            mov.setTmdbMovie(tmdbMovie);
            return mov;
        }).collect(Collectors.toList());
    }

    public Movie saveMovie(Movie movie) throws Exception {
        if(movie.getTmdbId()==null) {
            setTmdbData(movie,false);
        }
        Optional<Movie> duplicate=movieRepository.findFirstByTmdbId(movie.getTmdbId());
        if(duplicate.isPresent()) {
            if(duplicate.get().getId()!=movie.getId()) {
                log.info(movie.getName() + " already in Database!");
                throw new Exception(movie.getName()+" is already in the Database!");
            }
        }
        log.info("Saving: " + movie.getName());
        return movieRepository.save(movie);
    }

    public void deleteById(long movieId) {
        movieRepository.deleteById(movieId);
    }

    private void updateMovie(Movie movie) {
        movieRepository.save(movie);
    }

    /**
     * Load infos from TMDB and set in movies
     */
    private List<Movie> loadTmdbMovies(List<Movie> movies) {
        for(Movie movie : movies) {
            setTmdbData(movie,true);
        }
        return movies;
    }

    private void setTmdbData(Movie movie, boolean update) {
        if(movie.getTmdbId()==null) {
            log.info("Updating: " + movie.getName());
            movie.setTmdbMovie(tmdbApiService.getMovieFromTmdb(movie));
            if(update) {
                updateMovie(movie);
            }
        }

    }

    public String getBackdrop(Integer movId) {
        String path=tmdbApiService.getBackdrop(movId);;
        return path;
    }

    public Optional<String> getTrailer(Long movId) {
        Optional<Movie> movie=movieRepository.findById(movId);
        if(movie.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(tmdbApiService.getTrailerURL(movie.get().getTmdbId()));
    }

    public List<Movie> queryMoviesByIds(List<Integer> movIds) {
        return movieRepository.findAllByIdIn(movIds);
    }

    public void deleteAll() {
        movieRepository.deleteAll();
    }
}
