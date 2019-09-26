package com.cn.ceshi.service.impl;

import com.cn.ceshi.dao.ConfigVoMapper;
import com.cn.ceshi.dao.Log2DBtMapper;
import com.cn.ceshi.model.ConfigVo;
import com.cn.ceshi.model.Log2DB;
import com.cn.ceshi.service.DealService;
import com.cn.ceshi.util.AllUtil2;
import com.cn.ceshi.util.HttpClientUtil;
import com.cn.ceshi.util.common.*;
import com.lamfire.json.JSON;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.*;

@Component("dealService2")
public class DealServiceImpl2 implements DealService {

    private static final Logger LOGGER = LogManager.getLogger(DealServiceImpl2.class.getName());

    private static final String _INTEGER = "Integer";
    private static final String _LONG = "Long";
    private static final String _FLOAT = "Float";
    private static final String _DOUBLE = "Double";
    private static final String _STRING = "String";
    private static final String _BOOLEAN = "Boolean";
    private static final String _JSON = "JSON";
    private static final String _JSONARRAY = "JSONArray";
    private List<String> errorMsgList = null;

    @Autowired
    private Log2DBtMapper log2DBtMapper;

    @Autowired
    private ConfigVoMapper cvMapper;



    @Override
    public String doing(HttpServletRequest request) {
        String host_origin = request.getHeader("host_origin");
        if (host_origin == null) {
            LOGGER.error("host_origin fidder 设置错误");
            return "";
        }

        String uri = request.getRequestURI();
        uri = uri.substring(5);

        String  hostAndURL = host_origin + uri;

        String aseBackData = null;

        try {
            Date requestTime = new Date();
            ConfigVo configVo = cvMapper.getByUrl(hostAndURL);
            String aseBody = null;

            if (configVo != null && configVo.isReqOpen()) {
                //TODO 加密明文
                aseBody = RequestEncode.encode(hostAndURL, configVo.getReqParams());
            } else {
                aseBody = IOUtils.toString(request.getInputStream(), "utf-8");
            }

            JSON forDes = new JSON();
            if ("GET".equals(request.getMethod())) {
                //不需要解密
                Enumeration<String> getMap = request.getParameterNames();

                while (getMap.hasMoreElements()) {
                    String name = getMap.nextElement();
                    String value = request.getParameter(name);
                    forDes.put(name, value);
                    aseBody += name + "=" + value + "&";
                }

            }
            String aseBodyOrigin = aseBody;

            if (("api.share.mob.com".equals(host_origin) && uri.indexOf("log4") > -1)
                    || ("l.gm.mob.com".equals(host_origin))
                    || ("c.data.mob.com".equals(host_origin))
                    || ("devs.data.mob.com".equals(host_origin))) {
                aseBody = URLDecoder.decode(aseBody, "utf-8");
            }
            //头过滤
            Map<String, String> heards = new HashMap<String, String>();
            Enumeration<String> hs = request.getHeaderNames();
            while (hs.hasMoreElements()) {
                String name = hs.nextElement();
                heards.put(name, request.getHeader(name));
            }
            if ("api.share.mob.com".equals(host_origin) && uri.indexOf("snsconf") > -1) {
                if (aseBody != null) {
                    String[] strArry = aseBody.split("&");
                    if (strArry != null && strArry.length == 2) {
                        String[] strArry2 = strArry[0].split("=");
                        String[] strArry3 = strArry[1].split("=");
                        heards.put("appkey", strArry2[1]);
                        heards.put("device", strArry3[1]);
                    }
                }
            }
            JSON desReq = null;

            //动态代码库入口mdc和业务mdc获取接口解密
            if(uri.contains("duc") || uri.contains("mdc")){
                desReq = RequestDecodeFnc.decodeDataFnc(host_origin, hostAndURL, heards, aseBody, forDes);

            }else if(uri.contains("dfl")){
                //动态代码库日志上传解密
                desReq = RequestDecodeDfl.decodeDataDfl(host_origin, hostAndURL, heards, aseBody, forDes);

            }else if(uri.contains("awt") || uri.contains("apl") || uri.contains("awl")){
                //唤醒相关下发任务和上传日志解密
                    desReq = RequestDecodeAw.decodeDataAw(host_origin, hostAndURL, heards, aseBody, forDes);

            }else if(uri.contains("drl")){
                //动态代码库加载日志解密
                    desReq = RequestDecodeDrl.decodeDataDrl(host_origin, hostAndURL, heards, aseBody, forDes);
            }else{
                //原有公共库解密
                    desReq = AllUtil2.inDecode(host_origin, hostAndURL, heards, aseBody, forDes);
                }



            //进去的参数处理
            //a.参数入库
            Date responseTime = null;
            String pbackData2Db = null;
            int code = 0;
            if (configVo != null && configVo.isRespOpen()) {
                responseTime = new Date();
                code = 1;
                String tmp = configVo.getRespParams();
                //动态代码库响应明文加密
                if(uri.contains("duc") || uri.contains("mdc")){
                    aseBackData = RequestEncodeFnc.encodeFnc(tmp);
                    //唤醒明文加密
                }else if(uri.contains("dfl")){
                    aseBackData = RequestEncodeDfl.encodeDfl(tmp);
                }else if(uri.contains("awt") || uri.contains("apl") || uri.contains("awl")){
                        aseBackData = RequestEncodeAw.encodeAw(tmp);
                }else if(uri.contains("drl")){
                        aseBackData =  RequestEncodeDrl.encodeDrl(tmp);
                }else{
                        aseBackData = RequestEncode.encode_v1_2_3_4_cconf_resp(hostAndURL, forDes, JSON.fromJSONString(tmp));
                }

                JSON result = new JSON();
                result.put("resultData", tmp);
                result.put("success", 1);
                result.put("passDes", 1);
                pbackData2Db = result.toJSONString();
            } else {
                 //httpClient
                JSON backDataJSON = HttpClientUtil.postForBody(request, aseBodyOrigin);//from httpclient

                responseTime = new Date();
                JSON tmp = new JSON();
                if (backDataJSON.getInteger("status") == 200) {
                    code = 1;
                    aseBackData = backDataJSON.getString("result");
                    if (!StringUtils.isEmpty(aseBackData)) {
                        //b.返回数据入库
                        JSON outData =null;
                        if(uri.contains("duc") || uri.contains("mdc")){
                            outData = RequestDecodeFnc.decodeFnc(aseBackData);
                        }else if(uri.contains("dfl")){
                            outData = RequestDecodeDfl.decodeDfl(aseBackData);
                        }else if(uri.contains("awt") || uri.contains("apl") || uri.contains("awl")){
                                outData = RequestDecodeAw.decodeAw(aseBackData);
                        }else if(uri.contains("drl")){
                                outData = RequestDecodeDrl.decodeDrl(aseBackData);
                        }else{
                                outData = AllUtil2.outDecode(host_origin, hostAndURL, heards, aseBackData, forDes);
                        }
                        pbackData2Db = outData != null ? outData.toJSONString() : null;

                    } else {
                        tmp.put("resultData", "请求成功，返回数据为null");
                        pbackData2Db = tmp.toJSONString();
                    }

                } else {
                    tmp.put("resultData", "http/https  fail");
                    pbackData2Db = tmp.toJSONString();
                }
            }
            //c.真正执行异步入库
            //doing..........something..........
            Log2DB log2DB = new Log2DB();
            log2DB.setCode(code);
            log2DB.setReqOriginParam(aseBody);
            log2DB.setRespOriginParam(aseBackData);
            log2DB.setRequestTime(requestTime);
            log2DB.setResponseTime(responseTime);
            log2DB.setHostAndURI(hostAndURL);
            log2DB.setSendParams(desReq.toJSONString());
            log2DB.setBackData(pbackData2Db);
            log2DBtMapper.save(log2DB);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return aseBackData;
    }
}