package com.wenge.model.service.impl;

import cn.hutool.core.date.DateUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.entity.AiSearchDataAnalysis;
import com.wenge.model.entity.AiSearchFileSubHistory;
import com.wenge.model.mapper.AiSearchFileSubHistoryMapper;
import com.wenge.model.service.AiSearchDataAnalysisService;
import com.wenge.model.service.AiSearchFileSubHistoryService;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.holder.AppContextHolder;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import jodd.util.StringUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import static com.wenge.model.entity.table.AiSearchFileSubHistoryTableDef.AI_SEARCH_FILE_SUB_HISTORY;

@Service
public class AiSearchFileSubHistoryServiceImpl extends ServiceImpl<AiSearchFileSubHistoryMapper, AiSearchFileSubHistory> implements AiSearchFileSubHistoryService {

    @Autowired
    private AiSearchDataAnalysisService aiSearchDataAnalysisService;

    @Override
    public Result subOrUnSub(AiSearchFileSubHistory aiSearchFileSubHistory) {
        if (StringUtil.isBlank(aiSearchFileSubHistory.getApplicationId())) {
            return Result.fail("应用id不能为空");
        }
        if (StringUtil.isBlank(aiSearchFileSubHistory.getFileId())) {
            return Result.fail("文件id不能为空");
        }
        if (StringUtil.isBlank(aiSearchFileSubHistory.getStatus())) {
            return Result.fail("订阅状态不能为空");
        }
        TokenUser tokenOauthUserInfo = AppContextHolder.getTokenUserInfo();
        // 查询是否有浏览过或者点赞过
        QueryWrapper wrappers = Wrappers.init()
                .where(AI_SEARCH_FILE_SUB_HISTORY.FILE_ID.eq(aiSearchFileSubHistory.getFileId()))
                .and(AI_SEARCH_FILE_SUB_HISTORY.APPLICATION_ID.eq(aiSearchFileSubHistory.getApplicationId()))
                .and(AI_SEARCH_FILE_SUB_HISTORY.USER_ID.eq(tokenOauthUserInfo.getId()));
        AiSearchFileSubHistory oldAiSearchFileSubHistory = getOne(wrappers);
        if (ObjectUtils.isEmpty(oldAiSearchFileSubHistory)) {
            aiSearchFileSubHistory.setCreateTime(DateUtil.now());
            aiSearchFileSubHistory.setUpdateTime(DateUtil.now());
            aiSearchFileSubHistory.setUserId(tokenOauthUserInfo.getId());
            boolean save = save(aiSearchFileSubHistory);
            AiSearchDataAnalysis aiSearchDataAnalysis = new AiSearchDataAnalysis();
            aiSearchDataAnalysis.setApplicationId(aiSearchFileSubHistory.getApplicationId());
            aiSearchDataAnalysis.setFileId(aiSearchFileSubHistory.getFileId());
            if (save) {
                aiSearchDataAnalysisService.subOrUnSub(aiSearchDataAnalysis, aiSearchFileSubHistory.getStatus());
            }
            return Result.success();
        }
        aiSearchFileSubHistory.setUpdateTime(DateUtil.now());
        update(aiSearchFileSubHistory, wrappers);
        AiSearchDataAnalysis aiSearchDataAnalysis = new AiSearchDataAnalysis();
        aiSearchDataAnalysis.setApplicationId(aiSearchFileSubHistory.getApplicationId());
        aiSearchDataAnalysis.setFileId(aiSearchFileSubHistory.getFileId());
        if ( !aiSearchFileSubHistory.getStatus().equals(oldAiSearchFileSubHistory.getStatus())) {
            aiSearchDataAnalysisService.subOrUnSub(aiSearchDataAnalysis, aiSearchFileSubHistory.getStatus());
        }
        return Result.success();
    }
}
