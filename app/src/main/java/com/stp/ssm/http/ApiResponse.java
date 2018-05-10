package com.stp.ssm.http;

public class ApiResponse {

    private String textReponse;
    private int httpStatus;

    public ApiResponse(String textReponse, int httpStatus) {
        this.textReponse = textReponse;
        this.httpStatus = httpStatus;
    }

    public String getTextReponse() {
        return textReponse;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
