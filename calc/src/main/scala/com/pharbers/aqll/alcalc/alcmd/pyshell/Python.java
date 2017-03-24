package com.pharbers.aqll.alcalc.alcmd.pyshell;

import java.util.List;

/**
 * Created by liwei on 2017/3/25.
 */
public class Python {
    private Integer status;
    private String filename;
    private List<String> markets;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public List<String> getMarkets() {
        return markets;
    }

    public void setMarkets(List<String> markets) {
        this.markets = markets;
    }
}
