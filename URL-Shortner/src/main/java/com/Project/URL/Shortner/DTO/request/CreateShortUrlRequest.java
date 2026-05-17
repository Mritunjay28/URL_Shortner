package com.Project.URL.Shortner.DTO.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateShortUrlRequest {
    @NotBlank(message = "URL cannot be Empty")
    private String originalUrl;
}
