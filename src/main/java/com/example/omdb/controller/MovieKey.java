package com.example.omdb.controller;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import lombok.NonNull;
import lombok.Value;

@Value
public class MovieKey {
    @NonNull
    String apiKey;
    @NonNull
    String imdbId;

    public MultiValueMap<String, String> toQueryParamsMap() {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("apiKey", apiKey);
        map.add("i", imdbId);
        return map;
    }
}
