package com.cn.ceshi.model;

import java.io.Serializable;
import java.util.Date;

/**
 * //记录数据的日志
 */
public class Log2DB implements Serializable {
    private long Id;
    private int code = 0;/*请求是否成功success fail*/
    private Date requestTime;/*请求发起时间*/
    private Date responseTime;/*请求响应时间*/
    private Date createAt = new Date(); /*入库时间*/
    private String sendParams;/*请求参数*/
    private String backData;/*返回数据*/
    private String hostAndURI;/*请求的url===p.ddking.mob.com/pay/balance*/

    private String reqOriginParam;//原始请求数据
    private String respOriginParam;//原始响应数据

    public String getReqOriginParam() {
        return reqOriginParam;
    }

    public void setReqOriginParam(String reqOriginParam) {
        this.reqOriginParam = reqOriginParam;
    }

    public String getRespOriginParam() {
        return respOriginParam;
    }

    public void setRespOriginParam(String respOriginParam) {
        this.respOriginParam = respOriginParam;
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }

    public Date getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Date responseTime) {
        this.responseTime = responseTime;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public String getSendParams() {
        return sendParams;
    }

    public void setSendParams(String sendParams) {
        this.sendParams = sendParams;
    }

    public String getBackData() {
        return backData;
    }

    public void setBackData(String backData) {
        this.backData = backData;
    }

    public String getHostAndURI() {
        return hostAndURI;
    }

    public void setHostAndURI(String hostAndURI) {
        this.hostAndURI = hostAndURI;
    }
}