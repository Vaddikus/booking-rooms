package com.herokuapp.restfulbooker.model.enums;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
public enum HeaderType {
    BASIC_AUTHORIZATION("Authorization"),
    COOKIE("Cookie"),
    EMPTY("");

    private String type;

    public String getType() {
        return type;
    }
}
