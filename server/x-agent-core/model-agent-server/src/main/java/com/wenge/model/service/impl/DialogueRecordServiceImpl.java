package com.wenge.model.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson2.JSON;
import com.mybatisflex.core.query.QueryWrapper;
import com.wenge.model.constants.SystemVerifyContant;
import com.wenge.model.dto.param.*;
import com.wenge.model.entity.ApplicationInfo;
import com.wenge.model.entity.Dialogue;
import com.wenge.model.entity.KnowledgeData;
import com.wenge.model.entity.KnowledgeInfo;
import com.wenge.model.enums.DialoguAuditStatusEnum;
import com.wenge.model.enums.DialoguFromTabEnum;
import com.wenge.model.enums.DialogueVerifyStatusEnum;
import com.wenge.model.mapper.es.DialogueMapper;
import com.wenge.model.service.ApplicationInfoService;
import com.wenge.model.service.DialogueRecordService;
import com.wenge.model.service.KnowledgeDataService;
import com.wenge.model.service.KnowledgeInfoService;
import com.wenge.model.utils.DateUtil;
import com.wenge.model.utils.EasyExcelUtil;
import com.wenge.oauth.constants.AppConfigContant;
import com.wenge.oauth.entity.ApplicationConfiguration;
import com.wenge.oauth.entity.OauthDept;
import com.wenge.oauth.entity.OauthUser;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.enums.PowerTypeEnum;
import com.wenge.oauth.holder.AppContextHolder;
import com.wenge.oauth.mapper.UserMapper;
import com.wenge.oauth.service.ApplicationConfigurationService;
import com.wenge.oauth.service.OauthDeptService;
import com.wenge.oauth.service.UserService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.core.constant.enums.YesNoEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.core.biz.EsPageInfo;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.wenge.oauth.constants.AppConfigContant.APP_CONFIG_GUANXIN_KNOWLEDGE_ENABLE;
import static com.wenge.oauth.constants.AppConfigContant.APP_CONFIG_GUANXIN_PUBLIC_KNOWLEDGE_ID;

@Service
@Slf4j
public class DialogueRecordServiceImpl implements DialogueRecordService {

    @Autowired
    private DialogueMapper dialogueMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private KnowledgeDataService knowledgeDataService;

    @Autowired
    private KnowledgeInfoService knowledgeInfoService;

    @Autowired
    private OauthDeptService oauthDeptService;

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationInfoService applicationInfoService;

    @Autowired
    private ApplicationConfigurationService applicationConfigurationService;

//    @Value("${appConfig.guanxin.publicKnowledgeId}")
//    private String publicKnowledgeId;
//    @Value("${appConfig.guanxin.privateKnowledgeId}")
//    private String privateKnowledgeId;

    @Override
    public Result<EsPageInfo<Dialogue>> getRecord(RecordPageParam param) {
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        LambdaEsQueryWrapper<Dialogue> wrapper = getWrapper(param);

        if (param.getIsMyRecord() == 1)  {
            if (null == tokenUserInfo) {
                log.info("未获取到用户信息");
                return Result.success(new EsPageInfo<Dialogue>());
            }
            String userId = String.valueOf(tokenUserInfo.getId());

            wrapper.eq(StringUtils.isNotBlank(userId), Dialogue::getUserId, userId);
        }
        String source = dialogueMapper.getSource(wrapper);
        log.info("==>dsl:{}", source);
        EsPageInfo<Dialogue> page = dialogueMapper.pageQuery(wrapper, param.getPageNo(), param.getPageSize());
        return Result.success(page);
    }

