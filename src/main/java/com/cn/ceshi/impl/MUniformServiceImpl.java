package com.cn.ceshi.impl;

import com.cn.ceshi.util.data_collector.MDecoder;
import com.cn.ceshi.util.data_collector.Response;
import com.lamfire.code.MD5;
import com.lamfire.json.JSON;
import com.lamfire.json.JSONArray;
import org.apache.commons.lang.StringUtils;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * TODO ADD DESCRIPTION
 * Date: 2017/9/22
 * Time: 11:19
 *
 * @author linrb
 */
public class MUniformServiceImpl {


    private static final String _INTEGER = "Integer";
    private static final String _LONG = "Long";
    private static final String _FLOAT = "Float";
    private static final String _DOUBLE = "Double";
    private static final String _STRING = "String";
    private static final String _BOOLEAN = "Boolean";
    private static final String _JSON = "JSON";
    private static final String _JSONARRAY = "JSONArray";


    /**
     * 参数解密
     *
     * @param requestUrl 请求的url(无协议头，如http)
     * @param headers    请求头
     * @param httpBody   待解密请求体
     * @param needKey    可能需要的解密参数  （客户端的动态秘钥）
     * @return
     */
    public JSON inDecode(String requestUrl, Map<String, String> headers, String httpBody, JSON needKey) throws Exception {
        if (StringUtils.isBlank(requestUrl) || StringUtils.isBlank(httpBody)) {
            return null;
        }
        try {
//            String decodeResult = null;
            // 上传公共库收集数据，客户端加密
            if ((requestUrl.contains("lgm.accuratead.cn") || requestUrl.contains("c.data.mob.com")) && requestUrl.contains("cdata")) {
                return cdata(requestUrl, httpBody);
                //获取统一收集服务配置
            } else if ((requestUrl.contains("m.data.mob.com") || requestUrl.contains("m.data.accuratead.cn"))
                    && requestUrl.contains("cconf")) {
                return cconf(httpBody, needKey);
                // 设备中心数据，客户端加密
            } else if (requestUrl.contains("devs.data.mob.com") && requestUrl.contains("dinfo")) {
                return dinfo(httpBody);
                //ios统一DUID标示
            } else if (requestUrl.contains("devs.data.mob.com") && requestUrl.contains("dck")) {
                return dck(httpBody);
                //上传设备产品线关联信息
            } else if ((requestUrl.contains("devs.data.mob.com") || requestUrl.contains("devsdata.accuratead.cn"))
                    && requestUrl.contains("dsign")) {
                return dsign(httpBody);
            }else if((requestUrl.contains("l.gm.mob.com") || requestUrl.contains("lgm.accuratead.cn"))
                    && requestUrl.contains("gcl")){
                return gcl(requestUrl, httpBody);
            }else if((requestUrl.contains("f.gm.mob.com") || requestUrl.contains("fgm.accuratead.cn"))
                    && requestUrl.contains("gcf")){
                return gcf(httpBody, needKey);
            } else {
                return Response.errorJSON(httpBody, "requestUrl unrecognized");
            }
        } catch (Exception e) {
            throw e;
        }
    }

    private JSON dsign(String httpBody) {
        String[] splitFields = httpBody.split("&");
        String appkey = null;
        String duid = null;
        String product = null;
        for (String sf : splitFields) {
            if (sf.contains("appkey")) {
                appkey = sf.replaceAll("appkey=", "");
            } else if (sf.contains("duid")) {
                duid = sf.replaceAll("duid=", "");
            } else if (sf.contains("product")) {
                product = sf.replaceAll("product=", "");
            }
        }
        List<String> errMsg = new ArrayList<String>();
        if (StringUtils.isBlank(appkey)) {
            errMsg.add("appkey is not exist");
        } else if (appkey.length() > 30) {
            errMsg.add("appkey too long");
        }
        if (StringUtils.isBlank(duid)) {
            errMsg.add("duid is not exist");
        } else if (duid.length() > 30) {
            errMsg.add("duid too long");
        }
        if (StringUtils.isBlank(product)) {
            errMsg.add("product is not exist");
        }
        String errMsgStr = StringUtils.join(errMsg, ",");
        if (StringUtils.isBlank(errMsgStr)) {
            return Response.successJSON(httpBody);
        } else {
            return Response.errorJSON(httpBody, errMsgStr);
        }
    }

