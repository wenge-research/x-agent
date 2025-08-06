package com.wenge.model.dto.param;

import com.wenge.model.entity.DialogueCache;
import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class DialogueCacheAddParam extends WGParam {

    private static final long serialVersionUID = 5788832274403140927L;

    /**
     * 对话缓存列表
     */
    private List<DialogueCache> dialogueCacheList;
}
