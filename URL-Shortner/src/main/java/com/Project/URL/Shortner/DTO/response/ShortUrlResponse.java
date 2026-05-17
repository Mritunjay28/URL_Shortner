package com.Project.URL.Shortner.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ShortUrlResponse {
    private String originalUrl;
    private String shortUrl;
    private String shortCode;
}
