package com.wenge.model.service.impl;

import cn.hutool.core.date.DateUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.entity.AiSearchDataAnalysis;
import com.wenge.model.mapper.AiSearchDataAnalysisMapper;
import com.wenge.model.service.AiSearchDataAnalysisService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import jodd.util.StringUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import static com.wenge.model.entity.table.AiSearchDataAnalysisTableDef.AI_SEARCH_DATA_ANALYSIS;

@Service
public class AiSearchDataAnalysisServiceImpl  extends ServiceImpl<AiSearchDataAnalysisMapper, AiSearchDataAnalysis> implements AiSearchDataAnalysisService {


    /**
     * 点赞
     * @param aiSearchDataAnalysis
     */
    @Override
    public Result like(AiSearchDataAnalysis aiSearchDataAnalysis) {
        if (StringUtil.isBlank(aiSearchDataAnalysis.getApplicationId())) {
            return Result.fail("应用id不能为空");
        }
        if (StringUtil.isBlank(aiSearchDataAnalysis.getFileId())) {
            return Result.fail("文件id不能为空");
        }

        // 查询是否有浏览过或者点赞过
        QueryWrapper wrappers = Wrappers.init()
                .where(AI_SEARCH_DATA_ANALYSIS.FILE_ID.eq(aiSearchDataAnalysis.getFileId()))
                .and(AI_SEARCH_DATA_ANALYSIS.APPLICATION_ID.eq(aiSearchDataAnalysis.getApplicationId()));
        AiSearchDataAnalysis oldAiSearchDataAnalysis = getOne(wrappers);
        if (ObjectUtils.isEmpty(oldAiSearchDataAnalysis)) {
            aiSearchDataAnalysis.setLikeNum(1);
            aiSearchDataAnalysis.setReadNum(0);
            aiSearchDataAnalysis.setSubNum(0);
            aiSearchDataAnalysis.setCreateTime(DateUtil.now());
            aiSearchDataAnalysis.setUpdateTime(DateUtil.now());
            save(aiSearchDataAnalysis);
            return Result.success(oldAiSearchDataAnalysis);
        }
        aiSearchDataAnalysis.setUpdateTime(DateUtil.now());
        aiSearchDataAnalysis.setLikeNum(oldAiSearchDataAnalysis.getLikeNum() + 1);
        update(aiSearchDataAnalysis, wrappers);
        return Result.success();
    }

    /**
     * 点赞
     * @param aiSearchDataAnalysis
     */
    @Override
    public Result read(AiSearchDataAnalysis aiSearchDataAnalysis) {
        if (StringUtil.isBlank(aiSearchDataAnalysis.getApplicationId())) {
            return Result.fail("应用id不能为空");
        }
        if (StringUtil.isBlank(aiSearchDataAnalysis.getFileId())) {
            return Result.fail("文件id不能为空");
        }

        // 查询是否有浏览过或者点赞过
        QueryWrapper wrappers = Wrappers.init()
                .where(AI_SEARCH_DATA_ANALYSIS.FILE_ID.eq(aiSearchDataAnalysis.getFileId()))
                .and(AI_SEARCH_DATA_ANALYSIS.APPLICATION_ID.eq(aiSearchDataAnalysis.getApplicationId()));
        AiSearchDataAnalysis oldAiSearchDataAnalysis = getOne(wrappers);
        if (ObjectUtils.isEmpty(oldAiSearchDataAnalysis)) {
            aiSearchDataAnalysis.setLikeNum(0);
            aiSearchDataAnalysis.setReadNum(1);
            aiSearchDataAnalysis.setCreateTime(DateUtil.now());
            aiSearchDataAnalysis.setUpdateTime(DateUtil.now());
            save(aiSearchDataAnalysis);
            return Result.success(oldAiSearchDataAnalysis);
        }
        aiSearchDataAnalysis.setUpdateTime(DateUtil.now());
        aiSearchDataAnalysis.setReadNum(oldAiSearchDataAnalysis.getReadNum() + 1);
        update(aiSearchDataAnalysis, wrappers);
        return Result.success();
    }

    @Override
    public Result subOrUnSub(AiSearchDataAnalysis aiSearchDataAnalysis, String status) {
        if (StringUtil.isBlank(aiSearchDataAnalysis.getApplicationId())) {
            return Result.fail("应用id不能为空");
        }
        if (StringUtil.isBlank(aiSearchDataAnalysis.getFileId())) {
            return Result.fail("文件id不能为空");
        }
        if (StringUtil.isBlank(status)) {
            return Result.fail("订阅状态不能为空");
        }
        // 查询是否有浏览过或者点赞过
        QueryWrapper wrappers = Wrappers.init()
                .where(AI_SEARCH_DATA_ANALYSIS.FILE_ID.eq(aiSearchDataAnalysis.getFileId()))
                .and(AI_SEARCH_DATA_ANALYSIS.APPLICATION_ID.eq(aiSearchDataAnalysis.getApplicationId()));
        AiSearchDataAnalysis oldAiSearchDataAnalysis = getOne(wrappers);
        if (ObjectUtils.isEmpty(oldAiSearchDataAnalysis)) {
            aiSearchDataAnalysis.setLikeNum(0);
            aiSearchDataAnalysis.setReadNum(0);
            aiSearchDataAnalysis.setSubNum(1);
            aiSearchDataAnalysis.setCreateTime(DateUtil.now());
            aiSearchDataAnalysis.setUpdateTime(DateUtil.now());
            save(aiSearchDataAnalysis);
            return Result.success(oldAiSearchDataAnalysis);
        }
        aiSearchDataAnalysis.setUpdateTime(DateUtil.now());
        if ("1".equals(status)) {
            aiSearchDataAnalysis.setSubNum(oldAiSearchDataAnalysis.getSubNum() + 1);
        } else {
            if (oldAiSearchDataAnalysis.getReadNum() >= 1) {
                aiSearchDataAnalysis.setSubNum(oldAiSearchDataAnalysis.getSubNum() - 1);
            }
        }
        update(aiSearchDataAnalysis, wrappers);
        return Result.success();
    }
}

