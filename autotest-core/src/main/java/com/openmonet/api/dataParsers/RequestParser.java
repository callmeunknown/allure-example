package com.openmonet.api.dataParsers;

import com.google.gson.GsonBuilder;

public class RequestParser {

    private String method;
    private String prefix;
    private String fullUrl;
    private String path;
    private String requestBody;
    private String multipart;
    private String formUrlEncoded;
    private String headers;

    public RequestParser(String method, String prefix, String path, String requestBody, String multipart, String headers, String formUrlEncoded, String fullUrl) {
        this.method = method;
        this.prefix = prefix;
        this.path = path;
        this.requestBody = requestBody;
        this.multipart = multipart;
        this.headers = headers;
        this.formUrlEncoded = formUrlEncoded;
        this.fullUrl = fullUrl;
    }

    public RequestParser(String method, String fullUrl, String requestBody) {
        this.method = method;
        this.fullUrl = fullUrl;
        this.requestBody = requestBody;
    }

    public RequestParser(String method, String fullUrl) {
        this.method = method;
        this.fullUrl = fullUrl;
    }

    public String getFormUrlEncoded() {
        return formUrlEncoded;
    }

    public void setFormUrlEncoded(String formUrlEncoded) {
        this.formUrlEncoded = formUrlEncoded;
    }

    public String getFullUrl() {
        return fullUrl;
    }

    public void setFullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }


    public String getMultipart() {
        return multipart;
    }

    public String getHeaders() {
        return headers;
    }

    @Override
    public String toString() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .create()
                .toJson(this);
    }
}
