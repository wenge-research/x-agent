package com.wenge.model.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.wenge.model.dto.param.DialogueCacheAddParam;
import com.wenge.model.entity.DialogueCache;
import com.wenge.model.service.DialogueCacheService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class DialogueCacheServiceImpl implements DialogueCacheService {
    @Autowired
    private RedisService redisService;

    @Override
    public Result<String> addDialogueCache(DialogueCacheAddParam param) {

        if(CollectionUtil.isEmpty(param.getDialogueCacheList())){
            return Result.fail("参数为空");
        }

        String redisKey = IdUtil.randomUUID();

        List<DialogueCache> dialogueCacheList = param.getDialogueCacheList();
        redisService.set(redisKey, JSONUtil.toJsonStr(dialogueCacheList), 60 * 60 * 24 * 7);


        return Result.success(redisKey);
    }

    @Override
    public Result<List<DialogueCache>> getDialogueCache(String param) {

        if(StringUtils.isBlank(param)){
            return Result.fail("参数为空");
        }

        String jsonData = redisService.get(param, String.class);

        if (StringUtils.isNotBlank(jsonData)) {
            List<DialogueCache> DialogueCacheData = JSONUtil.toList(jsonData, DialogueCache.class);
            return Result.success(DialogueCacheData);
        }

        return Result.fail("数据不存在或已过期");


    }


}
