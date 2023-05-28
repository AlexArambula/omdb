package com.example.omdb.dto;

import javax.annotation.Nullable;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class MovieResult {
    boolean success;
    @NonNull
    Integer status;
    String message;
    Movie movie;

    public static MovieResult fail(@NonNull Integer status, @NonNull String message) {
        return new MovieResult(false, status, message, null);
    }

    public static MovieResult success(@Nullable Movie movie) {
        return new MovieResult(true, HttpStatus.OK.value(), null, movie);
    }
}
