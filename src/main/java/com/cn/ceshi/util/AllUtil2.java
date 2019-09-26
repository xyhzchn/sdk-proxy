package com.cn.ceshi.util;

import com.cn.ceshi.impl.DecodeMapUtil;
import com.cn.ceshi.util.data_collector.MDecoder;
import com.lamfire.code.Base64;
import com.lamfire.json.JSON;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangly on 2017/8/30.
 */
public class AllUtil2 {

    private static final Map<String, Class<?>> map = new HashMap<String,  Class<?>>();
    static {
        //1--didking
        Map<String, String>  didking =  com.cn.ceshi.util.didiking2.DecodeMapUtil.getDecodeMap();
        putMap(didking);
        //2--data_collector
        Map<String, String>  data_collector = DecodeMapUtil.getDecodeMap();
        putMap(data_collector);
        //3--sharesdk_collector
        Map<String, String>  sharesdk_collector =  com.cn.ceshi.util.sharesdk.DecodeMapUtil.getDecodeMap();
        putMap(sharesdk_collector);
        //4-bbsdk_collector
        Map<String, String>  bbsdk_collector =  com.cn.ceshi.util.bbssdk.DecodeMapUtilImpl.getDecodeMap();
        putMap(bbsdk_collector);
    }

    private static void  putMap( Map<String, String> kValue){
        for(Map.Entry<String,String> obj:kValue.entrySet()){
            try {
                Class<?> clazz = Class.forName(obj.getValue());
                map.put(obj.getKey(),clazz);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     *
     * @param host_origin
     * @param requestUrl
     * @param headers
     * @param httpBody
     * @param dynamicSsecretKey
     * @return
     *
     //success= 1-响应参数无误  0-响应参数错误
    //resultData=响应明文数据
    //errorMsg=响应参数错误说明
    //passDes -1=不经过解密  0=解密失败  1-解密成功
    {"resultData":Obj,"success":1,"passDes":0}
    {"resultData":Obj,"success":0,"errorMsg":"缺少参数userName","passDes":0}

     */
    public static JSON inDecode(String host_origin,String requestUrl, Map<String, String> headers,String httpBody, JSON dynamicSsecretKey){
        Class<?> clazz = map.get(host_origin);
        int passDes = -1;
        JSON json = new JSON();
        if(clazz!=null){
            json.put("passDes",passDes=0);
            try {
                Method method = clazz.getMethod("inDecode",String.class,Map.class,String.class,JSON.class);
                JSON jsonTmp = (JSON) method.invoke(clazz.newInstance(),requestUrl,headers,httpBody,dynamicSsecretKey);
                //解密返回null
                if(jsonTmp == null){
                    json.put("resultData",httpBody);
                    json.put("success",1);
                    json.put("passDes",-2);
                    return json;
                }
                json = jsonTmp;
                json.put("passDes",passDes=1);
                return json;
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        json.put("resultData",httpBody);
        json.put("success",1);//1-参数无误  0-参数错误
        json.put("passDes",passDes);
        return json;
    }

    public static JSON outDecode(String host_origin,String requestUrl, Map<String, String> headers, String httpBody,
                                 JSON needKey){
        Class<?> clazz = map.get(host_origin);
        JSON result = new JSON();
        int passDes = -1;
        if(clazz!=null){
            result.put("passDes",passDes=0);
            try {
                Method method = clazz.getMethod("outDecode",String.class,Map.class,String.class,JSON.class);
                result = (JSON) method.invoke(clazz.newInstance(),requestUrl,headers,httpBody,needKey);
                result.put("passDes",passDes=1);
                return result;
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        result.put("resultData",httpBody);
        result.put("success",1);//1-参数无误  0-参数错误
        result.put("passDes",passDes);
        return result;
    }

    public static void main(String[] args) {
        String base64 = "AAAAhAAAAIAN6F0WuLxSH8iTVIoijKa4XgC0JDwW%2FZiz6hAGOWEd5pcAPbyWmKJVTplTWk5y3S4z%2Bv7GiSBNDv%2BDoSk1b1pdpIvvQUmn1i4dCA2n5DIihTvSOvKAPNIO5IvHSgPLq3OoRHQ7EX4%2BA2kGWXvsNmTpl4hoP9H3y8%2BX0AyVeqbIYQAAB9BZBV%2B0Z61fUw%2FrNjAfv9%2F4pYQYGKkvYSF0SfSy%2F%2FD0Kb9ZQsRk9ojAtleGlp1psyAY7Nwma7xtqvO9QGQG4rDR2%2Fy0GR1aWDEIx58oh8cRN7zqC%2BR5il4qzjcHWHZfLu2weMHv6tOcASvnIKDmOF9quq042ZCb9eMNK22QqCttujzzYRlSsJd22%2BljUlygCwskg1Wl0%2FdmSrwjv9qI5uiKUr3q3egrFEd3r5pLwxQRX%2Bf8XLjexAcquaaM0KBu00pssXL6T%2FlHNCMDW3pN3tfmS9anUGZCTV1nuZ28aMe9PW48OP6YzxuSM%2BKGzDmrKsKbZvGYdGjHr%2B35vWEQX7TnSrHkwwKkU24q3AdfAl%2FGu3Srt%2BXvxzJLgDEwbN%2FF7usmIuC0JLrSwvBua4TAUbbq%2F5pEeC4WxMr9a2Benkb2k0XybHLGXeAEemywA6h4OG7P0W1wCBiGjbro1gfWefofGUXEzgZZ2NysVtXWpPBQoY1ccasoyowN%2BvWBO4qKros71Vq9WWwlT2OEyGaOgmmNiw3lb0KBdc%2FDWn%2F63Bb5ROaOT7rYjSIU7ac44JjOEiDr0%2BQwMfKQLmQQShaKZXY4d2%2BK7XWgjyYL4PRaANa9bQezbRjEtVBjleTyBQnx0OxtYpdNu%2B2l4wSIea2j1CG7LQWPSYBUPT39HLp5gpVObSDCPKPcNiki84DFDBfh6yAKXGoWYrSG0cU2R%2FktTYauhBz58i8P9KAI2dQKjzfDAR5hiwDuEE4An8fHY4OQJkAkggbKY9%2BEBzxtPuB%2BX8aJ3EuPF9T91bOD%2FLX0zX0fAxit5WWyT540oGJAFe7vFMWzouBdwg3VxCbuy6bTsC5u%2B8pyWbfpMRhsr8NawqsFp0Ala7cnt9NPoRB6mQ2en39Mvn3NlkNGigZMDS%2F4vUak9YKN9lrm4ZdoSv9dDYchQUX%2BBAktKUynjyAiLIgkgMReo2Mtxv6TMAGQoxNbje3F6MIFVbKsJM3sTvKNmfoYCU%2FxmoxBn4TToicDiavVcGG%2F%2FeWZD9RF7b585UuF9VfwrRfAUi38W82e2v%2F36VxDmg8fEckNYG%2Bvo1Ysr9Pb6xfHYwAFDI7udX7fBOqkfvVLQ6x5x7H6l5NbfQcSRbdUGQKuk1YEPau4p4AUstuviHQ%2FVqlqmgHN95w%2Fx6sGSbAbqKcjX8zv9fD%2F0Tn892wAjYvt3lWwO4TGiUF8F%2FLj8U%2BN94JJEonN0OklZJ4Hz8VRcsw7i6MJu9Rci1KqzoE3Q0xAcTnKhm9JZnw8g4IoriEtPHksyC2p%2B5B6Rgu7gaTqY9ktVvFUYPMA6MFR8UBiJr%2F99gjR4FxtrbHBvnq%2BLKC0ZFcy4ukSPKIoZbj7N8OuzoIQBoJRrT8Z3iaCXeVtrz%2BUfs5PWYbBz2UKdmeyffMslVPzqOZKuKmlAykFjcCwFBzcSdUgtr3FbnXbEXnYnsQHTht4bh1y5r%2FxmMA3FYZ%2B%2Fxte7eMnsHBAU5dSJXj8Wtd7CxetySePDt5UHIsBcgDjt7DM7AKJR8rQE8DAPKajK6WDxM44aNS7uAcJB4YJQzteryMzF7dkhVidCpIwXx1F0WItYO11vxiReb%2Brv2HZK1e0RNHYBlKiWlcvwtCm3n6isjiaI2nqyne4%2FYIsR14boQ2bl4OaVXCUvULqXK79Y7pQ30z17Ly17R%2BunNFhqqnC%2Bk9OpycvQVI8GIw%2FSTNEDIO6JGhZc5O3efjtC0N93e8qhmh2LJWCE%2FjlkfixHSbtj%2Fc%2FwIEXPtmOIdqxandIad1GWeoM%2FpHJhfbyaRB1JpZ9udPHPSXpg16vdHE0R37P6HoVoxSqF5TGZ3udkhMzXdbxh6jpUyCX%2FoNmvaGFU4o%2FHMo3ZocGr9Ke%2Fa3uC3kp%2FVJG9kxjuXOUDkKhOXYMxVcQG%2FOvcbbjIDG3UeMEHxLmJLaqS6x7hhcp%2BbuH%2Fo1sFNasaMSyBjO3LboiqzgIobn924kiUZ0YveOIV5l1EgPj%2BD1vPYejJ6KbWqg5aNSNVtqqVW9zgKY0zHxF%2BkrmKTMhTP9f4T8SQAAmwNcS14eFcACrHvvHGzoRS4OAIpbdb1EfQdSipeNEl5gJjCWb1DS3PMEfO8lwkGfULfnjZRuBLBojGxHXaCoowBUU8pEqWC3sDGGcmxTkI1yY81AHFmgLB%2Fb7qcXnJbpxluH7xOS7CRTVdDhCSqVr4FNal6lckytEFZmVkr4RrT9EvgoLmUqlzvOlT4Pk9dLHUk2ZNFhymzmVb3OVvdj%2FR%2Fup4J7LEIRWQHgY84JVGVF375fhHvqoj3YW80T4gY1rKkg4tXmqzbUvn78zYcATdk8VvCQ7gUmPbNiPLR4WQPi8aSLkYnFMBqzLChUBz%2Bv8D%2FBe6hFM4Ejp9y%2BhyobEWtVHuXZAPtFEo0VEstdzn4bEagzhSdLL6u2TYzzzFyvzjKRAQUx8Lcj3u6okXGDu0HwX%2BYqHT%2FtDwBourRDyU5%2Fd7xq%2BbZkCHG5irtwKv3hLfSlkXz9gHd17rnYIV7RIeJnlW56ZHe0fiAL1iaXxMGrxMqJVGrhsv3xZBWWxSYErsewdMScbXz0O3gRUz5JIgCeNqzxeX8YcyLxVRWfoS7nB6hYWY01%2Fbnam1yYnKFkn%2B5ayWeg9ISSZuKFJlV9AQj%2FcbKBEcmcucdRiKrrII0weeQ%3D%3D";
//        try {
////            base64 = URLDecoder.decode(base64,"UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        try {
            String s = MDecoder.collectorDecodeV2Data(base64);
            System.out.println(s);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
