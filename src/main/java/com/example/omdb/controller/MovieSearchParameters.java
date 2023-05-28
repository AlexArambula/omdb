package com.example.omdb.controller;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class MovieSearchParameters {
    @NotBlank(message = "The apiKey cannot be a blank string.")
    String apiKey;
    @NotBlank(message = "Title cannot be a blank string.")
    String title;
    Type type;
    @Min(value = 1900, message = "The minimum value for year is 1900.")
    @Max(value = 2023, message = "The maximum value for year is 2023.")
    Integer year;

    public enum Type {
        movie, series, episode
    }

    public MultiValueMap<String, String> toQueryParamsMap() {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("apiKey", apiKey);
        map.add("s", title);
        if (type != null) {
            map.add("type", type.name());
        }
        if (year != null) {
            map.add("y", year.toString());
        }
        return map;
    }
}
