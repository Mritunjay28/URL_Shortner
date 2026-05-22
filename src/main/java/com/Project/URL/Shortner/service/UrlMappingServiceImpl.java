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
import com.Project.URL.Shortner.util.ShortCodeGenerator;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
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
       } catch (Exception e) {
           throw new InvalidUrlException("Invalid URL is Given");
       }

       // create shortcode such that not present in db already


        String code ;

       if(request.getCustomCode() != null &&
               !request.getCustomCode().trim().isEmpty()) {

           // trime the gode to remove extra like "  github  "
           String customCode = request.getCustomCode().trim();

           // check if of desire length and check if code contain only a-z , A-Z , 0-9 , _ , -
           boolean isValid = customCode.matches("^[a-zA-Z0-9_-]{3,10}$");

           if (!isValid) {
               throw new InvalidUrlException(
                       "Short code must be 3-10 characters and contain only letters, numbers, _ or -"
               );
           }

           // check if ok to add or not
           if (urlMappingrepo.existsByShortCode(customCode)) {
               throw new ShortCodeAlreadyExistsException(
                       customCode + " is already taken"
               );
           }

           code = customCode;
       }
       else{
           do {
               code = ShortCodeGenerator.generate();
           }while(urlMappingrepo.existsByShortCode(code));
       }

       // check if not creating expired links
        if (request.getExpiryAt() != null &&
                request.getExpiryAt().isBefore(OffsetDateTime.now())) {
            throw new InvalidUrlException("Expiry time must be in the future");
        }


       UrlMapping newEntity = UrlMapping.builder()
               .originalUrl(originalUrl)
               .shortCode(code)
               .expiryAt(request.getExpiryAt())
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

        if(mapping.getExpiryAt() != null && mapping.getExpiryAt().isBefore(OffsetDateTime.now())) throw new UrlExpiredException("The given ShortCode has Expired");

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
