package com.wenge.model.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.wenge.model.entity.ApplicationKnowledge;
import com.wenge.model.entity.Dialogue;
import com.wenge.model.entity.KnowledgeData;
import com.wenge.model.enums.ApplicationKnowledgeTypeEnum;
import com.wenge.model.mapper.ApplicationKnowledgeMapper;
import com.wenge.model.mapper.es.DialogueMapper;
import com.wenge.model.mapper.es.KnowledgeDataMapper;
import com.wenge.model.service.CleanQADataService;
import com.wenge.oauth.entity.OauthUser;
import com.wenge.oauth.mapper.UserMapper;
import com.wg.appframe.core.bean.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wenge.model.entity.table.ApplicationKnowledgeTableDef.APPLICATION_KNOWLEDGE;

@Service
@Slf4j
public class CleanQADataServiceImpl  implements CleanQADataService {


    @Autowired
    private DialogueMapper dialogueMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private KnowledgeDataMapper knowledgeDataMapper;

    @Autowired
    private ApplicationKnowledgeMapper applicationKnowledgeMapper;

    /**
     * @description: 使用对话日志中的userName字段补全userId字段
     * @author: caohaifeng
     * @date: 2024/9/25 11:03
     **/
    @Override
    public Result useinguserNameToUserId() {
        LambdaEsQueryWrapper<Dialogue> wrapper = EsWrappers.lambdaQuery(Dialogue.class)
                .exists(Dialogue::getUserName).not().exists(Dialogue::getUserId)
                .orderByDesc("createTime.keyword");
        String source = dialogueMapper.getSource(wrapper);
        log.info("==>useinguserNameToUserId dsl:{}", source);
        final List<Dialogue> dialogues = dialogueMapper.selectList(wrapper);
        Map<String, OauthUser> oauthUserMap = new HashMap<>();
        for (Dialogue dialogue : dialogues) {
            OauthUser oauthUser = oauthUserMap.get(dialogue.getUserName());
            if (oauthUser == null) {
                QueryWrapper queryWrapper = QueryWrapper.create()
                        .where(" status<> '4' and user_name = " + "'" + dialogue.getUserName() + "'").limit(1);
                final List<OauthUser> oauthUsers = userMapper.selectListByQuery(queryWrapper);
                if (oauthUsers != null && oauthUsers.size() > 0) {
                    oauthUserMap.put(dialogue.getUserName(), oauthUsers.get(0));
                    dialogue.setUserId(oauthUsers != null ? oauthUsers.get(0).getId() + "" : null);
                    dialogueMapper.updateById(dialogue);
                }else { //有脏数据 直接使用userName赋值userId
                    dialogue.setUserId(dialogue.getUserName());
                    dialogueMapper.updateById(dialogue);
                }
            }else {
                dialogue.setUserId(oauthUser.getId() + "");
                dialogueMapper.updateById(dialogue);
            }
        }
        return Result.success(dialogues.size());
    }

    /**
     * @description: 使用对话日志中的feedbackUserId字段补全deptId相关字段
     * @author: caohaifeng
     * @date: 2024/9/25 11:03
     **/
    @Override
    public Result useingFeedbackUserIdToDeptId() {
        LambdaEsQueryWrapper<Dialogue> wrapper = EsWrappers.lambdaQuery(Dialogue.class)  //.not().exists(Dialogue::getFeedbackDeptId).or()
                .exists(Dialogue::getFeedbackUserId).eq("feedbackDeptId.keyword", "")
                .orderByDesc("createTime.keyword");
        String source = dialogueMapper.getSource(wrapper);
        log.info("==> useingFeedbackUserIdToDeptId dsl:{}", source);
        final List<Dialogue> dialogues = dialogueMapper.selectList(wrapper);
        Map<String, OauthUser> oauthUserMap = new HashMap<>();
        for (Dialogue dialogue : dialogues) {
            OauthUser oauthUser = oauthUserMap.get(dialogue.getUserName());
            if (oauthUser == null) {
                QueryWrapper queryWrapper = QueryWrapper.create()
                        .where(" status<> '4' and id = " + "'" + dialogue.getFeedbackUserId() + "'").limit(1);
                final List<OauthUser> oauthUsers = userMapper.selectListByQuery(queryWrapper);
                if (oauthUsers != null && oauthUsers.size() > 0) {
                    OauthUser user = oauthUsers.get(0);
                    oauthUserMap.put(dialogue.getUserName(), user);
                    dialogue.setFeedbackDeptId(StringUtils.isNotBlank(user.getDeptId()) ? user.getDeptId() + "" : "-1");
                    dialogueMapper.updateById(dialogue);
                }else { //有脏数据 直接使用userName赋值userId
                    dialogue.setFeedbackDeptId("-1");
                    dialogueMapper.updateById(dialogue);
                }
            }else {
                dialogue.setFeedbackDeptId(StringUtils.isNotBlank(oauthUser.getDeptId()) ? oauthUser.getDeptId() + "" : "-1");
                dialogueMapper.updateById(dialogue);
            }
        }
        return Result.success(dialogues.size());
    }

    /**
     * @description: 补全QA问题中的userId deptId字段
     * @author: caohaifeng
     * @date: 2024/9/25 11:03
     **/
    @Override
    public Result useingQAuserIdDeptId() {
        LambdaEsQueryWrapper<Dialogue> wrapper = EsWrappers.lambdaQuery(Dialogue.class)
                .eq(Dialogue::getFeedbackType,"0")
                .exists(Dialogue::getFeedbackUserId).exists(Dialogue::getFeedbackDeptId)
                .orderByDesc("createTime.keyword");
        String source = dialogueMapper.getSource(wrapper);
        log.info("==> useingQAuserIdDeptId dsl:{}", source);
        final List<Dialogue> dialogues = dialogueMapper.selectList(wrapper);
        for (Dialogue dialogue : dialogues) {
            List<String> knowIds = new ArrayList<>();
            final List<ApplicationKnowledge> applicationKnowledges = applicationKnowledgeMapper.selectListByQuery(QueryWrapper.create()
                    .where(APPLICATION_KNOWLEDGE.APPLICATION_ID.eq(dialogue.getApplicationId()))
                    .and(APPLICATION_KNOWLEDGE.TYPE.eq(ApplicationKnowledgeTypeEnum.KNOWLEDGE)));
            if(CollectionUtil.isNotEmpty(applicationKnowledges)){
                applicationKnowledges.forEach(applicationKnowledge ->
                        knowIds.add(applicationKnowledge.getKnowledgeId())
                );
            }
            LambdaEsQueryWrapper<KnowledgeData> queryWrapper = EsWrappers.lambdaQuery(KnowledgeData.class)
                    .eq(KnowledgeData::getDelStatus, "0")
                    .in(KnowledgeData::getKnowledgeId, knowIds)
                    .eq(KnowledgeData::getTitle, dialogue.getQuestion())
                    .not().exists(KnowledgeData::getUserId)
                    .orderByDesc(KnowledgeData::getUpdateTime);
            List<KnowledgeData> knowledgeDataList =  knowledgeDataMapper.selectList(queryWrapper);
            if (knowledgeDataList != null && knowledgeDataList.size() > 0) {
                KnowledgeData update = knowledgeDataList.get(0);
                update.setDeptId(dialogue.getFeedbackDeptId());
                update.setDeptName(dialogue.getFeedbackDeptName());
                update.setUserId(dialogue.getFeedbackUserId());
                update.setUserName(dialogue.getFeedbackUserName());
                update.setDataSource("5");
                knowledgeDataMapper.updateById(update);
            }
        }
        return Result.success(dialogues.size());
    }

}