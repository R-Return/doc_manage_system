package com.wms.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wms.entity.Myfile;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wms.entity.User;


public interface MyfileService extends IService<Myfile> {
    IPage pageCC(IPage<Myfile> page, Wrapper wrapper);
}
