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
 * Created by guoxx on 2019/6/26.
 */
public class RequestEncodeDrl {


    private static final int KEY_SIZE = 1024;
    private static final String PUBLIC_KEY = "ab0a0a6473d1891d388773574764b239d4ad80cb2fd3a83d81d03901c1548c13fee7c9692c326e6682b239d4c5d0021d1b607642c47ec29f10b0602908c3e6c9"; // 指数
    private static final String PRIVATE_KEY = "1ad9f652ce5cd6df8b02a7a8da74bc1f7744fa999994d51f67c7a8c62d5accc736b37f3f117d9ef0b5003262136c9c0daf53d550f194ea125caf6f041ca04d496d772891d028078d3cdef853fede4d115ada2ae59192851acf363b8629b6ad85b39f10f92eac6123d57280a8ffbd3e5daa642d6cb19ccb1b5047fc8238e63e75";
    private static final String MODULES = "23c3c8cb41c47dd288cc7f4c218fbc7c839a34e0a0d1b2130e87b7914936b120a2d6570ee7ac66282328d50f2acfd82f2259957c89baea32547758db05de9cd7c6822304c8e45742f24bbbe41c1e12f09e18c6fab4d078065f2e5aaed94c900c66e8bbf8a120eefa7bd1fb52114d529250084f5f6f369ed4ce9645978dd30c51";
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
            return tmp[0] + "&" + "m=" + URLEncoder.encode(encodeDrl(new String(text,"utf-8")));

        } else if (hostAndURL.startsWith("v.data.mob.com") &&
                (hostAndURL.endsWith("cpl") || hostAndURL.endsWith("vpl"))) {
            byte[] text = params.getBytes();
            return encodeDrl(new String(text,"utf-8"));
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

    public static String encodeDrl(String json) throws Exception {
        AES aes = new AES(RequestDecodeDrl.sAesKey);
        byte[] data = aes.encode(json.getBytes("utf-8"));
        return Base64.encode(data).replaceAll("\n", "");
    }
}
