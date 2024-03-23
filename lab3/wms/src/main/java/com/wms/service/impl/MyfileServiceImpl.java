package com.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wms.entity.Myfile;
import com.wms.entity.User;
import com.wms.mapper.MyfileMapper;
import com.wms.mapper.UserMapper;
import com.wms.service.MyfileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class MyfileServiceImpl extends ServiceImpl<MyfileMapper, Myfile> implements MyfileService {
    @Resource
    private MyfileMapper myfileMapper;

    @Override
    public IPage pageCC(IPage<Myfile> page, Wrapper wrapper) {
        return myfileMapper.pageCC(page,wrapper);
    }
}
