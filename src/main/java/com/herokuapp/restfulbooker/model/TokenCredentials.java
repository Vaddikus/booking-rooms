package com.herokuapp.restfulbooker.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenCredentials {
    String username;
    String password;
}
