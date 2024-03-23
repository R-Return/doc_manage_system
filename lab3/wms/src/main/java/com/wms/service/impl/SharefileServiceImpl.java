package com.wms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wms.entity.Sharefile;
import com.wms.mapper.SharefileMapper;
import com.wms.service.SharefileService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class SharefileServiceImpl extends ServiceImpl<SharefileMapper, Sharefile> implements SharefileService {
    @Resource
    private SharefileMapper sharefileMapper;

}
