package com.cn.ceshi.util.didiking2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.UnsupportedEncodingException;

public class AESUtil {

    private static Logger log = LogManager.getLogger(AESUtil.class.getName());

    private static final byte[] key = "SeVQtpynIq6ZddLN".getBytes();


    /**
     * AES加密算法
     */
    private AESUtil() {
    }

    /**
     * 加密
     *
     * @param content 需要加密的内容
     * @return byte[] 加密后的字节数组
     */
    public static String encrypt(String content) {
        try {
            return Decoders.encodeContent(key, content);
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 解密
     *
     * @param base64content 待解密内容
     *                      解密密钥
     * @return byte[]
     */
    public static String decrypt(String base64content) {
        String backMessage = null;
        try {
            backMessage = Decoders.decodeData(base64content);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return backMessage;
    }

}