package com.testprint360.api_gateway.service;


import com.testprint360.api_gateway.dao.CachedUserDetails;
import reactor.core.publisher.Mono;

public interface CachedUserService {
    public void saveUserDetailsToCache(String key, CachedUserDetails userDetails);
    public CachedUserDetails getUserDetailsFromCached(String key);

    public CachedUserDetails getUserDetailsByUsername(String username, String jwt);

}
