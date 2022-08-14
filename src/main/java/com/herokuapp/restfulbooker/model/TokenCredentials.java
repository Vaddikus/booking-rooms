package com.herokuapp.restfulbooker.model;

import org.springframework.beans.factory.annotation.Value;

public class TokenCredentials {
    @Value("${token.username}")
    String username;
    @Value("${token.password}")
    String password;
}
