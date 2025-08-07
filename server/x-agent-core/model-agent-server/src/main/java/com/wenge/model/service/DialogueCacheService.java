package com.wenge.model.service;

import com.wenge.model.dto.param.DialogueCacheAddParam;
import com.wenge.model.entity.DialogueCache;
import com.wg.appframe.core.bean.Result;

import java.util.List;

public interface DialogueCacheService {
    Result<String> addDialogueCache(DialogueCacheAddParam param);


    Result<List<DialogueCache>> getDialogueCache(String param);
}