    @Override
    public List<Dialogue> getRecorByApplicationIdsAndQuestions(List<String> questions, List<String> applicationIds) {
        if (CollectionUtil.isEmpty(questions) || CollectionUtil.isEmpty(applicationIds)) {
            return new ArrayList<>();
        }
        LambdaEsQueryWrapper<Dialogue> wrapper = EsWrappers.lambdaQuery(Dialogue.class)
                .in(Dialogue::getQuestion, questions)
                .in(Dialogue::getApplicationId, applicationIds)
                .size(9999);
        String source = dialogueMapper.getSource(wrapper);
        log.info("==>dsl:{}", source);
        List<Dialogue> dialogues = dialogueMapper.selectList(wrapper);
        if (CollectionUtil.isEmpty(dialogues)) {
            return new ArrayList<>();
        }

        // 去除同一个应用中的重复问题
        List<Dialogue> distinctDialogues = dialogues.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                d -> d.getApplicationId() + "_" + d.getQuestion(),
                                Function.identity(),
                                (existing, replacement) -> replacement
                        ),
                        map -> new ArrayList<>(map.values())
                ));

        return distinctDialogues;
    }

    @Override
    public Result bindKnn(KnnBindParam param) {
        if (StringUtils.isBlank(param.getAppId())) {
            return Result.fail("参数不合法");
        }

        // 添加配置
        List<ApplicationConfiguration> configurationList = applicationConfigurationService.getConfigByAppId(param.getAppId());
        // 审核通过的推送到指定的知识库，保存知识库id
        ApplicationInfoServiceImpl.updateConfig(configurationList, param.getAppId(), APP_CONFIG_GUANXIN_PUBLIC_KNOWLEDGE_ID, param.getKnnId(), "审核通过的推送到指定的知识库，保存知识库id");
        String enable = "true";
        if (StringUtils.isBlank(param.getKnnId())) {
            enable = "false";
        }
        ApplicationInfoServiceImpl.updateConfig(configurationList, param.getAppId(), APP_CONFIG_GUANXIN_KNOWLEDGE_ENABLE, enable, "是否同步到公开或者私有知识库中 true-同步 false不同步");
        applicationConfigurationService.updateAppConfig(configurationList, param.getAppId());
        return Result.success();
    }

    @Override
    public Result<String> getBindKnn(KnnBindParam param) {
        if (StringUtils.isBlank(param.getAppId())) {
            return Result.fail("参数不合法");
        }

        String configuration = AppConfigContant.getConfiguration(param.getAppId(), APP_CONFIG_GUANXIN_PUBLIC_KNOWLEDGE_ID);
        return Result.success(configuration);
    }

    @Override
    public Result<EsPageInfo<Dialogue>> getLastOneRecord(LastestRecordPageParam param) {
        Assert.notBlank(param.getApplicationId(), "应用id不能为空");

        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        if (null == tokenUserInfo) {
            log.info("未获取到用户信息");
            return Result.success(new EsPageInfo<>());
        }

        String userId = String.valueOf(tokenUserInfo.getId());


        // 第一阶段：获取目标会话ID（最新或最早）
/*        LambdaEsQueryWrapper<Dialogue> idQueryWrapper = EsWrappers.lambdaQuery(Dialogue.class)
                .eq(Dialogue::getApplicationId, param.getApplicationId())
                .eq(Dialogue::getUserId, userId)
                .exists(Dialogue::getQuestion)
                .exists(Dialogue::getAnswer)
                .exists(Dialogue::getConversationId)
                .orderByDesc(Dialogue::getCreateTime) // 默认按时间倒序
                .size(1); // 只取1条记录

        Dialogue targetDialogue = dialogueMapper.selectOne(idQueryWrapper);
        if (targetDialogue == null) {
            return Result.success(new EsPageInfo<>());
        }

        // 对话记录中最新的会话id
        String targetConversationId = targetDialogue.getConversationId();*/

        // 第二阶段：根据会话类型构建查询条件
        LambdaEsQueryWrapper<Dialogue> dataQueryWrapper = EsWrappers.lambdaQuery(Dialogue.class)
                .eq(Dialogue::getApplicationId, param.getApplicationId())
                .eq(Dialogue::getUserId, userId)
                .exists(Dialogue::getQuestion)
                .exists(Dialogue::getAnswer)
                .exists(Dialogue::getConversationId)
                .orderByDesc(Dialogue::getCreateTime);

        // 根据标志位选择查询条件
/*        if (StringUtils.equals(param.getLastestFlag(), "0")) {
            // 排除对话记录中最新的会话的记录
            dataQueryWrapper
                    .not().eq(Dialogue::getConversationId, targetConversationId);
        } else {
            // 对话记录中最新的会话
            dataQueryWrapper.eq(Dialogue::getConversationId, targetConversationId);
        }*/

        EsPageInfo<Dialogue> dialogueEsPageInfo = dialogueMapper.pageQuery(dataQueryWrapper, param.getPageNo(), param.getPageSize());

        List<Dialogue> dialogues = dialogueEsPageInfo.getList();
        if (CollectionUtil.isEmpty(dialogues)) {
            return Result.success(new EsPageInfo<>());
        }

        // 最总结果按创建时间正序排列
        dialogues = dialogues.stream()
                .sorted(Comparator.comparing(Dialogue::getCreateTime))
                .collect(Collectors.toList());
        dialogueEsPageInfo.setList(dialogues);

        // 使用ES原生分页查询
        return Result.success(dialogueEsPageInfo);
    }

    @Override
    public Result getCountByAppId(RecordPageParam param) {
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        LambdaEsQueryWrapper<Dialogue> wrapper = getWrapper(param);

        if (param.getIsMyRecord() == 1)  {
            if (null == tokenUserInfo) {
                log.info("未获取到用户信息");
                return Result.success(new EsPageInfo<Dialogue>());
            }
            String userId = String.valueOf(tokenUserInfo.getId());

            wrapper.eq(StringUtils.isNotBlank(userId), Dialogue::getUserId, userId);
        }
        String source = dialogueMapper.getSource(wrapper);
        log.info("==>dsl:{}", source);
        Long count = dialogueMapper.selectCount(wrapper);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMonthAgo = LocalDateTimeUtil.offset(now, -1, java.time.temporal.ChronoUnit.MONTHS);
        LocalDateTime twoMonthAgo = LocalDateTimeUtil.offset(now, -2, java.time.temporal.ChronoUnit.MONTHS);
        param.setTimeStart(twoMonthAgo.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        param.setTimeEnd(oneMonthAgo.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        Long lastMount = dialogueMapper.selectCount(getWrapper(param));
        param.setTimeStart(oneMonthAgo.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        param.setTimeEnd(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        Long currentMount = dialogueMapper.selectCount(getWrapper(param));
        HashMap<Object, Object> map = new HashMap<>();
        map.put("total", count);
        map.put("unit", "次");
        DecimalFormat df = new DecimalFormat("#.####");
        // (当月-上月) / 上月
        map.put("QOQ", Double.valueOf(lastMount) != 0.0d ? df.format((Double.valueOf(currentMount) - Double.valueOf(lastMount)) / Double.valueOf(lastMount) * 100) + "%" : "暂无");
        return Result.success(map);
    }

    @Override
    public void exportRecord(RecordPageParam param, HttpServletResponse response) {
        LambdaEsQueryWrapper<Dialogue> wrapper = getWrapper(param);
        wrapper.size(9999);
        wrapper.select(Dialogue::getQuestion, Dialogue::getAnswer, Dialogue::getCreateTime, Dialogue::getUserName, Dialogue::getFeedbackType, Dialogue::getFeedbackContent);
        List<Dialogue> dialogues = dialogueMapper.selectList(wrapper);
        String dateFormat = LocalDateTimeUtil.format(LocalDateTimeUtil.now(), "yyyy_MM_dd") + ".xlsx";
        try {
            // 获取所有部门
            List<OauthDept> allDept = oauthDeptService.getAllDept();
            // 部门id和部门名称的映射
            Map<String, String> deptIdNameMap = allDept.stream().collect(Collectors.toMap(
                    OauthDept::getDeptId,
                    OauthDept::getDeptName,
                    (k1, k2) -> k1
            ));

            // 获取所有用户
            List<OauthUser> userList = userService.getUserByTenant();
            // 用户名和部门id的映射
            Map<String, String> userNameDpetIdMap = userList.stream().collect(Collectors.toMap(
                    OauthUser::getUsername,
                    p -> StringUtils.isBlank(p.getDeptId()) ? StringConstant.BLANK : p.getDeptId(),
                    (k1, k2) -> k1
            ));
            OutputStream outputStream = response.getOutputStream();
            response.setContentType(MediaType.TEXT_PLAIN_VALUE); // 设置为二进制流类型
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + dateFormat + "\""); // 添加引号以支持中文文件名
            List<List<String>> rows = Lists.newArrayList();
            rows.add(ListUtil.toList("问题", "答案", "对话时间", "用户", "部门", "赞踩", "反馈内容"));

            List<ArrayList<String>> dataList = dialogues.stream().map(p -> {
                String deptId = userNameDpetIdMap.getOrDefault(p.getUserName(), StringConstant.BLANK);
                String deptName = StringConstant.BLANK;
                if (StringUtils.isNotBlank(deptId)) {
                    if (StringUtils.isNotBlank(deptId)) {
                        deptName = deptIdNameMap.getOrDefault(deptId, StringConstant.BLANK);
                    }
                }
                //if (StringUtils.isBlank(deptName)) {
                //    deptName = "其他";
                //}
                String feedbackType = StringConstant.BLANK;
                if (StringUtils.isNotBlank(p.getFeedbackType())) {
                    feedbackType = "1".equals(p.getFeedbackType()) ? "赞" : "踩";
                }
                return ListUtil.toList(p.getQuestion(), p.getAnswer(), p.getCreateTime(), p.getUserName(), deptName, feedbackType, p.getFeedbackContent());
            }).collect(Collectors.toList());
            rows.addAll(dataList);
            EasyExcelUtil.export(rows, outputStream);
        } catch (IOException e) {
            log.error("Excel导出失败", e);
        }

    }

    /**
     * 构建查询条件
     *
     * @param param
     * @return
     */
    private LambdaEsQueryWrapper getWrapper(RecordPageParam param) {
        List<String> userIds = null;
        if (StringUtils.isNotEmpty(param.getUserType())) {
            userIds = new ArrayList<>();
            QueryWrapper queryWrapper = QueryWrapper.create()
                    .where(" user_type like " + "'" + param.getUserType() + "%'").limit(1000);
            final List<OauthUser> oauthUsers = userMapper.selectListByQuery(queryWrapper);
            if (CollectionUtil.isNotEmpty(oauthUsers)) {
                List<String> finalUserIds = userIds;
                for (OauthUser oauthUser : oauthUsers) {
                    userIds.add(oauthUser.getId() + "");
                }
            }
        }

        // 获取当前用户部门名称，针对日志分拨的页面
        List<String> deptNameList = Lists.newArrayList();
        if (DialoguFromTabEnum.ALLOCATE.getType().equals(param.getFromTab())) {
            deptNameList = authControl(param.getDeptId(), param.getApplicationId());
        }

        LambdaEsQueryWrapper<Dialogue> wrapper = EsWrappers.lambdaQuery(Dialogue.class)
                .ge(StringUtils.isNotBlank(param.getTimeStart()), Dialogue::getCreateTime, param.getTimeStart())
                .le(StringUtils.isNotBlank(param.getTimeEnd()), Dialogue::getCreateTime, param.getTimeEnd())
                .matchPhrasePrefixQuery(StringUtils.isNotBlank(param.getQuestion()), Dialogue::getQuestion, param.getQuestion())
                .matchPhrasePrefixQuery(StringUtils.isNotBlank(param.getAnswer()), Dialogue::getAnswer, param.getAnswer())
                .in(CollectionUtils.isNotEmpty(param.getApplicationIds()), Dialogue::getApplicationId, param.getApplicationIds())
                .eq(StringUtils.isNotBlank(param.getApplicationId()), Dialogue::getApplicationId, param.getApplicationId())
                .eq(StringUtils.isNotBlank(param.getVerifyStatus()), Dialogue::getVerifyStatus, param.getVerifyStatus())
                //.eq(StringUtils.isNotBlank(param.getDeptId()), Dialogue::getVerifyDeptId, param.getDeptId())
                .eq(StringUtils.isNotBlank(param.getVerifyDeptId()), Dialogue::getVerifyDeptId, param.getVerifyDeptId())
                .eq(StringUtils.isNotBlank(param.getConversationId()), Dialogue::getConversationId, param.getConversationId())
                .eq(param.getDeleted() != null, Dialogue::getDeleted, param.getDeleted())
                .eq(StringUtils.isNotBlank(param.getFeedbackType()), Dialogue::getFeedbackType, param.getFeedbackType())
                .in(userIds != null, Dialogue::getUserId, userIds)
                .eq(StringUtils.isNotBlank(param.getUserId()), Dialogue::getUserId, param.getUserId())
                .like(StringUtils.isNotBlank(param.getUserName()), Dialogue::getUserName, param.getUserName())
                //查询未回答的问题
                .and(param.getNoAnswerFlag() != null && param.getNoAnswerFlag(), and -> {
                    and.eq(Dialogue::getAnswerFlag, "1");
                })
                //查询能够回答的问题
                .and(param.getNoAnswerFlag() != null && !param.getNoAnswerFlag(), and -> {
                    and.eq(Dialogue::getAnswerFlag, "0");
                })
                .and(StringUtils.isNotBlank(param.getText()), and ->
                        {
                            and.or(or -> or.matchPhrasePrefixQuery(Dialogue::getAnswer, param.getText()));
                            and.or(or -> or.matchPhrasePrefixQuery(Dialogue::getQuestion, param.getText()));
                        }
                )
                .in(CollectionUtil.isNotEmpty(deptNameList), Dialogue::getDivisionDeptName, deptNameList)
                .exists(param.isLikeFlag(), Dialogue::getFeedbackType)
                .orderByDesc("createTime.keyword");
        String source = dialogueMapper.getSource(wrapper);
        log.info("==>dsl:{}", source);
        return wrapper;
    }


    @Override
    public Result feedback(FeedbackParam feedbackParam) {
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        StringBuffer resBuffer = new StringBuffer("对话记录[点赞点踩] 执行结果：");
        EsWrappers.lambdaChainUpdate(dialogueMapper)
                .set(Dialogue::getFeedbackContent, StringUtils.isNotBlank(feedbackParam.getFeedbackContent()) ? feedbackParam.getFeedbackContent() : feedbackParam.getContent())
                .set(Dialogue::getFeedbackType, feedbackParam.getFeedbackType())
                .set(Dialogue::getFeedbackUserId, tokenUserInfo.getId())
                .set(Dialogue::getFeedbackUserName, tokenUserInfo.getUserName())
                .set(Dialogue::getBasePublic, feedbackParam.isBasePublic())
                .set(Dialogue::getFeedbackDeptId, tokenUserInfo.getDeptId())
                .set(Dialogue::getFeedbackDeptName, tokenUserInfo.getDeptName())
                .set(Dialogue::getFeedbackTime, LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DateUtil.PATTERN_1))
                .eq(Dialogue::getDialogueId, feedbackParam.getDialogueId())
                .update();
        /*
         * 把点赞点踩放开，增对关芯助手的点踩反馈内容同步知识库。
         * （选择公开：同步到关芯客服知识库
         * 选择不公开：同步到关芯助理知识库）
         *
         **/
        //点踩
        if (StringUtils.isBlank(feedbackParam.getContent())) {
            return Result.success("内容为空，不执行后续逻辑");
        }
        final String enable = AppConfigContant.getConfiguration(feedbackParam.getApplicationId() + "@" + APP_CONFIG_GUANXIN_KNOWLEDGE_ENABLE);
        resBuffer.append(feedbackParam.getApplicationType() + "_");
        if ("true".equals(enable) || "TRUE".equals(enable)) {
            if ("guanxin-zhuli".equals(feedbackParam.getApplicationType())) { //关芯助理-工作人员使用
                if ("0".equals(feedbackParam.getFeedbackType())) { //踩
                    String knowledgeId = AppConfigContant.getConfiguration(feedbackParam.getApplicationId() + "@" + AppConfigContant.APP_CONFIG_GUANXIN_PRIVATE_KNOWLEDGE_ID);
                    if (feedbackParam.isBasePublic()) {
                        knowledgeId = AppConfigContant.getConfiguration(feedbackParam.getApplicationId() + "@" + APP_CONFIG_GUANXIN_PUBLIC_KNOWLEDGE_ID);
                    }
                    if (StringUtils.isNotBlank(knowledgeId)) {
                        Dialogue dialogue = new Dialogue();
                        dialogue.setQuestion(feedbackParam.getTitle());
                        dialogue.setAnswer(feedbackParam.getContent());
                        EsWrappers.lambdaChainUpdate(dialogueMapper)
                                .set(Dialogue::getFeedbackPullKnowledgeId, feedbackParam.getDialogueId())
                                .eq(Dialogue::getDialogueId, feedbackParam.getDialogueId())
                                .update();
                        return pushKnowledgeData(dialogue, knowledgeId, feedbackParam.getDialogueId(), "5", tokenUserInfo);// 来源系统推送QA对
                    } else {
                        resBuffer.append("没有配置推送知识库_");
                    }

                } else {
                    resBuffer.append("非点踩_不需要要推送知识库_");
                }
            } else if ("guanxin-kefu".equals(feedbackParam.getApplicationType())) { //关芯客服-用户使用 判断用户是否工作人员 工作人员可以推送数据到知识库
                if (tokenUserInfo != null && tokenUserInfo.getId() != null && tokenUserInfo.getUserType().contains("gov")) { //工作人员
                    if ("0".equals(feedbackParam.getFeedbackType())) { //踩
                        String knowledgeId = AppConfigContant.getConfiguration(feedbackParam.getApplicationId() + "@" + AppConfigContant.APP_CONFIG_GUANXIN_PRIVATE_KNOWLEDGE_ID);
                        if (feedbackParam.isBasePublic()) {
                            knowledgeId = AppConfigContant.getConfiguration(feedbackParam.getApplicationId() + "@" + APP_CONFIG_GUANXIN_PUBLIC_KNOWLEDGE_ID);
                        }
                        if (StringUtils.isNotBlank(knowledgeId)) {
                            Dialogue dialogue = new Dialogue();
                            dialogue.setQuestion(feedbackParam.getTitle());
                            dialogue.setAnswer(feedbackParam.getContent());
                            EsWrappers.lambdaChainUpdate(dialogueMapper)
                                    .set(Dialogue::getFeedbackPullKnowledgeId, feedbackParam.getDialogueId())
                                    .eq(Dialogue::getDialogueId, feedbackParam.getDialogueId())
                                    .update();
                            return pushKnowledgeData(dialogue, knowledgeId, feedbackParam.getDialogueId(), "5", tokenUserInfo);// 来源系统推送QA对
                        } else {
                            resBuffer.append("没有配置推送知识库_");
                        }
                    } else {
                        resBuffer.append("非点踩_不需要要推送知识库_");
                    }
                } else {
                    resBuffer.append("非工作人员_不需要要推送知识库_");
                }
            }
        }
        return Result.success(resBuffer.toString());
    }


    @Override
    public Result<EsPageInfo<Dialogue>> getCheckRecord(RecordCheckParam param) {
        // 查询有审核状态的数据
        EsPageInfo<Dialogue> page = EsWrappers.lambdaChainQuery(dialogueMapper)
                .ge(StringUtils.isNotBlank(param.getTimeStart()), Dialogue::getCreateTime, param.getTimeStart())
                .le(StringUtils.isNotBlank(param.getTimeEnd()), Dialogue::getCreateTime, param.getTimeEnd())
                .eq(StringUtils.isNotBlank(param.getDeptId()), Dialogue::getVerifyDeptId, param.getDeptId())
                .eq(StringUtils.isNotBlank(param.getAuditStatus()), Dialogue::getAuditStatus, param.getAuditStatus())
                .eq(StringUtils.isNotBlank(param.getApplicationId()), Dialogue::getApplicationId, param.getApplicationId())
                .exists(Dialogue::getAuditStatus)
                .and(StringUtils.isNotBlank(param.getText()), and ->
                        {
                            and.or(or -> or.matchPhrasePrefixQuery(Dialogue::getAnswer, param.getText()));
                            and.or(or -> or.matchPhrasePrefixQuery(Dialogue::getQuestion, param.getText()));
                        }
                )
                .orderByDesc("createTime.keyword")
                .page(param.getPageNo(), param.getPageSize());

        return Result.success(page);
    }

    @Override
    public Result verifyRecord(RecordVerifyParam param) {
        if (StringUtils.isBlank(param.getDialogueId())) {
            return Result.fail("记录不能为空");
        }

        // 获取当前用户信息
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();

        String auditStatus = DialoguAuditStatusEnum.WAIT_AUDIT.getCode();
        //已核实正确 -> 审核通过
        if (DialogueVerifyStatusEnum.VERIFY_CORRECT.getCode().equals(param.getVerifyStatus())) {
            auditStatus = DialoguAuditStatusEnum.AUDIT_PASS.getCode();

            //已核实修改 -> 审核通过
        } else if (DialogueVerifyStatusEnum.VERIFY_MODIFY.getCode().equals(param.getVerifyStatus())) {
            auditStatus = DialoguAuditStatusEnum.AUDIT_PASS.getCode();

            //恶意问题 -> 审核不通过
        } else if (DialogueVerifyStatusEnum.MALICIOUS_QUESTION.getCode().equals(param.getVerifyStatus())) {
            auditStatus = DialoguAuditStatusEnum.AUDIT_NOT_PASS.getCode();

            //不处置 -> 不处理
        } else if (DialogueVerifyStatusEnum.NOT_DISPOSE.getCode().equals(param.getVerifyStatus())) {
            auditStatus = DialoguAuditStatusEnum.NOT_DISPOSE.getCode();
        }

        // 更新记录
        EsWrappers.lambdaChainUpdate(dialogueMapper)
                .set(Dialogue::getVerifyStatus, param.getVerifyStatus())
                .set(Dialogue::getVerifyAnswer, param.getVerifyAnswer())
                .set(Dialogue::getVerifyUserId, tokenUserInfo.getId())
                .set(Dialogue::getVerifyUserName, tokenUserInfo.getUserName())
                .set(Dialogue::getVerifyDeptId, tokenUserInfo.getDeptId())
                .set(Dialogue::getVerifyDeptName, tokenUserInfo.getDeptName())
//                .set(Dialogue::getAuditStatus, auditStatus)
                .eq(Dialogue::getDialogueId, param.getDialogueId())
                .update();

        // 正确 和错误（修改之后答案推送QA） 其他不推送
        if (DialogueVerifyStatusEnum.VERIFY_MODIFY.getCode().equals(param.getVerifyStatus())
                || DialogueVerifyStatusEnum.VERIFY_CORRECT.getCode().equals(param.getVerifyStatus())) {

            //根据dialogueId查询应用id
            Dialogue dialogueIndex = EsWrappers.lambdaChainQuery(dialogueMapper)
                    .eq(Dialogue::getDialogueId, param.getDialogueId())
                    .one();
            RecordAuditParam recordAuditParam = new RecordAuditParam();
            recordAuditParam.setDialogueId(param.getDialogueId());
            recordAuditParam.setAuditStatus(auditStatus);
            recordAuditParam.setVerifyAnswer(param.getVerifyAnswer());
//            recordAuditParam.setAuditComment(param.getVerifyAnswer());
            if (dialogueIndex != null) {
                recordAuditParam.setApplicationId(dialogueIndex.getApplicationId());
            }
            //调用二次审核（推数据导QA对中）
            log.info("对话日志审核 正确或者错误（补充正确答案）的记录推送QA对");
            return checkRecord(recordAuditParam);
        }
        return Result.success();
    }

    @Override
    public Result checkRecord(RecordAuditParam param) {
        if (StringUtils.isBlank(param.getDialogueId())) {
            return Result.fail("记录不能为空");
        }
        if (StringUtils.isBlank(param.getApplicationId())) {
            return Result.fail("应用不能为空");
        }

        // 获取当前用户信息
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();

        // 查询记录
        List<Dialogue> dialogueList = EsWrappers.lambdaChainQuery(dialogueMapper)
                .eq(Dialogue::getDialogueId, param.getDialogueId())
                .list();
        if (CollectionUtil.isEmpty(dialogueList)) {
            return Result.success();
        }

        Dialogue dialogue = dialogueList.get(0);

        // 更新对话记录
        EsWrappers.lambdaChainUpdate(dialogueMapper)
                .set(Dialogue::getAuditStatus, param.getAuditStatus())
                .set(Dialogue::getAuditComment, param.getAuditComment())
                .set(Dialogue::getAuditUserId, tokenUserInfo.getId())
                .set(Dialogue::getAuditUserName, tokenUserInfo.getUserName())
                .set(Dialogue::getAuditTime, DateUtil.getCurrentTime())
                .eq(Dialogue::getDialogueId, param.getDialogueId())
                .update();

        // 审核通过，将数据推到问答库
        if (DialoguAuditStatusEnum.AUDIT_PASS.getCode().equals(param.getAuditStatus())) {
            dialogue.setVerifyAnswer(param.getVerifyAnswer());
            pushingKnowledgeData(dialogue, param.getApplicationId(), "3", tokenUserInfo);
        }
        return Result.success();
    }


    /**
     * @description: 推送相同问题的数据到知识库  并删除之前有的问题
     * @author: caohaifeng
     * @date: 2024/9/12 9:54
     **/
    @Override
    public Result pushingKnowledgeData(Dialogue dialogue, String applicationId, String dataSource, TokenUser tokenUser) {
        // 查询知识库，审核通过之后，将数据推到知识库
        List<KnowledgeInfo> knowledgeInfoList = knowledgeInfoService.effectiveKnowledge(applicationId);
        if (CollectionUtil.isEmpty(knowledgeInfoList)) {
            return Result.fail("未绑定知识库");
        }
        List<String> knowledgeList = knowledgeInfoList.stream().map(KnowledgeInfo::getKnowledgeId).collect(Collectors.toList());

        String knowledgeId = AppConfigContant.getConfiguration(applicationId + "@" + APP_CONFIG_GUANXIN_PUBLIC_KNOWLEDGE_ID);

        //读取公开库配置
        if (knowledgeId != null && knowledgeList.contains(knowledgeId)) {
            // 删除已存在的问题
            knowledgeDataService.deleteByTitle(dialogue.getQuestion(), knowledgeList);
            pushKnowledgeData(dialogue, knowledgeId, null, dataSource, tokenUser);
            return Result.success("公开库数据推送成功，库ID:" + knowledgeId);
        }
        return Result.success("未配置公开库不需要推送");
    }


    @Override
    public Result checkFeedbackAuditRecord(RecordFeedbackAuditParam param) {
        if (StringUtils.isBlank(param.getDialogueId())) {
            return Result.fail("记录不能为空");
        }
        if (StringUtils.isBlank(param.getApplicationId())) {
            return Result.fail("应用不能为空");
        }

        // 获取当前用户信息
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();

        // 查询记录
        List<Dialogue> dialogueList = EsWrappers.lambdaChainQuery(dialogueMapper)
                .eq(Dialogue::getDialogueId, param.getDialogueId())
                .list();
        if (CollectionUtil.isEmpty(dialogueList)) {
            return Result.success();
        }

        // 更新对话记录
        final Integer update = EsWrappers.lambdaChainUpdate(dialogueMapper)
                .set(Dialogue::getFeedbackAuditStatus, param.getFeedbackAuditStatus())
                .set(Dialogue::getFeedbackAuditComment, param.getFeedbackAuditComment())
                .set(Dialogue::getFeedbackAuditUserId, tokenUserInfo.getId())
                .set(Dialogue::getFeedbackAuditUserName, tokenUserInfo.getUserName())
                .set(Dialogue::getFeedbackAuditTime, DateUtil.getCurrentTime())
                .eq(Dialogue::getDialogueId, param.getDialogueId())
                .update();
        return Result.success(update);
    }

    @Override
    public Result logicDelete(RecordAuditParam param) {
        Assert.notBlank(param.getDialogueId(), "记录不能为空");
        // 更新对话记录
        EsWrappers.lambdaChainUpdate(dialogueMapper)
                .set(Dialogue::getDeleted, YesNoEnum.YES.getCode())
                .eq(Dialogue::getDialogueId, param.getDialogueId())
                .update();
        return Result.success();
    }

    @Override
    public Result updateYYZFPicUrl(RecordVerifyParam param) {
        log.info("DialogueRecordServiceImpl.updateYYZFPicUrl param is ：{}", JSON.toJSONString(param));
        if (StringUtils.isBlank(param.getDialogueId())) {
            return Result.fail("记录不能为空");
        }
        if (StringUtils.isBlank(param.getApplicationId())) {
            return Result.fail("应用不能为空");
        }
        // 查询记录
        List<Dialogue> dialogueList = EsWrappers.lambdaChainQuery(dialogueMapper)
                .eq(Dialogue::getDialogueId, param.getDialogueId())
                .list();
        if (CollectionUtil.isEmpty(dialogueList)) {
            return Result.success();
        }
        Dialogue dialogue = dialogueList.get(0);
        // 更新对话记录
        EsWrappers.lambdaChainUpdate(dialogueMapper)
                .set(Dialogue::getYyzhUrl, param.getYyzhUrl())
                .set(Dialogue::getYyzhUrlUploadTime, DateUtil.getCurrentTime())
                .eq(Dialogue::getDialogueId, param.getDialogueId())
                .update();
        return Result.success();
    }

    @Override
    public List<Dialogue> getByConversationId(String conversationId) {
        List<Dialogue> messagesList = new ArrayList<>();
        if (StringUtils.isBlank(conversationId)) {
            return messagesList;
        }


        LambdaEsQueryWrapper<Dialogue> wrapper = EsWrappers.lambdaQuery(Dialogue.class)
                .select(Dialogue::getAnswer, Dialogue::getQuestion)
                .eq(Dialogue::getConversationId, conversationId)
                .orderByDesc(Dialogue::getCreateTime)
                .size(999);

        return dialogueMapper.selectList(wrapper);
    }


    /**
     * @Param dialogueId 对话日志中的ID
     * @Param dataSource 数据来源
     **/
    private Result pushKnowledgeData(Dialogue dialogue, String knowledgeId, String dialogueId, String dataSource, TokenUser tokenUserInfo) {
        KnowledgeData knowledgeData = new KnowledgeData();
        knowledgeData.setTitle(dialogue.getQuestion());
        String content = StringUtils.isNotBlank(dialogue.getVerifyAnswer()) ? dialogue.getVerifyAnswer() : dialogue.getAnswer();
        knowledgeData.setDialogueId(dialogueId);
        knowledgeData.setContent(content);
        knowledgeData.setKnowledgeId(knowledgeId);
        knowledgeData.setAccurate(YesNoEnum.NO.getName());
        knowledgeData.setSuggest("推荐");
        knowledgeData.setPolishFlag(YesNoEnum.YES.getName());
        knowledgeData.setDataSource(dataSource);
        if (tokenUserInfo != null) {
            knowledgeData.setDeptId(tokenUserInfo.getDeptId());
            knowledgeData.setDeptName(tokenUserInfo.getDeptName());
            if (tokenUserInfo.getId() != null && tokenUserInfo.getId() == -1L) {
                knowledgeData.setUserId(SystemVerifyContant.SYSTEM_VERIFY_USER_ID);
            } else {
                knowledgeData.setUserId(tokenUserInfo.getId() + "");
            }
            knowledgeData.setUserName(tokenUserInfo.getUserName());
        }
        return knowledgeDataService.addKnowledgeData(knowledgeData);
    }

    /**
     * 权限控制
     *
     * @return
     */
    private List<String> authControl(String deptId, String applictionId) {
        // 获取当前用户部门名称
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();

        // 获取当前用户信息，如果没有登录场景
        if (null == tokenUserInfo || null == tokenUserInfo.getId()) {
            // 获取应用，并且判断是否需要认证
            ApplicationInfo activeApp = applicationInfoService.getActiveApp(applictionId, StringConstant.BLANK, YesNoEnum.YES.getName());
            if (null == activeApp) {
                // 应用不存在，则看不到日志
                return ListUtil.toList("-1");
            }

            // 如果pc需要认证
            String pcAuthChannel = activeApp.getPcAuthChannel();
            if (StringUtils.isNotBlank(pcAuthChannel)) {
                // 需要认证，则看不到日志
                return ListUtil.toList("-1");
            } else {
                // 不需要认证，则可以看到所有数据
                return null;
            }
        }

        // 如果是超级管理员或者管理员，可以看到所有数据
        if (PowerTypeEnum.NORMAL_ADMIN.getCode().equals(tokenUserInfo.getPowerType())
                || PowerTypeEnum.SYSTEM_ADMIN.getCode().equals(tokenUserInfo.getPowerType())) {
            // 如果是管理员，则可以看到指定部门数据
            if (StringUtils.isNotBlank(deptId)) {
                return getDeptName(deptId);
            }
            // 如果是管理员，则可以看到所有部门数据
            return null;
        }

        // 当前用户只能看到自己部门和子级部门的数据
        List<String> deptIdList = oauthDeptService.getAllChildrenId(tokenUserInfo.getDeptId());
        if (StringUtils.isNotBlank(tokenUserInfo.getDeptId())) {
            deptIdList.add(tokenUserInfo.getDeptId());
        }
        // 判断当前用户是否可以查看该部门数据
        if (StringUtils.isNotBlank(deptId) && !deptIdList.contains(deptId)) {
            return ListUtil.toList("-1");
        }
        List<OauthDept> allDept = oauthDeptService.getAllDept();
        Map<String, String> deptIdNameMap = allDept.stream().collect(Collectors.toMap(
                OauthDept::getDeptId,
                OauthDept::getDeptName,
                (k1, k2) -> k1
        ));

        if (CollectionUtil.isNotEmpty(deptIdList)) {
            return deptIdList.stream().map(deptIdNameMap::get).distinct().collect(Collectors.toList());
        }
        return ListUtil.toList("-1");
    }

    /**
     * 获取部门名称
     */
    private List<String> getDeptName(String deptId) {
        List<String> deptIdList = oauthDeptService.getAllChildrenId(deptId);
        deptIdList.add(deptId);
        List<OauthDept> allDept = oauthDeptService.getAllDept();
        Map<String, String> deptIdNameMap = allDept.stream().collect(Collectors.toMap(
                OauthDept::getDeptId,
                OauthDept::getDeptName,
                (k1, k2) -> k1
        ));

        if (CollectionUtil.isNotEmpty(deptIdList)) {
            return deptIdList.stream().map(deptIdNameMap::get).distinct().collect(Collectors.toList());
        }
        return ListUtil.toList("-1");
    }
}
