package com.productdock.adapter.out.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.productdock.application.port.out.web.RentalsClient;
import com.productdock.domain.BookRentalState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;

@Slf4j
@Component
public class RentalsApiClient implements RentalsClient {

    private String rentalsServiceUrl;
    private HttpClient client = HttpClient.newHttpClient();

    private ObjectMapper objectMapper = new ObjectMapper();

    public RentalsApiClient(@Value("${rental.service.url}/api/rental/books/") String rentalsServiceUrl) {
        this.rentalsServiceUrl = rentalsServiceUrl;
    }

    @Override
    public Collection<BookRentalState> getRentals(Long bookId) throws IOException, InterruptedException {
        var jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getCredentials()).getTokenValue();
        var uri = new DefaultUriBuilderFactory(rentalsServiceUrl)
                .builder()
                .path(bookId.toString())
                .path("/rentals")
                .build();
        var request = HttpRequest.newBuilder()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                .GET()
                .build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), new TypeReference<>() {
        });
    }
}
