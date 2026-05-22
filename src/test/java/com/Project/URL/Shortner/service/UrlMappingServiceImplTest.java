package com.Project.URL.Shortner.service;

import com.Project.URL.Shortner.DTO.request.CreateShortUrlRequest;
import com.Project.URL.Shortner.DTO.response.ShortUrlResponse;
import com.Project.URL.Shortner.DTO.response.UrlStatsResponse;
import com.Project.URL.Shortner.entity.UrlMapping;
import com.Project.URL.Shortner.exceptions.InvalidUrlException;
import com.Project.URL.Shortner.exceptions.ShortCodeAlreadyExistsException;
import com.Project.URL.Shortner.exceptions.UrlExpiredException;
import com.Project.URL.Shortner.exceptions.UrlNotFoundException;
import com.Project.URL.Shortner.repository.UrlMappingrepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // "tells JUnit enable Mockito features here"
public class UrlMappingServiceImplTest {
    @Mock // @Mock - Creates fake dependency.
    private UrlMappingrepo urlMappingrepo;

    @InjectMocks // @InjectMocks - Creates actual service object and injects mocked dependencies.
    private UrlMappingServiceImpl urlMappingService;

    @BeforeEach
    void setUp(){
        ReflectionTestUtils.setField(
                urlMappingService,
                "baseUrl",
                "http://localhost:8080"
        );
    }

    @Test
    void shouldCreateShortUrlSuccessfully() {
        CreateShortUrlRequest request = new CreateShortUrlRequest();
        request.setOriginalUrl("https://github.com");

        ShortUrlResponse response = urlMappingService.createShortUrl(request);

        assertNotNull(response);
        assertEquals("https://github.com", response.getOriginalUrl());
        assertNotNull(response.getShortCode());
        assertTrue(response.getShortUrl().startsWith("http://localhost:8080/"));

        verify(urlMappingrepo).save(any());
    }

    @Test
    void shouldThrowInvalidUrlExceptionForMalformedUrl() {
        CreateShortUrlRequest request = new CreateShortUrlRequest();
        request.setOriginalUrl("www.google.com");

        assertThrows(InvalidUrlException.class,() -> urlMappingService.createShortUrl(request));
    }

    @Test
    void shouldThrowExceptionWhenCustomShortCodeAlreadyExists() {

        CreateShortUrlRequest request = new CreateShortUrlRequest();
        request.setOriginalUrl("https://github.com");
        request.setCustomCode("github");

        when(urlMappingrepo.existsByShortCode("github")).thenReturn(true);

        assertThrows(
                ShortCodeAlreadyExistsException.class,
                () -> urlMappingService.createShortUrl(request)
        );
    }

    @Test
    void shouldThrowExceptionWhenUrlHasExpired() {

        UrlMapping mapping = UrlMapping.builder()
                .originalUrl("https://google.com")
                .shortCode("google")
                .clickCount(5L)
                .expiryAt(OffsetDateTime.now().minusDays(1))
                .build();

        when(urlMappingrepo.findByShortCode("google")).thenReturn(Optional.of(mapping));

        assertThrows(UrlExpiredException.class,
                ()-> urlMappingService.getOriginalUrl(mapping.getShortCode())
        );
    }

    @Test
    void shouldIncrementClickCountAndReturnOriginalUrl() {
        UrlMapping mapping = UrlMapping.builder()
                .originalUrl("https://google.com")
                .shortCode("google")
                .clickCount(5L)
                .expiryAt(OffsetDateTime.now().plusDays(5))
                .build();

        when(urlMappingrepo.findByShortCode("google")).thenReturn(Optional.of(mapping));

        String originalUrl = urlMappingService.getOriginalUrl("google");

        verify(urlMappingrepo).save(mapping);
        assertEquals(6L, mapping.getClickCount());
        assertEquals("https://google.com",originalUrl);


    }

    @Test
    void shouldThrowExceptionWhenUrlNotFound() {
        when(urlMappingrepo.findByShortCode("google")).thenReturn(Optional.empty());
        assertThrows(UrlNotFoundException.class,()-> urlMappingService.getOriginalUrl("google"));

    }

    @Test
    void shouldReturnUrlStatsSuccessfully() {
        UrlMapping mapping = UrlMapping.builder()
                .originalUrl("https://google.com")
                .shortCode("google")
                .clickCount(5L)
                .build();

        when(urlMappingrepo.findByShortCode("google")).thenReturn(Optional.of(mapping));

        UrlStatsResponse response = urlMappingService.getUrlStats("google");

        assertEquals("https://google.com", response.getOriginalUrl());
        assertEquals("google", response.getShortCode());
        assertEquals(5L, response.getClickCount());
        assertEquals(mapping.getCreatedAt(), response.getCreatedAt());

    }




}
