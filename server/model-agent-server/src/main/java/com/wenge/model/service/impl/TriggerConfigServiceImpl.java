package com.wenge.model.service.impl;

import cn.hutool.core.util.IdUtil;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.TriggerConfigParam;
import com.wenge.model.mapper.TriggerConfigMapper;
import com.wenge.model.service.TriggerConfigService;
import com.wenge.model.workflow.entity.TriggerConfig;
import com.wg.appframe.core.bean.Result;

import com.wg.appframe.mybatisflex.core.Wrappers;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.wenge.model.workflow.entity.table.TriggerConfigTableDef.TRIGGER_CONFIG;

@Service
public class TriggerConfigServiceImpl extends ServiceImpl<TriggerConfigMapper, TriggerConfig> implements TriggerConfigService {


    @Override
    public Result addOrUpdate(TriggerConfig triggerConfig) {
        if (StringUtils.isBlank(triggerConfig.getTriggerId())) {
            triggerConfig.setTriggerId(IdUtil.simpleUUID());
        }

        boolean isValid = CronExpression.isValidExpression(triggerConfig.getCrontab());
        if (!isValid) {
            return Result.fail("Cron表达式无效");
        }
        mapper.insertOrUpdate(triggerConfig);
        return Result.success(triggerConfig);
    }

    @Override
    public Result<Page<TriggerConfig>> queryAll(TriggerConfigParam param) {
        Wrappers<Object> queryWrapper = Wrappers.init();
        queryWrapper.where(StringUtils.isNotBlank(param.getComponentId()), TRIGGER_CONFIG.COMPONENT_ID.like(param.getComponentId()))
                .and(StringUtils.isNotBlank(param.getApplicationId()), TRIGGER_CONFIG.APPLICATION_ID.eq(param.getApplicationId()))
                .and(ObjectUtils.isNotEmpty(param.getStatus()), TRIGGER_CONFIG.STATUS.eq(param.getStatus()))
                .orderBy(TRIGGER_CONFIG.CREATED_TIME.desc());
        Page<TriggerConfig> page = page(Page.of(param.getPageNo(), param.getPageSize()), queryWrapper);
        return Result.success(page);

    }

    @Override
    public Result queryDetails(TriggerConfigParam param) {
        Wrappers<Object> queryWrapper = Wrappers.init();
        queryWrapper.where(StringUtils.isNotBlank(param.getComponentId()), TRIGGER_CONFIG.COMPONENT_ID.eq(param.getComponentId()))
                .and(StringUtils.isNotBlank(param.getApplicationId()), TRIGGER_CONFIG.APPLICATION_ID.eq(param.getApplicationId()))
                .and(ObjectUtils.isNotEmpty(param.getStatus()), TRIGGER_CONFIG.STATUS.eq(param.getStatus()))
                .orderBy(TRIGGER_CONFIG.CREATED_TIME.desc());
        return Result.success(getOne(queryWrapper));
    }
}
