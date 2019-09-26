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
 * Created by guoxx on 2019/1/14.
 */
public class RequestDecodeFnc {

    public static byte[] sAesKey = null;

    public static JSON decodeDataFnc(String host_origin, String requestUrl, Map<String, String> headers, String httpBody, JSON dynamicSsecretKey){
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

    public static JSON decodeFnc(String json) throws Exception {
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
        private static final String PUBLIC_KEY = "9e87e8d4b8f52f2916d0fb4342aa6b54a81a05666d0bdb23cc5ebf3a07440bc3976adff1ce11c64ddcdbfc017920648217196d51e3165e780e58b5460c525ee9"; // 指数
        private static final String PRIVATE_KEY = "897db96be986a144120b4b3bd8235bb0d72a56d93b10df76b588441aaa240440c6ad94e3b13db8886ec51973bebcea30346f29a93166ab71c2287062ab1f3fb679237274a7fc3adec895d8d5c6fe29c985d609099fe538c195aee457e783189940804ecd7533df231d3640d4d9a307817894faa094e56df6837e654b7e37a89";
        private static final String MODULES = "13bda4b87eb42ab9e64e6b4f3d17cf8005a4ae94af37bc9fd76ebd91a828f017c81bd63cbe2924e361e20003b9e5f47cdac1f5fba5fca05730a32c5c65869590287207e79a604a2aac429e55f0d35c211367bd226dd5e57df7810f036071854aa1061a0f34b418b9178895a531107c652a428cfa6ecfa65333580ae7e0edf0e1";
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
