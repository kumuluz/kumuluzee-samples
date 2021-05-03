package com.kumuluz.ee.samples.rest;

public class PathResponse {

    private String fullPath;

    private String sha256;

    public PathResponse(String fullPath, String sha256) {
        this.fullPath = fullPath;
        this.sha256 = sha256;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public String getSha256() {
        return sha256;
    }

    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }
}
