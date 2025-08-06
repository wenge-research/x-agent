package com.wenge.model.service;

import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.ApplicationInfoDialogueParam;
import com.wenge.model.entity.ApplicationInfoDialogueRecord;
import com.wg.appframe.core.bean.Result;

public interface ApplicationInfoDialogueRecordService extends IService<ApplicationInfoDialogueRecord> {
    public Result saveApplicationInfoDialogue(ApplicationInfoDialogueParam applicationInfoDialogueParam);
}
