package com.cn.ceshi.controller;

import com.cn.ceshi.cache.DiskCache;
import com.cn.ceshi.model.ConfigVo;
import com.cn.ceshi.model.Log2DB;
import com.cn.ceshi.model.RsaInfoConfig;
import com.cn.ceshi.model.UrlProxyConfig;
import com.cn.ceshi.service.DealService;
import com.cn.ceshi.service.LogDataService;
import com.lamfire.json.JSON;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by wangly on 2017/8/30.
 */
@Controller
public class IndexController {

    private static final Logger LOGGER = LogManager.getLogger(IndexController.class.getName());


    @Resource(name = "dealService2")
    private DealService dealService;


    @Autowired
    private LogDataService logDataService;


    @RequestMapping(value = "/admin/page")
    public String page(HttpServletRequest request) {
        return "tables";
    }

    @RequestMapping(value = "/admin/page/config")
    public String page_config(HttpServletRequest request) {
        return "config";
    }

    @RequestMapping(value = "/admin/page/config_rsa")
    public String page_config_rsa(HttpServletRequest request) {
        return "config_rsa";
    }

    @RequestMapping(value = "/admin/page/config_proxy")
    public String page_config_proxy(HttpServletRequest request) {
        return "config_proxy";
    }

    @ResponseBody
    @RequestMapping(value = "/admin/data")
    public JSON admin(HttpServletRequest request) {
        String pageNoStr = request.getParameter("pageno");
        String hostAndURI = request.getParameter("hostAndURI");
        String createAt = request.getParameter("createAt");

        int pageno = 1;
        int pagesize = 10;
        if (!StringUtils.isEmpty(pageNoStr)) {
            try {
                int pNo = Integer.valueOf(pageNoStr);
                if (pNo > 0) {
                    pageno = pNo;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log2DB log2DB = new Log2DB();
        //
        if (!StringUtils.isEmpty(hostAndURI)) {
            log2DB.setHostAndURI(hostAndURI);
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        if (!StringUtils.isEmpty(createAt)) {
            try {
                log2DB.setCreateAt(simpleDateFormat.parse(createAt));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        List<Log2DB> list = logDataService.list(log2DB, pageno, pagesize);
        long count = logDataService.count(log2DB);
        JSON map = new JSON();
        map.put("data", list);
        map.put("pageno", pageno);
        map.put("pagesize", pagesize);
        map.put("total", count > 0 ? (count - 1) / pagesize + 1 : 0);
        request.setAttribute("result", map);
        return map;
    }

    @ResponseBody
    @RequestMapping(value = "/admin/config/query")
    public JSON config(HttpServletRequest request) {
        String pageNoStr = request.getParameter("pageno");
        String hostAndURI = request.getParameter("hostAndURI");

        int pageno = 1;
        int pagesize = 10;
        if (!StringUtils.isEmpty(pageNoStr)) {
            try {
                int pNo = Integer.valueOf(pageNoStr);
                if (pNo > 0) {
                    pageno = pNo;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ConfigVo configVo = new ConfigVo();
        //
        if (!StringUtils.isEmpty(hostAndURI)) {
            configVo.setUrl(hostAndURI);
        }

        List<ConfigVo> list = logDataService.listConf(configVo, pageno, pagesize);
        long count = logDataService.countConfigVo();
        JSON map = new JSON();
        map.put("data", list);
        map.put("pageno", pageno);
        map.put("pagesize", pagesize);
        map.put("total", count > 0 ? (count - 1) / pagesize + 1 : 0);
        request.setAttribute("result", map);
        return map;
    }

    @ResponseBody
    @RequestMapping(value = "/admin/config/update")
    public JSON configUpdate(HttpServletRequest request) {
        String url = request.getParameter("url");
        String reqOpenStr = request.getParameter("reqOpen");
        String respOpenStr = request.getParameter("respOpen");
        String respParams = request.getParameter("respParams");
        String reqParams = request.getParameter("reqParams");
        boolean reqOpen = false;
        boolean respOpen = false;
        boolean param_error = false;
        try {
            url = url.trim();
            respParams = respParams != null ? respParams.trim() : null;
            reqParams = reqParams != null ? reqParams.trim() : null;
            reqOpen = Boolean.valueOf(reqOpenStr);
            respOpen = Boolean.valueOf(respOpenStr);


        } catch (Exception e) {
            param_error = true;
        }
        JSON map = new JSON();
        if (param_error || StringUtils.isEmpty(url)) {
            map.put("success", -1);
            map.put("msg", "params is errror");
            return map;
        }

        ConfigVo configVo = new ConfigVo();
        configVo.setUrl(url);
        configVo.setReqOpen(reqOpen);
        configVo.setReqParams(reqParams);
        configVo.setRespOpen(respOpen);
        configVo.setRespParams(respParams);

        int back = logDataService.updateConfigVo(configVo);

        map.put("success", back);
        return map;
    }

    @ResponseBody
    @RequestMapping(value = "/admin/config/query_rsa")
    public JSON config_rsa(HttpServletRequest request) {
        String pageNoStr = request.getParameter("pageno");
        String hostAndURI = request.getParameter("hostAndURI");

        int pageno = 1;
        int pagesize = 10;
        if (!StringUtils.isEmpty(pageNoStr)) {
            try {
                int pNo = Integer.valueOf(pageNoStr);
                if (pNo > 0) {
                    pageno = pNo;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ConfigVo configVo = new ConfigVo();
        //
        if (!StringUtils.isEmpty(hostAndURI)) {
            configVo.setUrl(hostAndURI);
        }

        HashMap<String, RsaInfoConfig> cacheMap = DiskCache.getAllRsaInfo();
        List<RsaInfoConfig> list = new ArrayList<>();
        for (Map.Entry<String, RsaInfoConfig> item : cacheMap.entrySet()) {
            list.add(item.getValue());
        }
        Collections.sort(list);
        long count = list.size();

        List<RsaInfoConfig> newList = new ArrayList<>();
        if (pagesize >= count) {
            newList = list;
        } else {
            for (int i = 0; i < pagesize; i ++) {
                int index = (pageno -1) * pagesize + i;
                if (index < count) {
                    newList.add(list.get(index));
                }
            }
        }
        JSON map = new JSON();
        map.put("data", newList);
        map.put("pageno", pageno);
        map.put("pagesize", pagesize);
        map.put("total", count > 0 ? (count - 1) / pagesize + 1 : 0);
        request.setAttribute("result", map);
        return map;
    }

    @ResponseBody
    @RequestMapping(value = "/admin/config/update_rsa")
    public JSON configUpdateRsa(HttpServletRequest request) {
        String url = request.getParameter("url");
        String publicKey = request.getParameter("publicKey");
        String secretKey = request.getParameter("secretKey");
        String modules = request.getParameter("modules");

        url = url == null ? "" : url.trim();
        publicKey = publicKey == null ? "" : publicKey.trim();
        secretKey = secretKey == null ? "" : secretKey.trim();
        modules = modules == null ? "" : modules.trim();
        JSON map = new JSON();
        if ("".equals(url) || "".equals(publicKey) || "".equals(secretKey) || "".equals(modules)) {
            map.put("success", -1);
            map.put("msg", "params is errror");
            return map;
        }

        RsaInfoConfig info = new RsaInfoConfig();
        info.setUrl(url);
        info.setModules(modules);
        info.setPublicKey(publicKey);
        info.setSecretKey(secretKey);
        info.setCreateTime(System.currentTimeMillis());

        boolean result = DiskCache.saveRsaInfo(url, info);
        map.put("success", result ? 1 : 0);
        return map;
    }


    @ResponseBody
    @RequestMapping(value = "/admin/config/query_proxy")
    public JSON config_proxy(HttpServletRequest request) {
        String pageNoStr = request.getParameter("pageno");
        String hostAndURI = request.getParameter("hostAndURI");

        int pageno = 1;
        int pagesize = 10;
        if (!StringUtils.isEmpty(pageNoStr)) {
            try {
                int pNo = Integer.valueOf(pageNoStr);
                if (pNo > 0) {
                    pageno = pNo;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        HashMap<String, UrlProxyConfig> cacheMap = DiskCache.getAllUrlProxyConfig();
        List<UrlProxyConfig> list = new ArrayList<>();
        for (Map.Entry<String, UrlProxyConfig> entry : cacheMap.entrySet()) {
            list.add(entry.getValue());
        }

        Collections.sort(list);
        long count = list.size();

        List<UrlProxyConfig> newList = new ArrayList<>();
        if (pagesize >= count) {
            newList = list;
        } else {
            for (int i = 0; i < pagesize; i ++) {
                int index = (pageno -1) * pagesize + i;
                if (index < count) {
                    newList.add(list.get(index));
                }
            }
        }

        JSON map = new JSON();
        map.put("data", newList);
        map.put("pageno", pageno);
        map.put("pagesize", pagesize);
        map.put("total", count > 0 ? (count - 1) / pagesize + 1 : 0);
        request.setAttribute("result", map);
        return map;
    }

    @ResponseBody
    @RequestMapping(value = "/admin/config/update_proxy")
    public JSON configUpdateProxy(HttpServletRequest request) {
        String clientReqUrl = request.getParameter("clientReqUrl");
        String destServerUrl = request.getParameter("destServerUrl");
        String tmp = request.getParameter("enable");

        clientReqUrl = clientReqUrl == null ? "" : clientReqUrl.trim();
        destServerUrl = destServerUrl == null ? "" : destServerUrl.trim();
        boolean enable = tmp == null ? false : Boolean.valueOf(tmp.trim());

        JSON map = new JSON();
        if ("".equals(clientReqUrl) || "".equals(destServerUrl)) {
            map.put("success", -1);
            map.put("msg", "params is errror");
            return map;
        }

        UrlProxyConfig config = new UrlProxyConfig();
        config.setClientReqUrl(clientReqUrl);
        config.setDestServerUrl(destServerUrl);
        config.setEnable(enable);
        config.setCreateTime(System.currentTimeMillis());

        boolean result = DiskCache.saveUrlProxyConfig(clientReqUrl, config);
        map.put("success", result ? 1 : 0);
        return map;
    }

    @ResponseBody
    @RequestMapping(value = "/admin/update/respparams/cconf")
    public JSON updateSingleParams(HttpServletRequest request) {
        String url = request.getParameter("url");
        String respParams = request.getParameter("respParams");
        JSON map = new JSON();
        if (StringUtils.isEmpty(url) || StringUtils.isEmpty(respParams)) {
            map.put("success", -1);
            map.put("msg", "params is errror");
            return map;
        }
        int back = logDataService.updateRespparams(url, respParams);
        map.put("success", back);
        return map;

    }

    @ResponseBody
    @RequestMapping(value = "/test/**")
    public String index(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (StringUtils.isEmpty(uri) || "/".equals(uri) || uri.indexOf("favicon.ico") != -1) {
            return "\"error\":\"no_url\"";
        }
        String host_origin = request.getHeader("host_origin");
        uri = uri.substring(5);
        String url = host_origin + uri;
        LOGGER.info("[请求路径]" + uri);
        if (uri.indexOf(".html") > -1) {
            return "null";
        }
        return dealService.doing(request);
    }
}
