package com.cn.ceshi.dao;


import com.cn.ceshi.model.Log2DB;

import java.util.List;


/**
 * Created by wangly on 2017/7/31.
 */
public interface Log2DBtMapper {

    int save(Log2DB log2DB);

    List<Log2DB> list(Log2DB log2DB);

    long count(Log2DB log2DB);
}
