package com.cn.ceshi.util.common;

import com.cn.ceshi.cache.DiskCache;
import com.cn.ceshi.model.RsaInfoConfig;
import com.cn.ceshi.util.data_collector.Response;
import com.cn.ceshi.util.data_collector.SMRSA;
import com.lamfire.code.AES;
import com.lamfire.code.Base64;
import com.lamfire.json.JSON;
import com.lamfire.utils.Bytes;
import com.lamfire.utils.StringUtils;
import com.lamfire.utils.ZipUtils;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by guoxx on 2019/1/14.
 */
public class RequestDecodeRsa {

    public static ConcurrentHashMap<String, byte[]> sAesKeyMap = new ConcurrentHashMap<>();

    public static JSON decodeDataRsa(String requestUrl, String httpBody, boolean zip) {
        if (!StringUtils.isBlank(requestUrl) && !StringUtils.isBlank(httpBody)) {
            try {
                RSAUtils utils = new RSAUtils(requestUrl);
                System.out.println("wenjun test rsaNotFound: " + utils.rsaNotFound);
                if (utils.rsaNotFound) {
                    return null;
                }
                byte[] bytes = Base64.decode(httpBody);
                if (bytes != null) {
                    byte[] sAesKey = null;
                    try {
                        byte[] result = new byte[4];
                        System.arraycopy(bytes, 0, result, 0, 4);
                        int secretKeyLen = Bytes.toInt(result);
                        if (secretKeyLen >= 0 && secretKeyLen < bytes.length) {
                            sAesKey = new byte[secretKeyLen];
                            System.arraycopy(bytes, 4, sAesKey, 0, secretKeyLen);
                            sAesKey = utils.decodebyPrivateKey(sAesKey);
                            byte[] dataLenBytes = new byte[4];
                            System.arraycopy(bytes, 4 + secretKeyLen, dataLenBytes, 0, 4);
                            int dataLen = Bytes.toInt(dataLenBytes);
                            if (dataLen >= 0 && dataLen < bytes.length) {
                                byte[] data = new byte[dataLen];
                                System.arraycopy(bytes, 8 + secretKeyLen, data, 0, dataLen);
                                AES aes = new AES(sAesKey);
                                bytes = aes.decode(data);
                                if (zip) {
                                    bytes = ZipUtils.ungzip(bytes);
                                }
                            }
                        }
                    } catch (Throwable var9) {
                        throw var9;
                    }

                    String result1 = new String(bytes, Charset.forName("utf-8"));
                    result1 = result1.trim();
                    System.out.println("wenjun test decode: " + result1);
                    if (!StringUtils.isBlank(result1)) {
                        sAesKeyMap.put(requestUrl, sAesKey);
                        return Response.successJSON(result1);
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String encodeByOriginAesKey(String requestUrl, String json) {
        try {
            byte[] aesKey = sAesKeyMap.get(requestUrl);
            if (aesKey != null) {
                AES aes = new AES(aesKey);
                byte[] data = aes.encode(json.getBytes("utf-8"));
                return Base64.encode(data).replaceAll("\n", "");
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    public static JSON decodeByOriginAesKey(String requestUrl, String json) {
        try {
            byte[] aesKey = sAesKeyMap.get(requestUrl);
            if (aesKey != null) {
                JSON jsonData = new JSON();
                AES aes = new AES(aesKey);
                byte[] data = aes.decode(Base64.decode(json));
                String dataStr = new String(data, "utf-8");
                if (!StringUtils.isBlank(dataStr)) {
                    jsonData = Response.successJSON(dataStr);
                }
                return jsonData;
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    static class RSAUtils {
        private static final int KEY_SIZE = 1024;
        //唤醒解密
        private BigInteger RSA_PUBLIC = null;
        private BigInteger RSA_PRIVATE = null;
        private BigInteger RSA_MODULES = null;
        public boolean rsaNotFound = true;

        public RSAUtils(String requestUrl) {
            RsaInfoConfig info = DiskCache.getRsaInfo(requestUrl);
            if (info != null) {
                rsaNotFound = false;
                RSA_PUBLIC = new BigInteger(info.getPublicKey(), 16);
                RSA_PRIVATE = new BigInteger(info.getSecretKey(), 16);
                RSA_MODULES = new BigInteger(info.getModules(), 16);
            }
        }

        public byte[] decodebyPrivateKey(byte[] source) throws Exception {
            SMRSA rsa = new SMRSA(KEY_SIZE);
            return rsa.decode(source, RSA_PRIVATE, RSA_MODULES);
        }

        public byte[] encodeByPublicKey(byte[] source) throws Exception {
            SMRSA rsa = new SMRSA(1024);
            return rsa.encode(source, RSA_PUBLIC, RSA_MODULES);
        }

        public void main(String[] args) throws Exception {
            String ss = "测试";
            byte[] mi = encodeByPublicKey(ss.getBytes("UTF-8"));
            System.out.println(new String(decodebyPrivateKey(mi), "UTF-8"));
        }
    }
}
