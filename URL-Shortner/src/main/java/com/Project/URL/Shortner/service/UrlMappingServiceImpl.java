package com.Project.URL.Shortner.service;

import com.Project.URL.Shortner.DTO.request.CreateShortUrlRequest;
import com.Project.URL.Shortner.DTO.response.ShortUrlResponse;
import com.Project.URL.Shortner.DTO.response.UrlStatsResponse;
import com.Project.URL.Shortner.entity.UrlMapping;
import com.Project.URL.Shortner.exceptions.InvalidUrlException;
import com.Project.URL.Shortner.exceptions.UrlNotFoundException;
import com.Project.URL.Shortner.repository.UrlMappingrepo;
import com.Project.URL.Shortner.util.ShortCodeGenerator;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlMappingServiceImpl implements UrlMappingService{

    @Value("${app.base-url}")
    private String baseUrl;

    private final UrlMappingrepo urlMappingrepo;

    @Override
    public ShortUrlResponse createShortUrl(CreateShortUrlRequest request) {
        // extract url
       String originalUrl = request.getOriginalUrl();

       // validate url
       try{
           URI.create(originalUrl).toURL();
       } catch (MalformedURLException e) {
           throw new InvalidUrlException("Invalid URL is Given");
       }

       // create shortcode such that not present in db already
        String code ;
       do {
           code = ShortCodeGenerator.generate();
       }while(urlMappingrepo.existsByShortCode(code));

       UrlMapping newEntity = UrlMapping.builder()
               .originalUrl(originalUrl)
               .shortCode(code)
               .build();



       urlMappingrepo.save(newEntity);

        ShortUrlResponse response = ShortUrlResponse.builder()
                .originalUrl(originalUrl)
                .shortUrl(baseUrl+ "/" + code)
                .shortCode(code)
                .build();

        return response;

    }

    @Override
    public String getOriginalUrl(String shortCode) {
        UrlMapping mapping = urlMappingrepo.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("Short code not found"));


        mapping.setClickCount(mapping.getClickCount()+1);

        urlMappingrepo.save(mapping);

        return mapping.getOriginalUrl();
    }

    @Override
    public UrlStatsResponse getUrlStats(String shortCode) {
        UrlMapping mapping = urlMappingrepo.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("Short code not found"));

        return UrlStatsResponse.builder()
                .originalUrl(mapping.getOriginalUrl())
                .shortCode(mapping.getShortCode())
                .createdAt(mapping.getCreatedAt())
                .clickCount(mapping.getClickCount())
                .build();
    }
}
