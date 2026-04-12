package com.healthcare.patient.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignClientConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                // This grabs the token from Postman
                String token = attributes.getRequest().getHeader("Authorization");
                if (token != null) {
                    // This glues the token to the internal request
                    requestTemplate.header("Authorization", token);
                }
            }
        };
    }
}