    private JSON dck(String httpBody) {
        String[] splitFields = httpBody.split("&");
        String appkey = null;
        String duid = null;
        for (String sf : splitFields) {
            if (sf.contains("appkey")) {
                appkey = sf.replaceAll("appkey=", "");
            } else if (sf.contains("duid")) {
                duid = sf.replaceAll("duid=", "");
            }
        }
        List<String> errMsg = new ArrayList<String>();
        if (StringUtils.isBlank(appkey)) {
            errMsg.add("appkey is not exist");
        } else if (appkey.length() > 30) {
            errMsg.add("appkey too long");
        }
        if (StringUtils.isBlank(duid)) {
            errMsg.add("duid is not exist");
        } else if (duid.length() > 30) {
            errMsg.add("duid too long");
        }
        String errMsgStr = StringUtils.join(errMsg, ",");
        if (StringUtils.isBlank(errMsgStr)) {
            return Response.successJSON(httpBody);
        } else {
            return Response.errorJSON(httpBody, errMsgStr);
        }
    }

    private JSON dinfo(String httpBody) {
        String decodeResult;
        String m = httpBody.replaceAll("m=", "");
        if (!StringUtils.isBlank(m)) {
            decodeResult = MDecoder.decodeDevice(m);
            JSON json = null;
            try {
                json = JSON.fromJSONString(decodeResult);
            } catch (Exception e) {
                return Response.errorJSON(decodeResult, "decode result to JSON failed");
            }

            String errorMsg = this.checkDeviceData(json);
            if (StringUtils.isBlank(errorMsg)) {
                return Response.successJSON(decodeResult);
            } else {
                return Response.errorJSON(decodeResult, errorMsg);
            }
        } else {
            return Response.errorJSON(httpBody, "m is not exist");
        }
    }

    private JSON cdata(String requestUrl, String httpBody) throws Exception {
        String decodeResult;
        String[] splitFields = httpBody.split("&");
        String m = null;
        String appkey = null;
        for (String sf : splitFields) {
            if (sf.contains("appkey")) {
                appkey = sf.replaceAll("appkey=", "");
                continue;
            }
            m = sf.replaceAll("m=", "");
        }
        if (!StringUtils.isBlank(m)) {
            m = URLDecoder.decode(m,"UTF-8").replaceAll(" ","+");
            if (requestUrl.contains("v")) {
                decodeResult = MDecoder.collectorDecodeV2Data(m);
            } else {
                decodeResult = MDecoder.collectorDecodeV1Data(m);
            }
            JSON json = new JSON();
            json.put("m", decodeResult);
            if (StringUtils.isBlank(appkey)) {
                return Response.errorJSON(json, "appkey not exist");
            } else {
                json.put("appkey", appkey);
            }

            JSON data = null;
            try {
                data = JSON.fromJSONString(decodeResult);
            } catch (Exception e) {
                return Response.errorJSON(json, "decode result to JSON failed");
            }

            String errorMsg = null;
            if (!requestUrl.contains("v")) {
                errorMsg = this.checkDataCollectorData(data, 1);
            }else if(requestUrl.contains("v2")){
                errorMsg = this.checkDataCollectorData(data, 2);
            }else if(requestUrl.contains("v3")){
                errorMsg = this.checkDataCollectorData(data, 3);
            }

            if (StringUtils.isBlank(errorMsg)) {
                return Response.successJSON(json);
            } else {
                return Response.errorJSON(json, errorMsg);
            }
        } else {
            return Response.errorJSON(httpBody, "m is not exist");
        }
    }

    private JSON cconf(String httpBody, JSON needKey) {
        String[] splitFields = httpBody.split("&");
        String plat = null;
        String appkey = null;
        String apppkg = null;
        for (String sf : splitFields) {
            if (sf.contains("plat")) {
                plat = sf.replaceAll("plat=", "");
            }else if (sf.contains("appkey")){
                appkey = sf.replaceAll("appkey=", "");
            }else if(sf.contains("apppkg")){
                apppkg = sf.replaceAll("apppkg=", "");
            }
        }
        //返回体解密所需参数
        needKey.put("appkey",appkey);
        needKey.put("apppkg",apppkg);

        List<String> errMsg = new ArrayList<String>();
        if (StringUtils.isBlank(plat)) {
            errMsg.add("plat is not exist");
        }
        String errMsgStr = StringUtils.join(errMsg, ",");
        if (StringUtils.isBlank(errMsgStr)) {
            return Response.successJSON(httpBody);
        } else {
            return Response.errorJSON(httpBody, errMsgStr);
        }
    }

