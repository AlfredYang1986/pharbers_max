package com.pharbers.aqll.alcalc.alfinaldataprocess;

/**
 * Created by liwei on 2017/3/26.
 */
public class alExport {
    private Integer status;
    private String message;
    private String filename;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
