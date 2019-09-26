package com.cn.ceshi.util.didiking2;

import com.lamfire.json.JSON;
import com.lamfire.utils.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

/**
 * 统一的加解密工具
 */
public class DidiKingUniformServiceImpl {

    public JSON inDecode(String requestUrl, Map<String, String> headers, String httpBody, JSON needKey) throws Exception {
        JSON result = new JSON();
        result.put("success", 0);//1-参数无误  0-参数错误
        result.put("errorMsg", "[test]缺少字段UserID");
        String seeBody = httpBody;
        result.put("resultData", seeBody);
        if (requestUrl == null) {
            return result;
        }
        if (requestUrl.indexOf("p.ddking.mob.com") != -1
                || requestUrl.indexOf("ddking.mob.com") != -1) {
            seeBody = didiking_des(httpBody, true);
            result.put("resultData", seeBody);
        }
        return result;
    }
    public JSON outDecode(String requestUrl, Map<String, String> headers,String httpBody, JSON needKey) throws Exception {
        JSON result = new JSON();
        result.put("success", 1);//1-参数无误  0-参数错误
        String seeBody = httpBody;
        result.put("resultData", seeBody);
        if (requestUrl == null) {
            return result;
        }
        if (requestUrl.indexOf("p.ddking.mob.com") != -1
                || requestUrl.indexOf("ddking.mob.com") != -1) {
            seeBody = didiking_des(httpBody, false);
            result.put("resultData", seeBody);
        }
        return result;
    }

    private String didiking_des(String aseBody, boolean needURLDecoder) {
        if (!StringUtils.isEmpty(aseBody)) {
            try {
                if (needURLDecoder) {
                    aseBody = URLDecoder.decode(aseBody, "utf-8");
                }
                return AESUtil.decrypt(aseBody);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}