    private JSON gcf(String httpBody, JSON needKey) {
        String[] splitFields = httpBody.split("&");
        String plat = null;
        String appkey = null;
        String apppkg = null;
        for (String sf : splitFields) {
            if (sf.contains("plat")) {
                plat = sf.replaceAll("plat=", "");
            }else if (sf.contains("appkey")){
                appkey = sf.replaceAll("appkey=", "");
            }else if(sf.contains("apppkg")){
                apppkg = sf.replaceAll("apppkg=", "");
            }
        }
        //返回体解密所需参数
        needKey.put("appkey",appkey);
        needKey.put("apppkg",apppkg);

        List<String> errMsg = new ArrayList<String>();
        if (StringUtils.isBlank(plat)) {
            errMsg.add("plat is not exist");
        }
        String errMsgStr = StringUtils.join(errMsg, ",");
        if (StringUtils.isBlank(errMsgStr)) {
            return Response.successJSON(httpBody);
        } else {
            return Response.errorJSON(httpBody, errMsgStr);
        }
    }

    private JSON gcl(String requestUrl, String httpBody) throws Exception {
        String decodeResult;
        String[] splitFields = httpBody.split("&");
        String m = null;
        String appkey = null;
        for (String sf : splitFields) {
            if (sf.contains("appkey")) {
                appkey = sf.replaceAll("appkey=", "");
                continue;
            }
            m = sf.replaceAll("m=", "");
        }
        if (!StringUtils.isBlank(m)) {
            m = URLDecoder.decode(m,"UTF-8").replaceAll(" ","+");
            if (requestUrl.contains("v")) {
                decodeResult = MDecoder.collectorDecodeV2Data(m);
            } else {
                decodeResult = MDecoder.collectorDecodeV1Data(m);
            }
            JSON json = new JSON();
            json.put("m", decodeResult);
            if (StringUtils.isBlank(appkey)) {
                return Response.errorJSON(json, "appkey not exist");
            } else {
                json.put("appkey", appkey);
            }

            JSON data = null;
            try {
                data = JSON.fromJSONString(decodeResult);
            } catch (Exception e) {
                return Response.errorJSON(json, "decode result to JSON failed");
            }

            String errorMsg = null;
            if(requestUrl.contains("v5")){
                errorMsg = this.checkDataCollectorData(data, 3);
            }

            if (StringUtils.isBlank(errorMsg)) {
                return Response.successJSON(json);
            } else {
                return Response.errorJSON(json, errorMsg);
            }
        } else {
            return Response.errorJSON(httpBody, "m is not exist");
        }
    }

