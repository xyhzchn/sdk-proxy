package com.cn.ceshi.util.common;

import com.lamfire.code.AES;
import com.lamfire.code.Base64;
import com.lamfire.code.MD5;
import com.lamfire.json.JSON;
import com.lamfire.utils.ZipUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;

/**
 * Created by huangwt on 2018/3/2.
 * 公共库加密
 */
public class RequestEncodeFnc {


    private static final int KEY_SIZE = 1024;
    private static final String PUBLIC_KEY = "e566828c25d8ac4b588217c0a437ace230917f1135abf7d98ef9346e5d8e4f99fc4d6c21bc818c0fffd6db3e3a1aca947feb987003c104d2ab803f7f06726177"; // 指数
    private static final String PRIVATE_KEY = "9cb54a4700937a68414e52d6d2e57ac6ea1fafb23bcbbbe18e133d7d11b7b5f9c670371ffa226e6442dfdb616192e75e2d553c26042867a5021856bdc236053233a5a257d59719b05afde80ff6544c759cfc59165b1bb68c0d2e442d27a59b1150bd83a7ae8e13e798bf294e20841d011c997ac4deba050833fc2bf45486e7";
    private static final String MODULES = "21af4ddbf8167dd7a7af6de3e61ec55783db252d983aeab13cacc8159437e05c59645b2319241b0f0b4a030f6023fe00c2b633d9a1e17f4e2296d30338b0d3ca0b4285bc342004f9b8a4e2696f21507717f9c396ec7d9775a4fe429d542f4bf66fc445672a7a7f3b3d96fc556b86621086c3f74967a4d5da1e2a318d87805e9b";
    private static final BigInteger RSA_PUBLIC = new BigInteger(PUBLIC_KEY, 16);
    private static final BigInteger RSA_PRIVATE = new BigInteger(PRIVATE_KEY, 16);
    private static final BigInteger RSA_MODULES = new BigInteger(MODULES, 16);

    private static byte[] AES_KEY = "19779ABVCDECSa4a".getBytes();

    public static String encode_v1_2_3_4_cconf_resp(String url, JSON needKey, JSON params) throws Exception {

        String appkey = needKey.get("appkey").toString();
        String apppkg = needKey.getString("apppkg");
        long timestamp = System.currentTimeMillis();

        byte[] aesKey = MD5.digest(String.format("%s:%s:%s", new Object[]{appkey, apppkg, timestamp}).getBytes());
        JSON jsonBack = new JSON();
        if (url.indexOf("/v5") > -1) {
            jsonBack.put("sr", cconf_resp_encode_v1_v2_v3(params.getString("sr")));
            jsonBack.put("sc", cconf_resp_encode_v4(aesKey, params.getString("sc")));
        } else {
            jsonBack.put("sr", cconf_resp_encode_v1_v2_v3(params.getString("sr")));
            jsonBack.put("sc", cconf_resp_encode_v1_v2_v3(params.getString("sc")));
        }
        jsonBack.put("timestamp", timestamp);
        jsonBack.put("status", params.getLong("status"));
        return jsonBack.toJSONString();
    }


    public static String cconfDecode(String base64) throws UnsupportedEncodingException {
        byte[] AES_KEY = "FYsAXMqlWJLCDpnc".getBytes();
        byte[] base64Bs = Base64.decode(base64);
        String res = null;

        try {
            AES aes = new AES(AES_KEY);
            res = new String(aes.decode(base64Bs), "UTF-8");
            return res;
        } catch (UnsupportedEncodingException var5) {
            throw var5;
        }
    }

    public static String cconfDecode(String base64, byte[] AES_KEY) throws UnsupportedEncodingException {
        byte[] base64Bs = Base64.decode(base64);
        String res = null;

        try {
            AES aes = new AES(AES_KEY);
            res = new String(aes.decode(base64Bs), "UTF-8");
            return res;
        } catch (UnsupportedEncodingException var5) {
            throw var5;
        }
    }

    public static String cconf_resp_encode_v1_v2_v3(String sr) throws UnsupportedEncodingException {
        byte[] AES_KEY = "FYsAXMqlWJLCDpnc".getBytes();
        try {
            AES aes = new AES(AES_KEY);
            byte[] tmp_data = aes.encode(sr.getBytes("UTF-8"));
            return Base64.encode(tmp_data);
        } catch (UnsupportedEncodingException var5) {
            throw var5;
        }
    }

