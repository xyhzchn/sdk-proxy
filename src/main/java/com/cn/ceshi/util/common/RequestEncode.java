package com.cn.ceshi.util.common;

import com.lamfire.code.AES;
import com.lamfire.code.Base64;
import com.lamfire.code.MD5;
import com.lamfire.json.JSON;
import com.lamfire.utils.ZipUtils;
import com.mob.util.crypt.rsa.MobRSACrypter;

import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.ByteBuffer;

/**
 * Created by huangwt on 2018/3/2.
 * 公共库加密
 */
public class RequestEncode {


    private static final int KEY_SIZE = 1024;
    private static final String PUBLIC_KEY = "ceeef5035212dfe7c6a0acdc0ef35ce5b118aab916477037d7381f85c6b6176fcf57b1d1c3296af0bb1c483fe5e1eb0ce9eb2953b44e494ca60777a1b033cc07"; // 指数
    private static final String PRIVATE_KEY = "d7e1d83158c5b0f269e78c7e5d688021ed6cad00052483d74db5fd1d9c9cd96e2e15e75ba10a22448ee1cee1a8506197f098074caa352b9cad2c708f196954e2a2e35e1bdc228d9ce79830583fe8d2038ba7e6282700ba5f31052eaae2c648591332335968ec3c80a4c723f50fd3f729d1130a17aaec5486234b7d0b2be7b7f";
    private static final String MODULES = "191737288d17e660c4b61440d5d14228a0bf9854499f9d68d8274db55d6d954489371ecf314f26bec236e58fac7fffa9b27bcf923e1229c4080d49f7758739e5bd6014383ed2a75ce1be9b0ab22f283c5c5e11216c5658ba444212b6270d629f2d615b8dfdec8545fb7d4f935b0cc10b6948ab4fc1cb1dd496a8f94b51e888dd";
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
        if (url.indexOf("/v4") > -1 || url.indexOf("/v5") > -1) {
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
        if (hostAndURL.startsWith("c.data.mob.com") && hostAndURL.endsWith("cdata") || hostAndURL.startsWith("l.gm.mob.com")&& hostAndURL.endsWith("gcl")) {
            String[] tmp = params.split("&");
            String m_val = tmp[1].substring(2);
            byte[] text = ZipUtils.gzip(m_val.getBytes());
            return tmp[0] + "&" + "m=" + URLEncoder.encode(encode(text));

        } else if (hostAndURL.startsWith("v.data.mob.com") &&
                (hostAndURL.endsWith("cpl") || hostAndURL.endsWith("vpl"))) {
            byte[] text = params.getBytes();
            return encode(text);
        } else if (hostAndURL.endsWith("rdata")) {
            return new String(Base64.decode(params));
        }

        return params;
    }

    private static String encode(byte[] text) throws Exception {
        AES aes = new AES(AES_KEY);
        byte[] data = aes.encode(text);
        MobRSACrypter defRsaCrypter = new MobRSACrypter(RSA_MODULES, RSA_PRIVATE, RSA_PUBLIC, KEY_SIZE);
        byte[] aesKeyRSA = defRsaCrypter.encodeByPublicKey(AES_KEY);
        byte[] mBytes = new byte[8 + data.length + aesKeyRSA.length];
        ByteBuffer b = ByteBuffer.allocate(4);
        b.putInt(aesKeyRSA.length);
        System.arraycopy(b.array(), 0, mBytes, 0, 4);
        System.arraycopy(aesKeyRSA, 0, mBytes, 4, aesKeyRSA.length);
        b = ByteBuffer.allocate(4);
        b.putInt(data.length);
        System.arraycopy(b.array(), 0, mBytes, 4 + aesKeyRSA.length, 4);
        System.arraycopy(data, 0, mBytes, 8 + aesKeyRSA.length, data.length);
        return Base64.encode(mBytes).replaceAll("\n", "");
    }
}
