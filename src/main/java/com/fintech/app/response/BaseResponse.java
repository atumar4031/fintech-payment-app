package com.fintech.app.response;

import lombok.Data;

@Data
public class BaseResponse<T> {

    private Boolean status;
    private String message;
    private T result;

    public BaseResponse() {
    }

    public BaseResponse(Boolean status, String message, T result) {
        this.status = status;
        this.message = message;
        this.result = result;
    }
}
