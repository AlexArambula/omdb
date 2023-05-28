package com.example.omdb.dto;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SearchResult {
    boolean success;
    @NonNull
    Integer status;
    String message;
    @JsonProperty("Search")
    List<Search> items = List.of();

    public static SearchResult fail(@NonNull Integer status, @NonNull String message) {
        return new SearchResult(false, status, message, List.of());
    }

    public static SearchResult success(@Nullable List<Search> items) {
        return new SearchResult(true, HttpStatus.OK.value(), null, Objects.isNull(items) ? List.of() : items);
    }
}