    public static String cconf_resp_encode_v4(byte[] AES_KEY, String sc) throws UnsupportedEncodingException {

        try {
            AES aes = new AES(AES_KEY);
            byte[] tmp_data = aes.encode(sc.getBytes("UTF-8"));
            return Base64.encode(tmp_data);
        } catch (UnsupportedEncodingException var5) {
            throw var5;
        }
    }

    public static String encode(String hostAndURL, String params) throws Exception {

        if (hostAndURL == null) {
            return params;
        }
        if (hostAndURL.startsWith("l.gm.mob.com") && hostAndURL.endsWith("gcl")) {
            String[] tmp = params.split("&");
            String m_val = tmp[1].substring(2);
            byte[] text = ZipUtils.gzip(m_val.getBytes());
            return tmp[0] + "&" + "m=" + URLEncoder.encode(encodeFnc(new String(text,"utf-8")));

        } else if (hostAndURL.startsWith("v.data.mob.com") &&
                (hostAndURL.endsWith("cpl") || hostAndURL.endsWith("vpl"))) {
            byte[] text = params.getBytes();
            return encodeFnc(new String(text,"utf-8"));
        } else if (hostAndURL.endsWith("rdata")) {
            return new String(Base64.decode(params));
        }

//        if(hostAndURL.contains("smarttel.cn") || hostAndURL.contains("10.21.141.13")){
////            byte[] text = params.getBytes();
////            return cconf_resp_encode_v1_v2_v3(new String(text,"utf-8"));
//            return "AAAAgwAAAH8Hw5re4G5O4p1x6dyowzABpYdQZ47IpN5qzwmUvp954qViFyM8gUE0/7iPp6J0WXOmI7hN8jlecdZwrIA3PBq7Xd+B63yGiUJlf3bs7486hcUWetbx79nahCbIhEQ8JQNGFNJ8lzNx45V7pNZjh2zRYcKr+NXC7CNyulPfxkqYAAACUOMVxgdkMEJsdKZtHN0wtkslk/8S0A7K9ruSo8o9HJX8VQpLPiSfCqVqLrWH5iM7cBHDN8mEaKPFOHGPgS3CH/VAlSiN52dj351ptH+paj8A7qWMT8uSeNtQABGVN/qgCIuuNJnlIjgPtU/2UaClAU+xHqqoZ0NL1rXCoFqXZv/DjnCOwcVCpd95uDxyeMVeo9uqy/h26Pal0RXmAoTY2zfu3qVvJIln55VsQcnjYOOiJ9wKTQmEXqHjHItKglJXxck202MT/aU7bicGN9l1jxjwD9hbc9XyBfh3jXXIeURhDUNB/M0vP+9C8Ziiml+LwnkH/Nm1gvcS3bOYRlcU02h1PCHEKaQmKrzxtGkgiEN+/KaaUh2Pzb5ZyA4Y345hDDTEMZNTMFSG2606cmPGM/P2W2iFgVdhWGiOxhgrLFLg+bLZjfm6fH6OO3Q3uGAr8Yv2SzE1lLAG5P8B0L5PG17Bf0j6UmS+odA2vJkTtubRlzlBtrBswCw5+Et2evTKxcBSmnIPGVys6VF5ZLNqDeyadcFwemSlpT+92I6f5mrZ8aD+BrVO7vIi7Z8/e5Uf3V28Mhyq9yv5rFczyHORk0ywGkMnb+JGpAo1bui3KBCR3iYGo2qnq4I6vS4WxS8K2y610u+y77U85pub+ra3IKnV9gPhyHo6zgawAABU2u3dyu+xOD49idZ9A+nn0V0xIEzZWKDIjJzz5eNWSIDETJmmdRI1Rk1fcIFiQVTeW+SKxUeFhZmbYxNf5ltlk6wy8hJaedPhtgY3fvU5wk/WUBs=";
//        }

        return params;
    }

    public static String encodeFnc(String json) throws Exception {
        AES aes = new AES(RequestDecodeFnc.sAesKey);
        byte[] data = aes.encode(json.getBytes("utf-8"));
        return Base64.encode(data).replaceAll("\n", "");
    }
}
