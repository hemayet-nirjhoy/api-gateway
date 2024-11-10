package com.testprint360.api_gateway.config;

import com.testprint360.api_gateway.security.JwtAuthenticationFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public GatewayConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * This method is alternative of yml configuration.
     * It is more relevant when a developer needs more control over the code
    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("test-route", r -> r.path("/test/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))) // Correct usage
                           .uri("http://localhost:9090")
                )
                .route("subscription-route", r -> r.path("/subscription/**")
                        .uri("http://localhost:9096")
                )
                .route("subscription-route", r -> r.path("/public/**")
                        .uri("http://localhost:9096")
                )
                .build();
    }
    */


}
