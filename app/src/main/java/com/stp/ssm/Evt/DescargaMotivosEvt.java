package com.stp.ssm.Evt;

import com.stp.ssm.http.HttpStatus;

public class DescargaMotivosEvt {

    private String json;
    private String errorRazon;
    private HttpStatus httpStatus;

    public DescargaMotivosEvt(String json) {
        this.json = json;
    }


    public DescargaMotivosEvt(String json, String errorRazon, HttpStatus httpStatus) {
        this.json = json;
        this.errorRazon = errorRazon;
        this.httpStatus = httpStatus;
    }


    public String getJson() {
        return json;
    }

    public String getErrorRazon() {
        return errorRazon;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
