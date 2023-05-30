package com.example.omdb.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.omdb.client.OmdbRestClient;
import com.example.omdb.controller.MovieKey;
import com.example.omdb.controller.MovieSearchParameters;
import com.example.omdb.dto.Movie;
import com.example.omdb.dto.MovieResult;
import com.example.omdb.dto.Search;
import com.example.omdb.dto.SearchResult;
import com.google.common.cache.Cache;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Service
@RequiredArgsConstructor
public class MoviesService {

    @NonNull
    private final Cache<String, Set<String>> keysCache;

    @NonNull
    private final Cache<String, Movie> moviesCache;

    @NonNull
    private final OmdbRestClient restClient;

    @Value("${services.movies.search_results_limit:3}")
    private long searchResultsLimit;

    @SneakyThrows
    public ResponseEntity<Object> fetchMovieById(@NonNull MovieKey movieKey) {
        MovieResult movieResult = null;
        if (hasKey(keysCache, movieKey.getApiKey())) {
            Movie movie = moviesCache.getIfPresent(movieKey.getImdbId());
            if (movie != null) {
                movieResult = MovieResult.success(movie);
            }
        }

        if (movieResult == null) {
            movieResult = restClient.fetchMovieById(movieKey);
        }

        if (movieResult.isSuccess()) {
            cacheApiKey(movieKey.getApiKey());
            if (movieResult.getMovie() == null) {
                return ResponseEntity.notFound().build();
            }
            cacheMovie(movieResult.getMovie());
            return ResponseEntity.ok(movieResult.getMovie());
        }

        return ResponseEntity.status(movieResult.getStatus()).body(movieResult.getMessage());
    }

    @SneakyThrows
    public ResponseEntity<Object> fetchMoviesBySearchCriteria(@NonNull MovieSearchParameters searchParameters) {

        SearchResult result = restClient.fetchMoviesBySearchCriteria(searchParameters);
        if (!result.isSuccess()) {
            return ResponseEntity.status(result.getStatus()).body(result.getMessage());
        }

        cacheApiKey(searchParameters.getApiKey());
        List<Movie> movieList = new ArrayList<>();
        for (Search item: result.getItems()) {
            Movie movie = moviesCache.getIfPresent(item.getImdbId());
            if (movie != null) {
                movieList.add(movie);
            } else {
                MovieKey movieKey = new MovieKey(searchParameters.getApiKey(), item.getImdbId());
                MovieResult movieResult = restClient.fetchMovieById(movieKey);

                if (movieResult.isSuccess() && movieResult.getMovie() != null) {
                    cacheMovie(movieResult.getMovie());
                    movieList.add(movieResult.getMovie());
                }
            }

            if (movieList.size() == searchResultsLimit) {
                break;
            }
        }

        return ResponseEntity.ok(movieList);
    }

    public ResponseEntity<Object> fetchFavorite(@NonNull MovieKey movieKey) {
        if (!hasKey(keysCache, movieKey.getApiKey())) {
            return ResponseEntity.notFound().build();
        }

        Movie movie = moviesCache.getIfPresent(movieKey.getImdbId());
        if (movie == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(movie);
    }

    public ResponseEntity<Object> fetchFavorites(@NonNull String apiKey) {
        if (!hasKey(keysCache, apiKey)) {
            return ResponseEntity.notFound().build();
        }

        List<Movie> movieList = new ArrayList<>();
        for (String key: keysCache.getIfPresent(apiKey)) {
            Movie movie = moviesCache.getIfPresent(key);
            if (movie != null) {
                movieList.add(movie);
            } else {
                MovieResult movieResult = restClient.fetchMovieById(new MovieKey(apiKey, key));
                if (movieResult.isSuccess()) {
                    if (movieResult.getMovie() != null) {
                        cacheMovie(movieResult.getMovie());
                    }
                }
            }

            if (movieList.size() == searchResultsLimit) {
                break;
            }
        }
        return ResponseEntity.ok(movieList);
    }

    public ResponseEntity<Object> addFavorite(@NonNull MovieKey movieKey) {
        String imdbId = movieKey.getImdbId();
        if (!hasKey(moviesCache, imdbId)) {
            MovieResult movieResult = restClient.fetchMovieById(movieKey);
            if (movieResult.isSuccess()) {
                if (movieResult.getMovie() != null) {
                    cacheMovie(movieResult.getMovie());
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        }

        Set<String> ids = keysCache.getIfPresent(movieKey.getApiKey());
        if (null == ids) {
            ids = new HashSet<>();
        }
        ids.add(movieKey.getImdbId());
        keysCache.put(movieKey.getApiKey(), ids);
        return ResponseEntity.ok(movieKey.getImdbId());
    }

    public ResponseEntity<Object> removeFavorite(@NonNull MovieKey movieKey) {
        Set<String> ids = keysCache.getIfPresent(movieKey.getApiKey());
        if (null == ids) {
            cacheApiKey(movieKey.getApiKey());
        } else {
            ids.remove(movieKey.getImdbId());
            keysCache.put(movieKey.getApiKey(), ids);
        }

        return ResponseEntity.ok(movieKey.getImdbId());
    }

    private boolean hasKey(@NonNull Cache<?, ?> cache, @NonNull String key) {
        return cache.getIfPresent(key) != null;
    }

    private void cacheApiKey(@NonNull String apiKey) {
        if (!hasKey(keysCache, apiKey)) {
            keysCache.put(apiKey, new HashSet<>());
        }
    }

    private void cacheMovie(@NonNull Movie movie) {
        if (!hasKey(moviesCache, movie.getImdbId())) {
            moviesCache.put(movie.getImdbId(), movie);
        }
    }

}
