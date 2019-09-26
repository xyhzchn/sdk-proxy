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
 *
 * Created by guoxx on 2019/6/26.
 */
public class RequestDecodeDrl {


    public static byte[] sAesKey = null;

    public static JSON decodeDataDrl(String host_origin, String requestUrl, Map<String, String> headers, String httpBody, JSON dynamicSsecretKey){
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
                                sAesKey = RequestDecodeDrl.RSAUtils.decodebyPrivateKey(sAesKey);
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

    public static JSON decodeDrl(String json) throws Exception {
        JSON jsonData = new JSON();
        AES aes = new AES(sAesKey);
        byte[] data = aes.decode(Base64.decode(json));
        String dataStr = new String(data);
        if(!StringUtils.isBlank(dataStr)){
            jsonData = Response.successJSON(dataStr);
        }
        return jsonData;
    }

    static class RSAUtils {

        private static final int KEY_SIZE = 1024;
        private static final String PUBLIC_KEY = "ab0a0a6473d1891d388773574764b239d4ad80cb2fd3a83d81d03901c1548c13fee7c9692c326e6682b239d4c5d0021d1b607642c47ec29f10b0602908c3e6c9"; // 指数
        private static final String PRIVATE_KEY = "1ad9f652ce5cd6df8b02a7a8da74bc1f7744fa999994d51f67c7a8c62d5accc736b37f3f117d9ef0b5003262136c9c0daf53d550f194ea125caf6f041ca04d496d772891d028078d3cdef853fede4d115ada2ae59192851acf363b8629b6ad85b39f10f92eac6123d57280a8ffbd3e5daa642d6cb19ccb1b5047fc8238e63e75";
        private static final String MODULES = "23c3c8cb41c47dd288cc7f4c218fbc7c839a34e0a0d1b2130e87b7914936b120a2d6570ee7ac66282328d50f2acfd82f2259957c89baea32547758db05de9cd7c6822304c8e45742f24bbbe41c1e12f09e18c6fab4d078065f2e5aaed94c900c66e8bbf8a120eefa7bd1fb52114d529250084f5f6f369ed4ce9645978dd30c51";
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