    /**
     * 返回值 解密
     *
     * @param requestUrl 请求的url(无协议头，如http)
     *                   eg1:
     *                   p.ddking.mob.com
     *                   eg2:
     *                   api.share.mob.com/log
     *                   api.share.mob.com/data
     * @param headers    请求头
     * @param httpBody   待解密请求体
     * @param needKey    可能需要的解密参数      （客户端的动态秘钥）
     * @return
     */
    public JSON outDecode(String requestUrl, Map<String, String> headers, String httpBody, JSON needKey) throws Exception {
        if (StringUtils.isBlank(requestUrl) || StringUtils.isBlank(httpBody)) {
            return null;
        }
        try {
            JSON json = null;
            String errorMsgStr = null;
            List<String> errMsg = new ArrayList<String>();
            try {
                json = JSON.fromJSONString(httpBody);
            } catch (Exception e) {
                return Response.errorJSON(httpBody, "httpBody to JSON failed");
            }
            // 获取统一收集配置，服务端返回加密包体
            if ((requestUrl.contains("m.data.accuratead.cn") || requestUrl.contains("m.data.mob.com")) && requestUrl.contains("cconf")) {
                Integer plat = needKey.getInteger("plat");
                if (!requestUrl.contains("v")) {
                    return Response.successJSON(httpBody);
                } else if (requestUrl.contains("v2")) {
                    if(json.containsKey("sr")){
                        json.put("sr", MDecoder.cconfDecode(json.getString("sr")).trim());
                    }
                    if (json.containsKey("sc")) {
                        json.put("sc", MDecoder.cconfDecode(json.getString("sc")).trim());
                    }
                    errorMsgStr = this.checkCConfRetData(json, 2, plat);
                } else if (requestUrl.contains("v3")) {
                    if(json.containsKey("sr")){
                        json.put("sr", MDecoder.cconfDecode(json.getString("sr")).trim());
                    }
                    if (json.containsKey("sc")) {
                        json.put("sc", MDecoder.cconfDecode(json.getString("sc")).trim());
                    }
                    errorMsgStr = this.checkCConfRetData(json, 3, plat);
                } else if (requestUrl.contains("v4")) {
                    String appkey = needKey.getString("appkey");
                    String apppkg = needKey.getString("apppkg");
                    String timestamp = json.getString("timestamp");
                    byte[] aesKey = MD5.digest(String.format("%s:%s:%s", appkey, apppkg, timestamp).getBytes());
                    if(json.containsKey("sr")){
                        json.put("sr", MDecoder.cconfDecode(json.getString("sr")).trim());
                    }
                    if (json.containsKey("sc")) {
                        json.put("sc", MDecoder.cconfDecode(json.getString("sc"), aesKey).trim());
                    }
                    errorMsgStr = this.checkCConfRetData(json, 4, plat);
                }
                if (StringUtils.isBlank(errorMsgStr)) {
                    return Response.successJSON(json);
                } else {
                    return Response.errorJSON(json, errorMsgStr);
                }
            } else if ((requestUrl.contains("devsdata.accuratead.cn") || requestUrl.contains("devs.data.mob.com")) && requestUrl.contains("dsign")) {

                if (!json.containsKey("reup")) {
                    errMsg.add("reup is not exist");
                }
                if (!json.containsKey("status")) {
                    errMsg.add("status is not exist");
                }
                String errMsgStr = StringUtils.join(errMsg, ",");
                if (StringUtils.isBlank(errMsgStr)) {
                    return Response.successJSON(httpBody);
                } else {
                    return Response.errorJSON(httpBody, errMsgStr);
                }
            } else if ((requestUrl.contains("devsdata.accuratead.cn") || requestUrl.contains("devs.data.mob.com")) && requestUrl.contains("dinfo")) {
                if (!json.containsKey("duid")) {
                    errMsg.add("duid is not exist");
                }
                if (!json.containsKey("status")) {
                    errMsg.add("status is not exist");
                }
                String errMsgStr = StringUtils.join(errMsg, ",");
                if (StringUtils.isBlank(errMsgStr)) {
                    return Response.successJSON(httpBody);
                } else {
                    return Response.errorJSON(httpBody, errMsgStr);
                }
            }
            if ((requestUrl.contains("fgm.accuratead.cn") || requestUrl.contains("f.gm.mob.com")) && requestUrl.contains("gcf")) {
                Integer plat = needKey.getInteger("plat");
                if (requestUrl.contains("v5")) {
                    String appkey = needKey.getString("appkey");
                    String apppkg = needKey.getString("apppkg");
                    String timestamp = json.getString("timestamp");
                    byte[] aesKey = MD5.digest(String.format("%s:%s:%s", appkey, apppkg, timestamp).getBytes());
                    if(json.containsKey("sr")){
                        json.put("sr", MDecoder.cconfDecode(json.getString("sr")).trim());
                    }
                    if (json.containsKey("sc")) {
                        json.put("sc", MDecoder.cconfDecode(json.getString("sc"), aesKey).trim());
                    }
                    errorMsgStr = this.checkCConfRetData(json, 4, plat);
                }
                if (StringUtils.isBlank(errorMsgStr)) {
                    return Response.successJSON(json);
                } else {
                    return Response.errorJSON(json, errorMsgStr);
                }
            } else {
                if (!json.containsKey("status")) {
                    errMsg.add("status is not exist");
                }
                String errMsgStr = StringUtils.join(errMsg, ",");
                if (StringUtils.isBlank(errMsgStr)) {
                    return Response.successJSON(httpBody);
                } else {
                    return Response.errorJSON(httpBody, errMsgStr);
                }
            }
        } catch (Exception e) {
            throw e;
        }
    }

    private List<String> errorMsgList = null;

