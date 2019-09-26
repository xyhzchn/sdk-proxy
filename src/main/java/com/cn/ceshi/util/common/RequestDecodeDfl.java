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
 * Created by guoxx on 2019/8/28.
 */
public class RequestDecodeDfl {

    public static byte[] sAesKey = null;

    public static JSON decodeDataDfl(String host_origin, String requestUrl, Map<String, String> headers, String httpBody, JSON dynamicSsecretKey){
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

    public static JSON decodeDfl(String json) throws Exception {
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
        private static final String PUBLIC_KEY = "cc44c3c73baa392b87b171911500f0284398c340a8bdc88003ca870d6b6f67f267b24d51c4991d798f42f4a59a453036501c7091a875fd3e2f173ef6f3bb81d7"; // 指数
        private static final String PRIVATE_KEY = "1bf5f476dd770e321973e07c9bbc42d6e6a96a5af3802c90e0f007841f422ea220c76813354ea4f27aa2aca2a324d135b22b099a77085158b606a6304ef837bae1d53f2b3fe9990b395b4bbf894aa251dd87bc2fd775637ba785f7d74653f3b8cb4cc50820320e15181e23322ab2239062e5d41e8bf3a18ce33d785dde5770ff";
        private static final String MODULES = "1e5ed3687766ae453b9ed491058d63429d3e1ce3d7a3935b04a429339744c0407364282b7675df3d71def64b836c7f4f10279473ec677d47c44b96a7702ed972a601ae4a2d7ac8092e0c79e9fd659a69a7fb4c22204996fed792d7f45d2664817041edaf1cacc7b24d22676cb240cbce93516631a3eed5729867664aad1c57bb";

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
