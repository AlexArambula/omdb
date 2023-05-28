package com.example.omdb.client;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.omdb.controller.MovieKey;
import com.example.omdb.controller.MovieSearchParameters;
import com.example.omdb.dto.Movie;
import com.example.omdb.dto.MovieResult;
import com.example.omdb.dto.Search;
import com.example.omdb.dto.SearchResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class OmdbRestClient {

    @NonNull
    private final URI baseUrl;

    @NonNull
    private final ObjectMapper objectMapper;

    @NonNull
    private final RestTemplate restTemplate;

    public MovieResult fetchMovieById(@NonNull MovieKey movieKey) {
        return getMovieResult(restTemplate.getForEntity(buildUri(movieKey.toQueryParamsMap()), String.class));
    }

    public SearchResult fetchMoviesBySearchCriteria(@NonNull MovieSearchParameters searchParameters) {
        return getSearchResult(restTemplate.getForEntity(buildUri(searchParameters.toQueryParamsMap()), String.class));
    }

    private MovieResult getMovieResult(@NonNull ResponseEntity<String> response) {

        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(response.getBody());
        } catch (JsonProcessingException e) {
            return MovieResult.fail(500, e.getMessage());
        }

        int statusCode = response.getStatusCode().value();
        if (jsonNode.has("Error")) {
            return MovieResult.fail(statusCode, jsonNode.get("Error").textValue());
        }

        Movie movie = null;
        try {
            movie = objectMapper.treeToValue(jsonNode, Movie.class);
        } catch (JsonProcessingException e) {
            return MovieResult.fail(500, e.getMessage());
        }

        return MovieResult.success(movie);
    }

    private SearchResult getSearchResult(@NonNull ResponseEntity<String> response) {
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(response.getBody());
        } catch (JsonProcessingException e) {
            return SearchResult.fail(500, e.getMessage());
        }

        int statusCode = response.getStatusCode().value();
        if (jsonNode.has("Error")) {
            return SearchResult.fail(statusCode, jsonNode.get("Error").textValue());
        }

        ObjectReader reader = objectMapper.readerFor(new TypeReference<List<Search>>() {});
        List<Search> list = null;
        try {
            list = reader.readValue(jsonNode.get("Search"));
        } catch (IOException e) {
            return SearchResult.fail(500, e.getMessage());
        }

        return SearchResult.success(list);
    }

    private String buildUri(@NonNull MultiValueMap<String, String> params) {
        return UriComponentsBuilder.fromUri(baseUrl)
                .queryParams(params)
                .encode()
                .build()
                .toString();
    }
}
