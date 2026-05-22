package com.Project.URL.Shortner.DTO.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@Setter
public class CreateShortUrlRequest {
    @NotBlank(message = "URL cannot be Empty")
    private String originalUrl;
//    @Size(min = 3 ,max = 10)
    private String customCode;

    private OffsetDateTime expiryAt;
}
