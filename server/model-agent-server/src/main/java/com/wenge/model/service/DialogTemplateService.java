package com.wenge.model.service;

import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.DialogTemplateParam;
import com.wenge.model.dto.param.TemplateStatusUpdateParam;
import com.wenge.model.entity.DialogTemplate;
import com.wg.appframe.core.bean.Result;

import java.util.List;

/**
 * Description: 聊天模板服务类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-25 17:16:27
 *
 */
public interface DialogTemplateService extends IService<DialogTemplate> {

    Result addDialogTemplate(DialogTemplate dialogTemplate);

    Result getDialogTemplateList(DialogTemplateParam dialogTemplate);

    Result updateDialogTemplate(DialogTemplate dialogTemplate);

    Result deleteDialogTemplate(List<String> idList);

    Result updateStatus(TemplateStatusUpdateParam param);

    Result setPreset(TemplateStatusUpdateParam param);
}