    /**
     * 对公共库收集的数据校验
     *
     * @param json 解密后的数据
     * @return
     */
    private String checkDataCollectorData(JSON json, Integer ver) {
        errorMsgList = new ArrayList<String>();
        this.checkIfExistAndtype(json, "plat", _INTEGER);
        this.checkIfExistAndtype(json, "duid", _STRING);
        this.checkIfExistAndtype(json, "datas", _JSONARRAY);
        if (ver == 1 || ver == 2) {
            this.checkIfExistAndtype(json, "networktype", _STRING);
            this.checkIfExistAndtype(json, "device", _STRING);
        }

        //校验datas数组
        JSONArray datas = json.getJSONArray("datas");
        if (datas != null && !datas.isEmpty()) {
            JSON data = null;
            for (Iterator<Object> it = datas.iterator(); it.hasNext(); ) {
                data = (JSON) it.next();
                if (data == null) {
                    continue;
                }

                this.checkIfExistAndtype(data, "type", _STRING);
                this.checkIfExistAndtype(data, "datetime", _LONG);
                this.ifExistThenCheckType(data, "recordat", _LONG);
                this.ifExistThenCheckType(data, "sdk_runtime_len", _INTEGER);
                this.ifExistThenCheckType(data, "top_count", _INTEGER);

                String type = (String) data.get("type");
                if (StringUtils.isBlank(type)) {
                    continue;
                }

                //上传进程数据校验
                if (type.equalsIgnoreCase("PROCESS_INCR") || type.equalsIgnoreCase("PROCESS_ALL")) {
                    //文档中没有
                }

                //上传安卓安装的APP列表数据校验、上传安卓卸载的APP列表数据校验
                if (type.equalsIgnoreCase("APPS_INCR") || type.equalsIgnoreCase("APPS_ALL") ||
                        type.equalsIgnoreCase("UNINSTALL"))
                {
                    this.checkIfExistAndtype(data, "list", _JSONARRAY);
                    JSON internalData = null;
                    for (Iterator<Object> i = data.getJSONArray("list").iterator(); i.hasNext(); ) {
                        internalData = (JSON) i.next();
                        if (internalData == null) {
                            continue;
                        }
                        this.checkIfExistAndtype(internalData, "pkg", _STRING);
                        this.checkIfExistAndtype(internalData, "name", _STRING);
                        this.checkIfExistAndtype(internalData, "version", _STRING);
                    }
                }

                //安卓应用列表运行时长校验
                if (type.equalsIgnoreCase("APP_RUNTIMES")) {
                    this.checkIfExistAndtype(data, "list", _JSONARRAY);
                    JSON internalData = null;
                    for (Iterator<Object> i = data.getJSONArray("list").iterator(); i.hasNext(); ) {
                        internalData = (JSON) i.next();
                        if (internalData == null) {
                            continue;
                        }
                        this.checkIfExistAndtype(internalData, "pkg", _STRING);
                        this.checkIfExistAndtype(internalData, "name", _STRING);
                        this.checkIfExistAndtype(internalData, "version", _STRING);
                        this.checkIfExistAndtype(internalData, "runtimes", _INTEGER);
                        this.ifExistThenCheckType(internalData, "bg", _INTEGER);
                        this.ifExistThenCheckType(internalData, "fg", _INTEGER);
                        this.ifExistThenCheckType(internalData, "empty", _INTEGER);
                    }
                }

                //上传设备补充信息校验
                if (type.equalsIgnoreCase("DEVEXT")) {
                    this.checkIfExistAndtype(data, "data", _JSON);
                    JSON internalData = data.getJSONObject("data");
                    this.checkIfExistAndtype(internalData, "phonename", _STRING);
                    this.ifExistThenCheckType(internalData, "developerid", _STRING);
                    this.ifExistThenCheckType(internalData, "signmd5", _STRING);
                }

                //安卓上传基站信息校验
                if (type.equalsIgnoreCase("BSINFO")) {
                    this.checkIfExistAndtype(data, "data", _JSON);
                    JSON internalData = data.getJSONObject("data");
                    this.checkIfExistAndtype(internalData, "carrier", _INTEGER);
                    this.checkIfExistAndtype(internalData, "simopname", _STRING);
                    this.checkIfExistAndtype(internalData, "lac", _INTEGER);
                    this.checkIfExistAndtype(internalData, "cell", _INTEGER);
                }

                //安卓上传iOS 的mac地址信息校验
                if (type.equalsIgnoreCase("IOS_MAC_INFO")) {
                    //文档中没有
                }

                //上传地理位置信息校验
                if (type.equalsIgnoreCase("LOCATION") || type.equalsIgnoreCase("O_LOCATION")) {
                    this.checkIfExistAndtype(data, "data", _JSON);
                    JSON internalData = data.getJSONObject("data");
                    this.checkIfExistAndtype(internalData, "accuracy", _FLOAT);
                    this.checkIfExistAndtype(internalData, "latitude", _FLOAT);
                    this.checkIfExistAndtype(internalData, "longitude", _FLOAT);
                    this.ifExistThenCheckType(internalData, "location_type", _INTEGER);
                    this.ifExistThenCheckType(internalData, "cur_bssid", _STRING);
                    this.ifExistThenCheckType(internalData, "cur_ssid", _STRING);
                }

                //上传WIFI信息校验
                if (type.equalsIgnoreCase("WIFI_INFO")) {
                    this.checkIfExistAndtype(data, "data", _JSON);
                    JSON internalData = data.getJSONObject("data");
                    this.checkIfExistAndtype(internalData, "ssid", _STRING);
                    this.checkIfExistAndtype(internalData, "bssid", _STRING);
                }

                //上传WIFI扫描列表信息校验
                if (type.equalsIgnoreCase("WIFI_SCAN_LIST")) {
                    this.checkIfExistAndtype(data, "list", _JSONARRAY);
                    JSON internalData = null;
                    for (Iterator<Object> i = data.getJSONArray("list").iterator(); i.hasNext(); ) {
                        internalData = (JSON) i.next();
                        if (internalData == null) {
                            continue;
                        }
                        this.checkIfExistAndtype(internalData, "SSID", _STRING);
                        this.checkIfExistAndtype(internalData, "wifiSsid", _STRING);
                        this.checkIfExistAndtype(internalData, "BSSID", _STRING);
                    }
                }

                //iOS包含地理位置的图片信息校验
                if (type.equalsIgnoreCase("IMG_LOCATION")) {
                    this.checkIfExistAndtype(data, "list", _JSONARRAY);
                    JSON internalData = null;
                    for (Iterator<Object> i = data.getJSONArray("list").iterator(); i.hasNext(); ) {
                        internalData = (JSON) i.next();
                        if (internalData == null) {
                            continue;
                        }
                        this.checkIfExistAndtype(internalData, "mt", _INTEGER);
                        this.checkIfExistAndtype(internalData, "ms", _INTEGER);
                        this.checkIfExistAndtype(internalData, "pw", _INTEGER);
                        this.checkIfExistAndtype(internalData, "ph", _INTEGER);
                        this.checkIfExistAndtype(internalData, "cd", _LONG);
                        this.checkIfExistAndtype(internalData, "md", _LONG);
                        this.checkIfExistAndtype(internalData, "d", _DOUBLE);
                        this.checkIfExistAndtype(internalData, "h", _BOOLEAN);
                        this.checkIfExistAndtype(internalData, "f", _BOOLEAN);
                        this.checkIfExistAndtype(internalData, "bi", _STRING);
                        this.checkIfExistAndtype(internalData, "rb", _BOOLEAN);
                        this.checkIfExistAndtype(internalData, "bst", _INTEGER);
                        this.checkIfExistAndtype(internalData, "st", _INTEGER);
                        this.checkIfExistAndtype(internalData, "li", _STRING);
                        this.checkIfExistAndtype(internalData,"l_clat",_DOUBLE);
                        this.checkIfExistAndtype(internalData,"l_clon",_DOUBLE);
                        this.checkIfExistAndtype(internalData,"l_a",_DOUBLE);
                        this.checkIfExistAndtype(internalData,"l_ha",_DOUBLE);
                        this.checkIfExistAndtype(internalData,"l_va",_DOUBLE);
                        this.checkIfExistAndtype(internalData,"l_c",_DOUBLE);
                        this.checkIfExistAndtype(internalData,"l_s",_DOUBLE);
                        this.checkIfExistAndtype(internalData,"l_t",_DOUBLE);
                        this.checkIfExistAndtype(internalData,"l_fl",_INTEGER);
                    }
                }

                //应用前置(启动)信息校验
                if (type.equalsIgnoreCase("PV")) {
                    //文档中无
                }

                //上传小米设备的应用运行时长校验
                if (type.equalsIgnoreCase("XM_APP_RUNTIMES")) {
                    this.checkIfExistAndtype(data, "list", _JSONARRAY);
                    JSON internalData = null;
                    for (Iterator<Object> i = data.getJSONArray("list").iterator(); i.hasNext(); ) {
                        internalData = (JSON) i.next();
                        if (internalData == null) {
                            continue;
                        }
                        this.checkIfExistAndtype(internalData, "packageName", _STRING);
                        this.checkIfExistAndtype(internalData, "firstTimeStamp", _LONG);
                        this.checkIfExistAndtype(internalData, "lastTimeStamp", _LONG);
                        this.checkIfExistAndtype(internalData, "lastTimeUsed", _LONG);
                        this.checkIfExistAndtype(internalData, "totalTimeInForeground", _LONG);
                        this.checkIfExistAndtype(internalData, "launchCount", _INTEGER);
                        this.checkIfExistAndtype(internalData, "lastEvent", _INTEGER);
                    }
                }


            }
        }
        return StringUtils.join(errorMsgList, ',');
    }

