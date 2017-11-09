package com.ef.Parser.com.ef.repository;

/**
 * Created by minhle on 8/11/17.
 */

public class AccessLog {

    private String datetime;
    private String ip ;
    private String request;
    private int status ;
    private String userAgent;

    public AccessLog(String datetime, String ip, String request, int status, String userAgent) {
        this.datetime = datetime;
        this.ip = ip;
        this.request = request;
        this.status = status;
        this.userAgent = userAgent;
    }


    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}
