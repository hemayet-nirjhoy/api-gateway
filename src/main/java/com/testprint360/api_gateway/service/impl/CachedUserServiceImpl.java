package com.testprint360.api_gateway.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testprint360.api_gateway.dao.CachedUserDetails;
import com.testprint360.api_gateway.helpers.UserHelper;
import com.testprint360.api_gateway.service.CachedUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.util.Objects;


@Service
public class CachedUserServiceImpl implements CachedUserService {

    private String AUTH_SERVICE;
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private RedisTemplate<String, Object> redisTemplate;
    private UserHelper userHelper;
    private WebClient webClient;
    @Autowired
    public CachedUserServiceImpl(RedisTemplate<String, Object> redisTemplate,
                                 UserHelper userHelper,
                                 WebClient.Builder webClientBuilder,
                                 @Value("${testsprint360.api.gateway.auth.service}") String authService) {
        System.out.println("Web Client initializing....");
        this.redisTemplate = redisTemplate;
        this.userHelper = userHelper;
        this.AUTH_SERVICE = authService;
        this.webClient = WebClient.builder()
                .baseUrl(authService)
                .filter((request, next) -> next.exchange(request)
                        .timeout(Duration.ofSeconds(10))
                        .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(5))))
                .build();
    }

    @Override
    public void saveUserDetailsToCache(String key, CachedUserDetails userDetails) {
        redisTemplate.opsForValue().set(key, userDetails);
    }

    @Override
    public CachedUserDetails getUserDetailsFromCached(String key) {
        return (CachedUserDetails) redisTemplate.opsForValue().get(key);
    }

    /***
     * Find the username from the cache, if not found then fetch it from
     * external authentication service through API call
     * @param username
     * @param jwtToken
     * @return
     */
    @Override
    public CachedUserDetails getUserDetailsByUsername(String username, String jwtToken) {
        try {
            // Check cache first
            CachedUserDetails userDetails = getUserDetailsFromCached(userHelper.getKey(username));
            if (Objects.nonNull(userDetails)) {
                log.info("Found user-details from cache!");
                return userDetails;
            }
            log.info("Fetching user details from authentication service...");
            String uri = this.AUTH_SERVICE+"/user/username/" + username;
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Authorization", "Bearer " + jwtToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            RestTemplate restTemplate = new RestTemplate();

            // Make the HTTP GET request
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                String jsonString = response.getBody();
                try {
                    // Parse the JSON response
                    CachedUserDetails user = parseUserFromJson(jsonString);
                    // Cache the user details
                    saveUserDetailsToCache(userHelper.getKey(username), user);
                    return user;
                } catch (Exception e) {
                    log.error("Error parsing JSON: ", e);
                    return null;
                }
            } else {
                log.error("Error: Received non-successful HTTP status code " + response.getStatusCode());
                return null;
            }
        } catch (Exception e) {
            log.error("Error occurred while fetching user details: ", e);
            return null;
        }
    }


    private CachedUserDetails parseUserFromJson(String jsonString) throws Exception {
        JsonNode jsonNode = new ObjectMapper().readTree(jsonString);
        CachedUserDetails user = new CachedUserDetails();
        user.setId(jsonNode.path("id").asLong());
        user.setUsername(jsonNode.path("username").asText());
        user.setRole(jsonNode.path("role").asText());
        user.setFirstName(jsonNode.path("firstName").asText());
        user.setLastName(jsonNode.path("lastName").asText());
        user.setEmail(jsonNode.path("email").asText());
        user.setActive(jsonNode.path("active").asBoolean());
        return user;
    }

}
