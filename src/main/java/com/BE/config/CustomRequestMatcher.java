package com.BE.config;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.Arrays;
import java.util.List;


public class CustomRequestMatcher implements RequestMatcher {
    private final List<String> PUBLIC_ENDPOINTS_METHOD;

    public CustomRequestMatcher(String[] publicEndpoints) {
        this.PUBLIC_ENDPOINTS_METHOD = Arrays.asList(publicEndpoints);
    }

    @Override
    public boolean matches(jakarta.servlet.http.HttpServletRequest request) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        boolean isPublicEndpoint = PUBLIC_ENDPOINTS_METHOD.stream().anyMatch(publicEndpoint -> uri.startsWith(publicEndpoint));
        return "GET".equals(method) && isPublicEndpoint;
    }


    @Override
    public MatchResult matcher(jakarta.servlet.http.HttpServletRequest request) {
        return RequestMatcher.super.matcher(request);
    }
}

