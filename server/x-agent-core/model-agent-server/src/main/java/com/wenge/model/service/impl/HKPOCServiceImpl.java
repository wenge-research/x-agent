package com.wenge.model.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import com.wenge.model.constants.RedisKey;
import com.wenge.model.dto.param.FilePageParam;
import com.wenge.model.dto.param.HKParam;
import com.wenge.model.service.FileService;
import com.wenge.model.service.HKPOCService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.redis.service.RedisService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HKPOCServiceImpl implements HKPOCService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private FileService fileService;

    // 设置缓存一个小时
    long HK_REQUEST_ID_EXPIRE_TIME = 60 * 60;

    @Override
    public Result queryRequestId(HKParam hkParam) {
        if (ObjectUtils.isEmpty(hkParam) || CollectionUtil.isEmpty(hkParam.getFileIds())) {
            return Result.fail("请选择文件id");
        }
        String uuid = IdUtil.simpleUUID();
        // 先从缓存中获取
        String key = RedisKey.HK_REQUEST_ID + uuid;
        redisService.set(key, hkParam.getFileIds(), HK_REQUEST_ID_EXPIRE_TIME);
        return Result.success(uuid);
    }

    @Override
    public Result questFilesByRequestId(HKParam hkParam) {
        if (ObjectUtils.isEmpty(hkParam) || StringUtils.isEmpty(hkParam.getRequestId())) {
            return Result.success();
        }
        // 先从缓存中获取
        String key = RedisKey.HK_REQUEST_ID + hkParam.getRequestId();
        // 先从缓存中获取
        List files = CollectionUtil.newArrayList();
        if (redisService.hasKey(key)) {
            files =  redisService.get(key, List.class);
        }
        return Result.success(files);
    }


    @Override
    public Result getFileListByFileIds(HKParam hkParam) {

        List<String> fileIds = (List<String>) this.questFilesByRequestId(hkParam).getData();
        FilePageParam filePageParam = new FilePageParam();
        filePageParam.setFileIds(fileIds);
        filePageParam.setPageNo(hkParam.getPageNo());
        filePageParam.setPageSize(hkParam.getPageSize());
        return fileService.getFileList(filePageParam);
    }
}
