package com.example.omdb.dto;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

@Value
public class Search {

    @EqualsAndHashCode.Include
    @NonNull
    @JsonProperty("imdbID")
    String imdbId;

    @ConstructorProperties({"imdbID"})
    Search(String imdbId) {
        this.imdbId = imdbId;
    }
}
