package com.testprint360.api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}


//	@Bean
//	public RouteLocator routeLocator(RouteLocatorBuilder builder) {
//		System.out.println("Route Locator is working fine");
//		return builder.routes()
//				.route(" test-route", r -> r.path("/test/**").uri("lb://test-application"))
//				.route("subscription-route", r-> r.path("/subscription/**").uri("lb://subscription-service"))
//				.build();
//	}


}
