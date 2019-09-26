package com.cn.ceshi.util.didiking2;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangly on 2017/8/31.
 */
public class DecodeMapUtil {

    public static Map<String, String> getDecodeMap() {
        Map<String, String> tempTestMap = new HashMap<String, String>();
        tempTestMap.put("p.ddking.mob.com", "com.cn.ceshi.util.didiking2.DidiKingUniformServiceImpl");
        tempTestMap.put("api.ddking.mob.com", "com.cn.ceshi.util.didiking2.DidiKingUniformServiceImpl");
        return tempTestMap;
    }
}
