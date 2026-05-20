package com.Project.URL.Shortner.controller;

import com.Project.URL.Shortner.DTO.response.ShortUrlResponse;
import com.Project.URL.Shortner.DTO.response.UrlStatsResponse;
import com.Project.URL.Shortner.exceptions.UrlExpiredException;
import com.Project.URL.Shortner.exceptions.UrlNotFoundException;
import com.Project.URL.Shortner.service.UrlMappingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UrlShortenerController.class) // Loads ONLY web layer.
class UrlShortenerControllerTest {

    @Autowired
    private MockMvc mockMvc; //Lets you simulate requests

    @MockitoBean // Creates fake Spring bean.
    private UrlMappingService urlMappingService;


    @Test // POST /api/urls // Expected: //200 OK //JSON response
    void shouldCreateShortUrlSuccessfully() throws Exception{
        ShortUrlResponse response = ShortUrlResponse.builder()
                .originalUrl("https://google.com")
                .shortCode("abcde")
                .shortUrl("http://localhost:8080/abcde")
                .build();

        when(urlMappingService.createShortUrl(any())).thenReturn(response);


        String request = """
                {
                "originalUrl":"https://google.com"
                }
                """;

        mockMvc.perform(post("/api/urls")
                .contentType("application/json")
                .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalUrl").value("https://google.com"))
                .andExpect(jsonPath("$.shortCode").value("abcde"))
                .andExpect(jsonPath("$.shortUrl").value("http://localhost:8080/abcde"));
    }

    @Test
    void shouldReturnBadRequestWhenUrlIsMissing() throws Exception {
        String request = """
                {
                "originalUrl":""
                }
                """;

        mockMvc.perform(post("/api/urls")
                .contentType("application/json")
                .content(request))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(urlMappingService);
    }

    @Test
    void shouldRedirectToOriginalUrl() throws Exception {
       when(urlMappingService.getOriginalUrl("google")).thenReturn("https://google.com");

       mockMvc.perform(get("/google"))
               .andExpect(status().isFound())
               .andExpect(header().string("Location","https://google.com"));
    }


    @Test
    void shouldReturnNotFoundWhenShortCodeDoesNotExist() throws Exception {
        when(urlMappingService.getOriginalUrl("google")).thenThrow(new UrlNotFoundException("ShortCode not Found"));

        mockMvc.perform(get("/google"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("ShortCode not Found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void shouldReturnGoneWhenUrlHasExpired() throws Exception {

        when(urlMappingService.getOriginalUrl("expired"))
                .thenThrow(new UrlExpiredException("The given ShortCode has Expired"));

        mockMvc.perform(get("/expired"))
                .andExpect(status().isGone())
                .andExpect(jsonPath("$.message")
                        .value("The given ShortCode has Expired"))
                .andExpect(jsonPath("$.status").value(410));
    }

    @Test
    void shouldReturnUrlStatsSuccessfully() throws Exception {

        UrlStatsResponse response = UrlStatsResponse.builder()
                .originalUrl("https://google.com")
                .shortCode("google")
                .clickCount(5L)
                .build();

        when(urlMappingService.getUrlStats("google"))
                .thenReturn(response);

        mockMvc.perform(get("/google/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalUrl")
                        .value("https://google.com"))
                .andExpect(jsonPath("$.shortCode")
                        .value("google"))
                .andExpect(jsonPath("$.clickCount")
                        .value(5));
    }


}
