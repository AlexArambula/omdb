package com.example.omdb.configuration;

import java.time.Duration;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.omdb.dto.Movie;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

@Configuration
public class CacheConfig {

    @Bean
    public Cache<String, Set<String>> keysCache(@Value("${cache.api_keys.eviction.max_records:100}") int maxRecords,
                                                @Value("${cache.api_keys.eviction.idle_minutes:1440}") int idleMinutes) {
        return CacheBuilder.newBuilder()
                .maximumSize(maxRecords)
                .expireAfterAccess(Duration.ofMinutes(idleMinutes))
                .build();
    }

    @Bean
    public Cache<String, Movie> moviesCache(@Value("${cache.movies.eviction.max_records:50}") int maxRecords,
                                            @Value("${cache.movies.eviction.idle_minutes:1440}") int idleMinutes) {
        return CacheBuilder.newBuilder()
                .maximumSize(maxRecords)
                .expireAfterAccess(Duration.ofMinutes(idleMinutes))
                .build();
    }
}
