package com.testprint360.api_gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testprint360.api_gateway.service.impl.CachedUserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@SpringBootTest
class ApiGatewayApplicationTests {

	private final WebClient webClient;

	ApiGatewayApplicationTests() {
		this.webClient = WebClient.builder()
				.baseUrl("http://localhost:9096")
				.filter((request, next) -> next.exchange(request)
						.timeout(Duration.ofSeconds(5))
						.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))))
				.build();
	}

	@Autowired
	CachedUserServiceImpl userService;


	@Test
	void contextLoads() throws JsonProcessingException {
		String uri = "http://localhost:9096/subscription/user/username/" + "admin";
		System.out.println(uri);

		Mono<String> response = this.webClient.get()
				.uri(uri)
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTcyNDAzNzg4MywiZXhwIjoxNzM5NTg5ODgzfQ.AXgd96XyeKC6vpk-IV8zGefbJKqEoLMuMRhcaVWhkFE")
				.retrieve()
				.bodyToMono(String.class);

		response.flatMap(jsonString -> {
			System.out.println("Fetching json nodes");
			try {
				JsonNode jsonNode = new ObjectMapper().readTree(jsonString);
				String username = jsonNode.path("username").asText();
				System.out.println("Username " + username);
//				userService.saveUserDetailsToCache("nothing",username);
				return Mono.just(jsonNode);
			} catch (Exception e) {
				System.out.println("Exception occurred: " + e.toString());
				return Mono.error(e);
			}
		}).subscribe(jsonNode -> {
			// Further processing if needed
			System.out.println("FlatMap executed");
		}, throwable -> {
			System.out.println("Error: " + throwable);
		});

		// Adding sleep to allow async processing to complete in test
		try {
			Thread.sleep(5000); // Adjust the sleep duration as needed
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
