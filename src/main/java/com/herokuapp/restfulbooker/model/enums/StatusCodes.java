package com.herokuapp.restfulbooker.model.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum StatusCodes {
    SUCCESS(200),
    CREATED(201),
    BAD_REQUEST(400),
    FORBIDDEN(403),
    METHOD_NOT_ALLOWED(405);


    private int code;

    public int getCode() {
        return code;
    }
}
