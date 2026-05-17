package com.Project.URL.Shortner.controller;

import com.Project.URL.Shortner.DTO.request.CreateShortUrlRequest;
import com.Project.URL.Shortner.DTO.response.ShortUrlResponse;
import com.Project.URL.Shortner.service.UrlMappingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UrlShortenerController {
    private final UrlMappingService urlMappingService;

    @PostMapping("/api/urls")
    public ShortUrlResponse createShortUrl(@RequestBody @Valid CreateShortUrlRequest request){
        return urlMappingService.createShortUrl(request);
    }

    @GetMapping("/{code}")
    public ResponseEntity<Void>  getOriginalUrl(@PathVariable String code){
        String originalUrl = urlMappingService.getOriginalUrl(code);

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION,originalUrl)
                .build();
    }
}
