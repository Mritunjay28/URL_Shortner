package com.Project.URL.Shortner.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UrlStatsResponse {

    private String originalUrl;

    private String shortCode;

    private LocalDateTime createdAt;

    private Long clickCount ;
}
