package com.example.omdb.configuration;

import java.net.URI;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.example.omdb.client.OmdbRestClient;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class RestServiceConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    @Bean
    public RestTemplate restTemplate(@Value("${http.rest_client.connect_timeout_secs:6}") int connectTimeout,
                                     @Value("${http.rest_client.read_timeout_secs:6}") int readTimeout) {
        return new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(connectTimeout))
                .setReadTimeout(Duration.ofSeconds(readTimeout))
                .build();
    }

    @Bean
    public OmdbRestClient omdbRestClient(ObjectMapper objectMapper, RestTemplate restTemplate,
                                         @Value("${omdb.api.url:http://www.omdbapi.com/}") URI omdbApiUri) {
        return new OmdbRestClient(omdbApiUri, objectMapper, restTemplate);
    }
}
