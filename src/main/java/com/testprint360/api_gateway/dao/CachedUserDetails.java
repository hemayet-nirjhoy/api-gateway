package com.testprint360.api_gateway.dao;


import lombok.Data;


@Data
public class CachedUserDetails {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private boolean active;

    /** Object existence status (this is not related to user) **/

}
