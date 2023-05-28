package com.example.omdb.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.omdb.service.MoviesService;

import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("movies")
@Validated
@RequiredArgsConstructor
public class MoviesController {

    @NonNull
    private final MoviesService moviesService;

    @GetMapping("/{imdbId}")
    public ResponseEntity<Object> getById(@RequestParam String apiKey, @NonNull @PathVariable String imdbId) {
        return moviesService.fetchMovieById(getMovieKey(apiKey, imdbId));
    }

    @GetMapping("/")
    public ResponseEntity<Object> fetch(@NonNull @Valid MovieSearchParameters searchParameters) {
        return moviesService.fetchMoviesBySearchCriteria(searchParameters);
    }

    @GetMapping("/favorites")
    public ResponseEntity<Object> fetchFavorites(@RequestParam String apiKey) {
        if (apiKey.isEmpty()) {
            throw new IllegalArgumentException("The apiKey cannot be an empty string.");
        }
        return moviesService.fetchFavorites(apiKey);
    }

    @GetMapping("/favorites/{imdbId}")
    public ResponseEntity<Object> fetchFavorite(@RequestParam String apiKey, @NonNull @PathVariable String imdbId) {
        return moviesService.fetchFavorite(getMovieKey(apiKey, imdbId));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/favorites/{imdbId}")
    public ResponseEntity<Object> addFavorite(@RequestParam String apiKey, @NonNull @PathVariable String imdbId) {
        return moviesService.addFavorite(getMovieKey(apiKey, imdbId));
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/favorites/{imdbId}")
    public ResponseEntity<Object> removeFavorite(@RequestParam String apiKey, @NonNull @PathVariable String imdbId) {
        return moviesService.removeFavorite(getMovieKey(apiKey, imdbId));
    }

    private MovieKey getMovieKey(@NonNull String apiKey, @NonNull String imdbId) {
        if (apiKey.isEmpty()) {
            throw new IllegalArgumentException("The apiKey cannot be an empty string.");
        }
        if (imdbId.isEmpty()) {
            throw new IllegalArgumentException("The imdbId cannot be an empty string.");
        }
        return new MovieKey(apiKey, imdbId);
    }
}
