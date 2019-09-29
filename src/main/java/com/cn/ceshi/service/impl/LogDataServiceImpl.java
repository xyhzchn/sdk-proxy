package com.cn.ceshi.service.impl;

import com.cn.ceshi.dao.ConfigVoMapper;
import com.cn.ceshi.dao.Log2DBtMapper;
import com.cn.ceshi.model.ConfigVo;
import com.cn.ceshi.model.Log2DB;
import com.cn.ceshi.service.LogDataService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by wangly on 2017/9/25.
 */
@Service
public class LogDataServiceImpl implements LogDataService {
    @Autowired
    private Log2DBtMapper log2DBtMapper;

    @Autowired
    private ConfigVoMapper configVoMapper;

    @Override
    public List<Log2DB> list(Log2DB log2DB, int pageno, int pagesize) {
        PageHelper.startPage(pageno, pagesize);
        return log2DBtMapper.list(log2DB);
    }

    @Override
    public int clear(Log2DB log2DB) {
        return log2DBtMapper.clear(log2DB);
    }

    @Override
    public long count(Log2DB log2DB) {
        return log2DBtMapper.count(log2DB);
    }

    @Override
    public List<ConfigVo> listConf(ConfigVo configVo, int pageno, int pagesize) {
        PageHelper.startPage(pageno, pagesize);
        return configVoMapper.list(configVo);
    }

    @Override
    public long countConfigVo() {
        return configVoMapper.count();
    }

    @Override
    public int updateConfigVo(ConfigVo configVo) {


        int back = -1;
        try {
            ConfigVo tmp = configVoMapper.getByUrl(configVo.getUrl());
            if (tmp == null) {
                back = configVoMapper.save(configVo);
            } else {
                back = configVoMapper.updateByUrl(configVo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return back;
    }

    @Override
    public int updateRespparams(String url, String params) {
        int back = -1;
        try {
            back = configVoMapper.updateRespparams(url,params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return back;
    }
}