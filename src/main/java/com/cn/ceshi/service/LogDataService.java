package com.cn.ceshi.service;

import com.cn.ceshi.model.ConfigVo;
import com.cn.ceshi.model.Log2DB;
import sun.security.krb5.Config;

import java.util.List;

/**
 * Created by wangly on 2017/9/25.
 */
public interface LogDataService {

    List<Log2DB> list(Log2DB log2DB, int pageno, int pagesize);

    int clear(Log2DB log2DB);

    long count(Log2DB log2DB);

    List<ConfigVo> listConf(ConfigVo configVo, int pageno, int pagesize);

    long countConfigVo();

    int updateConfigVo(ConfigVo configVo);

    int updateRespparams(String url, String params);

}
