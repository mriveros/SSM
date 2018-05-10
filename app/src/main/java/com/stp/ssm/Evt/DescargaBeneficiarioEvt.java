package com.stp.ssm.Evt;


import com.stp.ssm.http.HttpStatus;


public class
DescargaBeneficiarioEvt {


    private String json;

    private String errorRazon;

    private HttpStatus httpStatus;


    public DescargaBeneficiarioEvt(String json) {
        this.json = json;
    }


    public DescargaBeneficiarioEvt(String json, String errorRazon, HttpStatus httpStatus) {
        this.json = json;
        this.errorRazon = errorRazon;
        this.httpStatus = httpStatus;
    }

    public String getErrorRazon() {
        return errorRazon;
    }



    public String getJson() {
        return json;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
