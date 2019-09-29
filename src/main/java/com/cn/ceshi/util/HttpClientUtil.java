package com.cn.ceshi.util;

import com.cn.ceshi.cache.DiskCache;
import com.cn.ceshi.model.UrlProxyConfig;
import com.lamfire.json.JSON;
import com.lamfire.json.deserializer.JSONObjectDeserializer;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.TextUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.swing.plaf.TextUI;

import org.apache.http.conn.ssl.TrustStrategy;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.http.ssl.SSLContextBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

public class HttpClientUtil {

    private static final Logger LOGGER = LogManager.getLogger(HttpClientUtil.class.getName());

    private HttpClientUtil() {
    }

    private static PoolingHttpClientConnectionManager poolConnManager;
    private static final int maxTotalPool = 200;
    private static final int maxConPerRoute = 20;
    private static final int socketTimeout = 2000;
    private static final int connectionRequestTimeout = 3000;
    private static final int connectTimeout = 1000;

    static {
        try {
            SSLContextBuilder builder = new SSLContextBuilder();
            // 全部信任 不做身份鉴定
            builder.loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    return true;
                }
            });
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build(),
                    new String[]{"SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.2"},
                    null,
                    NoopHostnameVerifier.INSTANCE);


            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                    .<ConnectionSocketFactory>create()
                    .register("http",
                            PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", sslsf).build();
            poolConnManager = new PoolingHttpClientConnectionManager(
                    socketFactoryRegistry);
            // Increase max total connection to 200
            poolConnManager.setMaxTotal(maxTotalPool);
            // Increase default max connection per route to 20
            poolConnManager.setDefaultMaxPerRoute(maxConPerRoute);
            SocketConfig socketConfig = SocketConfig.custom()
                    .setSoTimeout(socketTimeout).build();
            poolConnManager.setDefaultSocketConfig(socketConfig);
        } catch (Exception e) {

        }
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

    public static JSON get(HttpServletRequest request, String aseBody) {
        JSON result = new JSON();
        String host_origin = request.getHeader("host_origin");

        System.out.println("[postForBody][get][Host origin]: " + host_origin);
        //转发公共库的请求
//        host_origin = replaceHost(host_origin);

        String uri = request.getRequestURI();
        uri = uri.substring(5);

        String url = "";
        String orgin_url = host_origin + uri;
        String tmp = checkAndReplaceRequestUrl(orgin_url);
        if (tmp.equals(orgin_url)) {//如果相等，走原来的逻辑
            url = "http://" + host_origin + uri+"?"+aseBody;
        } else {//否则，走配置中配置替换的url
            url = "http://" + tmp + "?" + aseBody;
        }

        System.out.println("[postForBody][get][url]: " + url);

        HttpGet httpGet = new HttpGet(url);

        Enumeration<String> hs = request.getHeaderNames();
        while (hs.hasMoreElements()) {
            String name = hs.nextElement();
//            httpGet.setHeader(name, request.getHeader(name));
            switch (name) {
                case "transfer-encoding":
                    break;
                case "host":
                    httpGet.setHeader("host", host_origin);
                    break;
                case "content-length":
                    break;
                default:
                    httpGet.setHeader(name, request.getHeader(name));
                    break;
            }
        }

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

    //转发公共库请求地址
    private static String replaceHost(String host_origin) {
        if(host_origin.equals("f.gm.mob.com") || host_origin.equals("l.gm.mob.com")){
            host_origin = "10.21.131.11:8088/mob-data-collector";
//            host_origin = "10.6.98.230:8080/data_collecter";
        }

        if(host_origin.equals("api.df.mob.com")){
            host_origin = "10.21.141.52:9000";
        }
        return host_origin;
    }

    //转发公共库请求地址
    private static String checkAndReplaceRequestUrl(String requestUrl) {
        UrlProxyConfig config = DiskCache.getUrlProxyConfig(requestUrl);
        if (config != null && config.isEnable()) {
            String tmp = config.getDestServerUrl();
            if (tmp != null && tmp.trim().length() > 0) {
                System.out.println("wenjun test replace url: " + requestUrl + ", to: " + tmp);
                return tmp;
            }
        }
        return requestUrl;
    }

    public static JSON postForBody(HttpServletRequest request, String aseBody) {
        if ("GET".equals(request.getMethod())) {
            return get(request, aseBody);
        }

        JSON result = new JSON();
        String host_origin = request.getHeader("host_origin");

        String uri = request.getRequestURI();
        uri = uri.substring(5);

//        JSON desReqJson = JSON.fromJSONString(desReq);
//        JSON desReqResultData = JSON.fromJSONString(desReq.get("resultData").toString());

        //独立包访问地址强制转换
//        if(desReqResultData.containsKey("awe")){
//            if(host_origin.equals("api.aw.smarttel.cn") && (desReqResultData.get("awe").toString().equals("1"))){
//                host_origin = "10.21.141.54";
//            }
//        }

        System.out.println("[postForBody][Host origin]: " + host_origin);

        //转发公共库的请求方法调用
//        host_origin = replaceHost(host_origin);

        //添加配置中的url转换
        String url = "";
        String orgin_url = host_origin + uri;
        String tmp = checkAndReplaceRequestUrl(orgin_url);
        if (tmp.equals(orgin_url)) {//如果相等，走原来的逻辑
            //唤醒测试环境
            if(host_origin.equals("10.21.141.54")){
                url = "http://" + host_origin +":8699"+ uri;
            }else {
                url = "http://" + host_origin + uri;
            }
        } else {
            //否则走配置中替换的url
            url = "http://" + tmp;
        }

        System.out.println("[postForBody][HttpPost url]: " + url);

        HttpPost httpPost = new HttpPost(url);
        StringEntity se = request.getContentType() == null ? new StringEntity(aseBody, "UTF-8") : new StringEntity(aseBody, ContentType.create(request.getContentType(), "UTF-8"));
        httpPost.setEntity(se);
        Enumeration<String> hs = request.getHeaderNames();
        while (hs.hasMoreElements()) {
            String name = hs.nextElement();
//            httpPost.setHeader(name, request.getHeader(name));
            switch (name) {
                case "transfer-encoding":
                    break;
                case "host":
                    httpPost.setHeader("host", host_origin);
                    break;
                case "content-length":
                    break;
                default:
                    httpPost.setHeader(name, request.getHeader(name));
                    break;
            }
        }
        int status = -111;
        try {
            CloseableHttpResponse response = getConnection().execute(httpPost);
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
            httpPost.abort();
        }
        result.put("status", status);
        return result;
    }


    public static void postMsg(String json2base64data) {
        String url1 = "https://apipool.zhugeio.com/open/v1/event_statis_srv/upload_event";
        HttpPost httpPost = new HttpPost(url1);
        try {
            String key = "Authorization";
            String str = "Basic NzgyM2YxYTE4OGQ4NDk1MDg2YjZjZDE2N2ZmN2IzOWI6MjJlZmM3YTE2ZDVlNDE2N2I2MDNjYzc3ZDRkOWYyNDk=";
            httpPost.setHeader(key, str);
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("data", json2base64data));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            CloseableHttpResponse response = getConnection().execute(httpPost);
            int status = response.getStatusLine().getStatusCode();
            HttpEntity backEntity = response.getEntity();
            if (status == 200) {
                InputStream resStream = backEntity.getContent();
                BufferedReader br = new BufferedReader(new InputStreamReader(resStream));
                StringBuffer resBuffer = new StringBuffer();
                String resTemp = "";
                while ((resTemp = br.readLine()) != null) {
                    resBuffer.append(resTemp);
                }
                String result = resBuffer.toString();
                System.out.println(result);
            } else {
                //记日志2
                LOGGER.error("status=" + status);
            }

        } catch (Exception e) {
            //记日志3
            LOGGER.error("httpPost.abort();");
            httpPost.abort();
        }

    }

    public static JSON postTestMsg(String msg) {
        String url = "http://192.168.104.215:8080/dinfo";
        url = "http://devs.data.mob.com/dinfo";
        JSON result = new JSON();

        HttpPost httpPost = new HttpPost(url);

/*        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("m", "45i+a8/e4m5EzVYTByFkQsJjDSE1v3glIpzATo82gztvXFupJ9PYFj2mVVeGRBjY2Gz65bg9bK1tTROAn/Zy1HqdAfXiLtRHzfy               D8HHelo5c5fpWayrFrRmbAwr/MOtjgWKYgGcRtSZrxBEV+N3lvVF6OuosYFbNZOjulfrIErYg96Ge86KY6rIfWg1INpa0bixadSEm75mn               E/fisVLpyj6gqZETC3iRxJDZpJtkCh0G+rPf2Y/D/UuCkNlKk0hhbYN+TPHDz271UyRtwZG6gw==\n"));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/

        StringEntity params = new StringEntity(msg, ContentType.create("application/x-www-form-urlencoded", "UTF-8"));
        httpPost.setEntity(params);

        int status = -111;
        try {
            CloseableHttpResponse response = getConnection().execute(httpPost);
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
            httpPost.abort();
        }
        result.put("status", status);
        return result;

    }

}