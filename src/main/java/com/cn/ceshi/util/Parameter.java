package com.cn.ceshi.util;

/**
 * Created by guoxx on 2019/6/10.
 * 获取配置参数
 */
public class Parameter {
    //唤醒测试环境访问地址
    public static  String AWAKEN_TEST_URL = "10.21.141.54:8699";

    //唤醒独立包线上访问地址
    public static  String AWAKEN_SINGLE_ONLINE_HOST = "api.aw.smarttel.cn";

    //唤醒公共库线上访问地址
    public static  String AWAKEN_COMMON_ONLINE_HOST = "aw.mic.mob.com";

    //动态库线上访问地址
    public static  String DYN_COMMON_ONLINE_HOST = "df.mic.mob.com";

    //公共库日志上次接口线上访问地址
    public static  String COMMON_GCL_ONLINE_HOST = "l.gm.mob.com";

    //公共库获取下发配置接口线上访问地址
    public static  String COMMON_GCF_ONLINE_HOST = "f.gm.mob.com";

    /**
     * 根据xml中的内容，获取相对应的url
     */
    public void getUrlByXml(){

    }

}