    /**
     * 设备中心数据校验
     *
     * @param json
     * @return
     */
    private String checkDeviceData(JSON json) {
        errorMsgList = new ArrayList<String>();
        this.checkIfExistAndtype(json, "simserialno", _STRING);
        this.checkIfExistAndtype(json, "phoneno", _STRING);
        this.checkIfExistAndtype(json, "udid", _STRING);
        this.checkIfExistAndtype(json, "adsid", _STRING);
        this.checkIfExistAndtype(json, "imei", _STRING);
        this.checkIfExistAndtype(json, "serialno", _STRING);
        this.checkIfExistAndtype(json, "androidid", _STRING);
        this.checkIfExistAndtype(json, "mac", _STRING);
        this.checkIfExistAndtype(json, "model", _STRING);
        this.checkIfExistAndtype(json, "factory", _STRING);
        this.checkIfExistAndtype(json, "carrier", _INTEGER);
        this.checkIfExistAndtype(json, "screensize", _STRING);
        this.checkIfExistAndtype(json, "sysver", _STRING);
        this.checkIfExistAndtype(json, "plat", _INTEGER);
        this.checkIfExistAndtype(json, "breaked", _BOOLEAN);

        return StringUtils.join(errorMsgList, ',');
    }

    /**
     * 获取配置的返回结果数据校验
     *
     * @param json
     * @return
     */
    private String checkCConfRetData(JSON json, Integer ver, Integer plat) {
        errorMsgList = new ArrayList<String>();
        this.checkIfExistAndtype(json, "status", _INTEGER);
        this.checkIfExistAndtype(json, "timestamp", _LONG);

        JSON internalData = JSON.fromJSONString(json.getString("sc"));
        this.checkIfExistAndtype(internalData, "in", _INTEGER);
        this.checkIfExistAndtype(internalData, "all", _INTEGER);
        this.checkIfExistAndtype(internalData, "aspa", _INTEGER);
        this.checkIfExistAndtype(internalData, "ext", _INTEGER);
        this.checkIfExistAndtype(internalData, "di", _INTEGER);
        this.checkIfExistAndtype(internalData, "l", _INTEGER);
        this.checkIfExistAndtype(internalData, "lgap", _INTEGER);
        this.checkIfExistAndtype(internalData, "wi", _INTEGER);

        if (ver >= 3) {
            this.checkIfExistAndtype(internalData, "p", _INTEGER);
            this.checkIfExistAndtype(internalData, "ol", _INTEGER);
        }
        if (ver >= 4) {
            this.checkIfExistAndtype(internalData, "bi", _INTEGER);
        }

        if (plat != null) {
            if (plat == 1) {
                this.checkIfExistAndtype(internalData, "adle", _INTEGER);
                this.checkIfExistAndtype(internalData, "un", _INTEGER);
                this.checkIfExistAndtype(internalData, "rt", _INTEGER);
                this.checkIfExistAndtype(internalData, "rtsr", _INTEGER);
                this.checkIfExistAndtype(internalData, "bs", _INTEGER);
                this.checkIfExistAndtype(internalData, "bsgap", _INTEGER);
                if (ver >= 3) {
                    this.checkIfExistAndtype(internalData, "rtgap", _INTEGER);
                    this.checkIfExistAndtype(internalData, "wl", _INTEGER);
                    this.checkIfExistAndtype(internalData, "wlsr", _INTEGER);
                    this.checkIfExistAndtype(internalData, "xmar", _INTEGER);
                }
            } else if (plat == 2) {
                this.checkIfExistAndtype(internalData, "l", _INTEGER);
                if (ver >= 3) {
                    this.ifExistThenCheckType(internalData, "asim", _STRING);
                    this.checkIfExistAndtype(internalData, "ud", _INTEGER);
                    this.checkIfExistAndtype(internalData, "il", _INTEGER);
                    this.checkIfExistAndtype(internalData, "ill", _INTEGER);
                }
            }
        }

        return StringUtils.join(errorMsgList, ',');
    }


