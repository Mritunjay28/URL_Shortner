package com.Project.URL.Shortner.service;

import com.Project.URL.Shortner.DTO.request.CreateShortUrlRequest;
import com.Project.URL.Shortner.DTO.response.ShortUrlResponse;

public interface UrlMappingService {
    ShortUrlResponse createShortUrl(CreateShortUrlRequest request);

    String getOriginalUrl(String shortCode);
}
