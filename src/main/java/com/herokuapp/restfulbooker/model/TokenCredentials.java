package com.herokuapp.restfulbooker.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenCredentials {
    @Builder.Default
    String username = "admin";
    @Builder.Default
    String password = "password123";
}
