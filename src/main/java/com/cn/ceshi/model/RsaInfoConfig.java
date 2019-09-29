package com.cn.ceshi.model;

import java.io.Serializable;
import java.util.Comparator;

public class RsaInfoConfig implements Serializable, Comparable<RsaInfoConfig> {
    private String url;
    private String modules;
    private String publicKey;
    private String secretKey;
    private long createTime;
    private boolean enable;

    public RsaInfoConfig () {

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getModules() {
        return modules;
    }

    public void setModules(String modules) {
        this.modules = modules;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getCreateTime() {
        return createTime;
    }

    @Override
    public int compareTo(RsaInfoConfig old) {
        return (int) (old.createTime - this.createTime);
    }
}
