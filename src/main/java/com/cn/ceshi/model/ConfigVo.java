package com.cn.ceshi.model;

import java.io.Serializable;

/**
 * Created by wangly on 2018/3/1.
 * 自定明文参数（请求or响应）
 */
public class ConfigVo implements Serializable {


    private long id;
    /**
     * 设定的URL，唯一
     */
    private String url;
    /**
     *请求参数开关是否打开
     */
    private boolean reqOpen;
    private String reqParams;
    /**
     *响应参数开关是否打开
     */
    private boolean respOpen;
    private String respParams;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isReqOpen() {
        return reqOpen;
    }

    public void setReqOpen(boolean reqOpen) {
        this.reqOpen = reqOpen;
    }

    public String getReqParams() {
        return reqParams;
    }

    public void setReqParams(String reqParams) {
        this.reqParams = reqParams;
    }

    public boolean isRespOpen() {
        return respOpen;
    }

    public void setRespOpen(boolean respOpen) {
        this.respOpen = respOpen;
    }

    public String getRespParams() {
        return respParams;
    }

    public void setRespParams(String respParams) {
        this.respParams = respParams;
    }

    @Override
    public String toString() {
        return "ConfigVo{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", reqOpen=" + reqOpen +
                ", reqParams='" + reqParams + '\'' +
                ", respOpen=" + respOpen +
                ", respParams='" + respParams + '\'' +
                '}';
    }
}
