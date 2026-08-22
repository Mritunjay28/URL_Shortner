package com.Project.URL.Shortner.config;

import com.Project.URL.Shortner.service.RateLimiterService;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor{
    private final RateLimiterService rateLimiterService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 1. Get client IP address
        String clientIp = getClientIp(request);

        Bucket bucket = rateLimiterService.resolveBucket(clientIp);
        // 3. Try to consume 1 token and get details
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (probe.isConsumed()) {
            // ✅ SUCCESS: User has tokens remaining
            // Add a helpful header so the client knows how many requests they have left
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            return true; // Let the request proceed to the Controller
        } else {
            // 🚫 LIMIT EXCEEDED: All 10 tokens used up!

            // Calculate how many seconds until the next token refills
            long waitForRefillSeconds = probe.getNanosToWaitForRefill() / 1_000_000_000;
            // Set HTTP status code 429
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");

            // Header telling the client how long to wait
            response.setHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefillSeconds));

            // Custom JSON error response
            response.getWriter().write(String.format(
                    "{\"status\": 429, \"error\": \"Too Many Requests\", \"message\": \"Rate limit exceeded. Try again in %d seconds.\"}",
                    waitForRefillSeconds
            ));
            return false;
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty() || "unknown".equalsIgnoreCase(xfHeader)) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}