    private void checkIfExistAndtype(JSON json, String key, String type) {
        if (json.containsKey(key)) {
            try {
                if (type.equals(_INTEGER)) {
                    json.getInteger(key);
                } else if (type.equals(_STRING)) {
                    json.getString(key);
                } else if (type.equals(_LONG)) {
                    json.getLong(key);
                } else if (type.equals(_FLOAT)) {
                    json.getFloat(key);
                } else if (type.equals(_JSON)) {
                    json.getJSONObject(key);
                } else if (type.equals(_JSONARRAY)) {
                    json.getJSONArray(key);
                } else if (type.equals(_DOUBLE)) {
                    json.getDouble(key);
                } else if (type.equals(_BOOLEAN)) {
                    json.getBoolean(key);
                }
            } catch (Exception e) {
                errorMsgList.add(key + " should be " + type);
            }
        } else {
            errorMsgList.add(key + " is missing");
        }
    }

    private void ifExistThenCheckType(JSON json, String key, String type) {
        if (json.containsKey(key)) {
            try {
                if (type.equals(_INTEGER)) {
                    json.getInteger(key);
                } else if (type.equals(_STRING)) {
                    json.getString(key);
                } else if (type.equals(_LONG)) {
                    json.getLong(key);
                } else if (type.equals(_FLOAT)) {
                    json.getFloat(key);
                } else if (type.equals(_JSON)) {
                    json.getJSONObject(key);
                } else if (type.equals(_JSONARRAY)) {
                    json.getJSONArray(key);
                } else if (type.equals(_BOOLEAN)) {
                    json.getBoolean(key);
                } else if (type.equals(_DOUBLE)) {
                    json.getDouble(key);
                }
            } catch (Exception e) {
                errorMsgList.add(key + " should be " + type);
            }
        }
    }

