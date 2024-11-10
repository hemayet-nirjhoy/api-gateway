package com.testprint360.api_gateway.security;

import com.testprint360.api_gateway.dao.CachedUserDetails;
import com.testprint360.api_gateway.service.impl.CachedUserServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

@Component
public class JwtUtil {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${testsprint360.subscription.service.jwtsecret}")
    private String SECRET_KEY;

    @Value("${testsprint360.subscription.service.jwtExpirationMs}")
    private Long jwtExpirationMs;

    @Autowired
    private CachedUserServiceImpl userService;


    public Claims extractClaims(String token) {
        try {
            log.info("Parsing jwt token...");
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SignatureException e) {
            throw new RuntimeException("Invalid JWT signature");
        }
    }

    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractClaims(token);
        return claimsResolver.apply(claims);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }



    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractClaims(token);
            if (isTokenExpired(token))
                return false;
            String username = extractUsername(token);
            if (Objects.isNull(username))
                return false;
            CachedUserDetails userDetails = userService.getUserDetailsByUsername(username, token);
            return Objects.nonNull(userDetails) && username.equals(userDetails.getUsername()) && userDetails.isActive();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
