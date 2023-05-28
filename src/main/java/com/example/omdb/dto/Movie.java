package com.example.omdb.dto;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

@Value
public class Movie {

    @EqualsAndHashCode.Include
    @NonNull
    @JsonProperty("imdbID")
    String imdbId;
    @JsonProperty("Title")
    String title;
    @JsonProperty("Year")
    String year;
    @JsonProperty("Genre")
    String genre;
    @JsonProperty("Language")
    String language;
    @JsonProperty("Plot")
    String plot;

    @ConstructorProperties({"imdbID", "Title", "Year", "Genre", "Language", "Plot"})
    public Movie(@NonNull String imdbId, String title, String year, String genre, String language, String plot) {
        this.imdbId = imdbId;
        this.title = title;
        this.year = year;
        this.genre = genre;
        this.language = language;
        this.plot = plot;
    }
}
