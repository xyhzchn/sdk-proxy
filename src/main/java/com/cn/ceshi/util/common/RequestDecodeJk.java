package com.cn.ceshi.util.common;

import com.cn.ceshi.util.data_collector.Response;
import com.cn.ceshi.util.data_collector.SMRSA;
import com.lamfire.code.AES;
import com.lamfire.code.Base64;
import com.lamfire.json.JSON;
import com.lamfire.utils.Bytes;
import com.lamfire.utils.StringUtils;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Created by guoxx on 2019/9/26.
 */
public class RequestDecodeJk {

    public static byte[] sAesKey = null;

    public static JSON decodeDataJk(String host_origin, String requestUrl, Map<String, String> headers, String httpBody, JSON dynamicSsecretKey){
        if(!StringUtils.isBlank(requestUrl) && !StringUtils.isBlank(httpBody)){
            try{
                    byte[] bytes = Base64.decode(httpBody);
                    if(bytes == null) {
                        return null;
                    } else {
                        try {
                            label32: {
                                byte[] result = new byte[4];
                                System.arraycopy(bytes, 0, result, 0, 4);
                                int secretKeyLen = Bytes.toInt(result);
                                if(secretKeyLen >= 0 && secretKeyLen < bytes.length) {
                                    sAesKey = new byte[secretKeyLen];
                                    System.arraycopy(bytes, 4, sAesKey, 0, secretKeyLen);
                                    sAesKey = RSAUtils.decodebyPrivateKey(sAesKey);
                                    byte[] dataLenBytes = new byte[4];
                                    System.arraycopy(bytes, 4 + secretKeyLen, dataLenBytes, 0, 4);
                                    int dataLen = Bytes.toInt(dataLenBytes);
                                    if(dataLen >= 0 && dataLen < bytes.length) {
                                        byte[] data = new byte[dataLen];
                                        System.arraycopy(bytes, 8 + secretKeyLen, data, 0, dataLen);
                                        AES aes = new AES(sAesKey);
                                        bytes = aes.decode(data);
//                                        bytes = ZipUtils.ungzip(bytes);
                                        break label32;
                                    }

                                    return null;
                                }

                                return null;
                            }
                        } catch (Exception var9) {
                            throw var9;
                        }

                        String result1 = new String(bytes, Charset.forName("utf-8"));
                        result1 = result1.trim();
                        System.out.println("decode: " + result1);
                        if(!StringUtils.isBlank(result1)){
                            return Response.successJSON(result1);
                        }
                    }
            }catch (Exception e){

            }
        }
        return null;
    }

    public static JSON decodeJk(String json) throws Exception {
        JSON jsonData = new JSON();
        AES aes = new AES(sAesKey);
        byte[] data = aes.decode(Base64.decode(json));
        String dataStr = new String(data,"utf-8");
        if(!StringUtils.isBlank(dataStr)){
            jsonData = Response.successJSON(dataStr);
        }
        return jsonData;
    }

    static class RSAUtils {

        private static final int KEY_SIZE = 1024;
        //唤醒解密
        private static final String PUBLIC_KEY = "e566828c25d8ac4b588217c0a437ace230917f1135abf7d98ef9346e5d8e4f99fc4d6c21bc818c0fffd6db3e3a1aca947feb987003c104d2ab803f7f06726177"; // 指数
        private static final String PRIVATE_KEY = "9cb54a4700937a68414e52d6d2e57ac6ea1fafb23bcbbbe18e133d7d11b7b5f9c670371ffa226e6442dfdb616192e75e2d553c26042867a5021856bdc236053233a5a257d59719b05afde80ff6544c759cfc59165b1bb68c0d2e442d27a59b1150bd83a7ae8e13e798bf294e20841d011c997ac4deba050833fc2bf45486e7";
        private static final String MODULES = "21af4ddbf8167dd7a7af6de3e61ec55783db252d983aeab13cacc8159437e05c59645b2319241b0f0b4a030f6023fe00c2b633d9a1e17f4e2296d30338b0d3ca0b4285bc342004f9b8a4e2696f21507717f9c396ec7d9775a4fe429d542f4bf66fc445672a7a7f3b3d96fc556b86621086c3f74967a4d5da1e2a318d87805e9b";
        private static final BigInteger RSA_PUBLIC = new BigInteger(PUBLIC_KEY, 16);
        private static final BigInteger RSA_PRIVATE = new BigInteger(PRIVATE_KEY, 16);
        private static final BigInteger RSA_MODULES = new BigInteger(MODULES, 16);

        public RSAUtils() {
        }

        public static byte[] decodebyPrivateKey(byte[] source) throws Exception {
            SMRSA rsa = new SMRSA(KEY_SIZE);
            return rsa.decode(source, RSA_PRIVATE, RSA_MODULES);
        }

        public static byte[] encodeByPublicKey(byte[] source) throws Exception {
            SMRSA rsa = new SMRSA(1024);
            return rsa.encode(source, RSA_PUBLIC, RSA_MODULES);
        }

        public static void main(String[] args) throws Exception {
            String ss = "测试";
            byte[] mi = encodeByPublicKey(ss.getBytes("UTF-8"));
            System.out.println(new String(decodebyPrivateKey(mi), "UTF-8"));
        }
    }
}
