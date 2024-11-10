package com.testprint360.api_gateway.helpers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UserHelper {

    @Value("${testsprint360.api.gateway.cache.key.prefix}")
    String cachePrefix;

    public String getKey(String username){
        StringBuilder key = new StringBuilder();
        key.append(cachePrefix);
        key.append("_");
        key.append("user");
        key.append("_");
        key.append(username);
        return key.toString();
    }
}
