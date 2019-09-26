package com.cn.ceshi.v4conf;

import com.cn.ceshi.util.AllUtil2;
import com.lamfire.json.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.StringUtils;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 本地模拟v4/cconf接口发送请求
 * Created by guoxx on 2019/3/21.
 */
public class V4ConfDemo {

    private static PoolingHttpClientConnectionManager poolConnManager;
    private static final int maxTotalPool = 200;
    private static final int maxConPerRoute = 20;
    private static final int socketTimeout = 2000;
    private static final int connectionRequestTimeout = 3000;
    private static final int connectTimeout = 1000;

    private static final Logger LOGGER = LogManager.getLogger(V4ConfDemo.class.getName());
    //接口参数
    private static String aseBody = "appkey=moba6b6c6d6&plat=1&apppkg=com.common.tj&appver=1.0&networktype=wifi&duid=2897c2a7bfb8aa465e4e073a73f4c0ad21fbecb8";
    //host
    private static String host = "172.25.48.193:8085";
    //urlAddr
    private static String uri = "/v4/cconf";
    //接口访问url
    private static String url = "http://" + host + uri+"?"+aseBody;

    private static int code = 0;


    public static JSON get(String url){
        JSON result = new JSON();

        HttpGet httpGet = new HttpGet(url);

        httpGet.setHeader("x-forwarded-for","1.58.73.213");

        int status = -111;
        try {
            CloseableHttpResponse response = getConnection().execute(httpGet);
            status = response.getStatusLine().getStatusCode();
            HttpEntity backEntity = response.getEntity();
            if (status == 200) {
                InputStream resStream = backEntity.getContent();
                BufferedReader br = new BufferedReader(new InputStreamReader(resStream));
                StringBuffer resBuffer = new StringBuffer();
                String resTemp = "";
                while ((resTemp = br.readLine()) != null) {
                    resBuffer.append(resTemp);
                }
                result.put("result", resBuffer.toString());
            } else {
                //记日志2
                LOGGER.error("[url]=" + url + "   [status=]=" + status);
            }

        } catch (Exception e) {
            e.printStackTrace();
            //记日志3
            LOGGER.error("[url]=" + url);
            httpGet.abort();
        }
        result.put("status", status);
        return result;
    }

    public static void  getResult(JSON backDataJSON){

        JSON tmp = new JSON();
        //头过滤
        Map<String, String> heards = new HashMap<String, String>();
        heards.put("x-forwarded-for","1.58.73.213");
        heards.put("connection","Keep-Alive");
        heards.put("user-identity","APP/com.mob.test.COMMON;19.03.20 SYS/Android;22 SDI/5593fa0495d08e1ebb0c38efe79f9167e6011c2a FM/Meizu;m2+note NE/wifi;-1 Lang/zh_CN CLV/20190320 SDK/MOBPUSH;10701 DC/2 P/GROWSOLUTION;1001");
        heards.put("accept-encoding","gzip");
        heards.put("user-agent","Dalvik/2.1.0 (Linux; U; Android 5.1; m2 note Build/LMY47D)");


        JSON forDes = new JSON();
        String[] params = aseBody.split("&");
        for(String param:params){
            String[] aparam = param.split("=");
                forDes.put(aparam[0],aparam[1]);
        }

        String pbackData2Db = "";
        if (backDataJSON.getInteger("status") == 200) {
            code = 1;
            String aseBackData = backDataJSON.getString("result");
            if (!StringUtils.isEmpty(aseBackData)) {
                //b.返回数据入库
                JSON outData = AllUtil2.outDecode("m.data.mob.com", "m.data.mob.com/v4/cconf", heards, aseBackData, forDes);

                pbackData2Db = outData != null ? outData.toJSONString() : null;
            } else {
                tmp.put("resultData", "请求成功，返回数据为null");
                pbackData2Db = tmp.toJSONString();
            }

        } else {
            tmp.put("resultData", "http/https  fail");
            pbackData2Db = tmp.toJSONString();
        }

        System.out.println(pbackData2Db);
    }

    private static CloseableHttpClient getConnection() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .setConnectTimeout(connectTimeout)
                .setSocketTimeout(socketTimeout).build();
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(poolConnManager)
                .setDefaultRequestConfig(requestConfig).build();
        return httpClient;
    }
    public static void main(String args[]){
       JSON backData =  get(url);
       getResult(backData);

    }
}
