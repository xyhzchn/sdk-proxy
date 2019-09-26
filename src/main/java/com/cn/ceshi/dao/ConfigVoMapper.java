package com.cn.ceshi.dao;

import com.cn.ceshi.model.ConfigVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by wangly on 2018/3/1.
 */
public interface ConfigVoMapper {

    int updateByUrl(ConfigVo configVo);

    int save(ConfigVo configVo);

    ConfigVo getByUrl(String url);

    List<ConfigVo> list(ConfigVo configVo);

    long count();

    int updateRespparams(@Param("url") String url, @Param("respParams") String respParams);

}