    public static void main(String[] args) throws Exception {
//        String m = "AAAAhAAAAIAR0mz3EbrRNRihRJJR8VRGAGj3dTGSy/X6ZwzhaA3EqTTuk1XbRosD25ubThpyy3f/ZqFfImxIEwvoXGGSTxglodb99M9wCD6h0f/Yg6INOA9KG8HpiFClcvxGYRRhCK6EpM5HZ9tO2XyMGpgQlFBKIThxRtbSQc2B2z/z5bqVJgAAAVAfrjdI/Q gHVFzYzKUHg6PA9e7S5yV4ld buVt8X0q39Q3mNvqAwlDjjGPzeT8CQ9B3D0MugV40lvGvTXQgf98OoQjnjURfLnppKnpND03YiRiLojPfdomPNErvoefUExFNulKiPU2E5DlMqb3STrBYoLpDC/ qUu1GRG0b0PfW9YdEKuSgfccBPrhaHv9gA5iODgTHXX5ex OWiOUX7TD2ZTGhH8YK lUWBwmpwBpkICgFgjpLKd1MV9qILSzxix56xe9VQ KAC26eHOb9LR1mtRplgmbvAikP8GIaFdu7242p3fgBNWsIQEoH3I9lBFCTYHSAeDCVCu9dcrobBKkZsvTGZlw5R kyLBERXMT6S4 U/nhq9o4IixGAOaMqKDWQ80mkNHUa95 mo/DNyKY 0cXhawwB1IbO/4yUMWidxYhrzkFRv8 TnBWXrF49zE=";
//        m = m.replaceAll(" ","+");
//        String s = MDecoder.collectorDecodeV2Data(m);
//        System.out.println(s);

        MUniformServiceImpl service = new MUniformServiceImpl();
        String m = "appkey=fe0614078730&m=AAAAhAAAAIAR0mz3EbrRNRihRJJR8VRGAGj3dTGSy/X6ZwzhaA3EqTTuk1XbRosD25ubThpyy3f/ZqFfImxIEwvoXGGSTxglodb99M9wCD6h0f/Yg6INOA9KG8HpiFClcvxGYRRhCK6EpM5HZ9tO2XyMGpgQlFBKIThxRtbSQc2B2z/z5bqVJgAAAVAfrjdI/Q+gHVFzYzKUHg6PA9e7S5yV4ld+buVt8X0q39Q3mNvqAwlDjjGPzeT8CQ9B3D0MugV40lvGvTXQgf98OoQjnjURfLnppKnpND03YiRiLojPfdomPNErvoefUExFNulKiPU2E5DlMqb3STrBYoLpDC/+qUu1GRG0b0PfW9YdEKuSgfccBPrhaHv9gA5iODgTHXX5ex+OWiOUX7TD2ZTGhH8YK+lUWBwmpwBpkICgFgjpLKd1MV9qILSzxix56xe9VQ+KAC26eHOb9LR1mtRplgmbvAikP8GIaFdu7242p3fgBNWsIQEoH3I9lBFCTYHSAeDCVCu9dcrobBKkZsvTGZlw5R+kyLBERXMT6S4+U/nhq9o4IixGAOaMqKDWQ80mkNHUa95+mo/DNyKY+0cXhawwB1IbO/4yUMWidxYhrzkFRv8+TnBWXrF49zE=";
        String url = "l.gm.mob.com/v5/gcl";
        JSON gcl = service.gcl(url, m);
        System.out.println(gcl.toJSONString());
    }

}
