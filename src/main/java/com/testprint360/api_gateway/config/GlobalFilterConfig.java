package com.testprint360.api_gateway.config;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;


@Configuration
public class GlobalFilterConfig {

    @Bean
    public GlobalFilter customGlobalFilter() {
        return (exchange, chain) -> {
            // Pre-processing logic
            System.out.println("Global filter: Pre-processing");

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                // Post-processing logic
                System.out.println("Global filter: Post-processing");
            }));
        };
    }

}
