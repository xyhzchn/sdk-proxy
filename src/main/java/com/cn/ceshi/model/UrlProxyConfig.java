package com.cn.ceshi.model;

import java.io.Serializable;

public class UrlProxyConfig implements Serializable, Comparable<UrlProxyConfig> {
    private String clientReqUrl;
    private String destServerUrl;
    private boolean enable;
    private long createTime;

    public UrlProxyConfig() {}

    public String getClientReqUrl() {
        return clientReqUrl;
    }

    public void setClientReqUrl(String clientReqUrl) {
        this.clientReqUrl = clientReqUrl;
    }

    public String getDestServerUrl() {
        return destServerUrl;
    }

    public void setDestServerUrl(String destServerUrl) {
        this.destServerUrl = destServerUrl;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getCreateTime() {
        return createTime;
    }


    @Override
    public int compareTo(UrlProxyConfig old) {
        return (int) (old.createTime - this.createTime);
    }
}
