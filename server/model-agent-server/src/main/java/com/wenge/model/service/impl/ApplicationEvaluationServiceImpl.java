package com.wenge.model.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.ApplicationEvaluationParam;
import com.wenge.model.dto.param.ApplicationEvaluationParamAsync;
import com.wenge.model.dto.param.DialogueByStreamParam;
import com.wenge.model.dto.template.ApplicationEvaluationDTO;
import com.wenge.model.dto.template.DatasetDTO;
import com.wenge.model.entity.*;
import com.wenge.model.mapper.ApplicationDatasetMapper;
import com.wenge.model.mapper.ApplicationEvaluationMapper;
import com.wenge.model.mapper.ApplicationInfoMapper;
import com.wenge.model.service.*;
import com.wenge.model.strategy.llmStrategy.LlmStrategy;
import com.wenge.model.utils.AnswerUtils;
import com.wenge.model.utils.DateUtil;
import com.wenge.oauth.entity.OauthUser;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.holder.AppContextHolder;
import com.wenge.oauth.service.UserService;
import com.wenge.oauth.util.JsonUtil;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.dromara.easyes.common.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static com.wenge.model.entity.table.ApplicationDatasetTableDef.APPLICATION_DATASET;
import static com.wenge.model.entity.table.ApplicationEvaluationTableDef.APPLICATION_EVALUATION;
import static com.wenge.model.entity.table.ApplicationInfoTableDef.APPLICATION_INFO;
import static com.wenge.model.entity.table.LlmInfoTableDef.LLM_INFO;

/**
 * ApplicationEvaluationService业务层处理
 *
 * @author yjz
 * @date 2025-04-22
 */
@Service
@Slf4j
public class ApplicationEvaluationServiceImpl extends ServiceImpl<ApplicationEvaluationMapper, ApplicationEvaluation> implements ApplicationEvaluationService
{
    @Autowired
    private ApplicationEvaluationMapper applicationEvaluationMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private ApplicationInfoMapper applicationInfoMapper;
    @Autowired
    private AnswerUtils answerUtils;
    @Autowired
    private ApplicationDatasetService applicationDatasetService;
    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired ApplicationDatasetMapper applicationDatasetMapper;
    @Autowired
    private LlmInfoService llmInfoService;
    @Autowired
    protected ApplicationContext applicationContext;
    @Autowired
    private KnowledgeInfoService knowledgeInfoService;
    @Autowired
    private DialogueServiceImpl dialogueServiceImpl;
    @Autowired
    private ConversationService conversationService;

    @Override
    public Result<ApplicationEvaluation> selectApplicationEvaluationById(ApplicationEvaluationParam applicationEvaluation) {
        return null;
    }

    /**
     * 查询ApplicationEvaluation列表
     *
     * @param applicationEvaluation ApplicationEvaluation
     * @return ApplicationEvaluation
     */
    @Override
    public Result<Page<ApplicationEvaluation>> selectApplicationEvaluationList(ApplicationEvaluationParam applicationEvaluation)
    {
        //数据隔离
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        String tenantId =null;
        String userid=null;
        String dept_id=null;
        if (null != tokenUserInfo&& ObjectUtil.isNotEmpty(tokenUserInfo)&&ObjectUtil.isNotEmpty(tokenUserInfo.getId())) {
            OauthUser oauthUser = userService.getById(tokenUserInfo.getId());
            tenantId = oauthUser.getTenantId();
            userid= String.valueOf(oauthUser.getId());
            dept_id=oauthUser.getDeptId();
            if(StringUtils.isEmpty(oauthUser.getTenantId())){
                tenantId=tokenUserInfo.getTenantId();
            }
        }
        Page<ApplicationEvaluation> page =new Page<>();
        if(userid.equals(1)){
            Wrappers<Object> wrappers = Wrappers.init()//makeType
                    .and(ObjectUtil.isNotEmpty(applicationEvaluation.getEvaluationStatus()) && applicationEvaluation.getEvaluationStatus() != 0, APPLICATION_EVALUATION.EVALUATION_STATUS.eq(applicationEvaluation.getEvaluationStatus()))
                    .and(StringUtils.isNotEmpty(tenantId) && !tenantId.equals(""), APPLICATION_EVALUATION.TENANT_ID.eq(tenantId))
                    //.and(StringUtils.isNotEmpty(userid) && !userid.equals(""), APPLICATION_EVALUATION.USER_ID.eq(userid))
                    //.and(StringUtils.isNotEmpty(dept_id) && !dept_id.equals(""), APPLICATION_EVALUATION.DEPT_ID.eq(dept_id))
                    .and(APPLICATION_EVALUATION.DELETE_FLAG.eq(0))
                    .and(StringUtils.isNotEmpty(applicationEvaluation.getStartTime()) && StringUtils.isNotEmpty(applicationEvaluation.getEndTime()), APPLICATION_EVALUATION.CREATE_TIME.between(applicationEvaluation.getStartTime(), applicationEvaluation.getEndTime()))
                    .orderBy(APPLICATION_EVALUATION.CREATE_TIME.desc());
            page = page(Page.of(applicationEvaluation.getPageNo(), applicationEvaluation.getPageSize()), wrappers);
        }else {
            Wrappers<Object> wrappers = Wrappers.init()//makeType
                    .and(ObjectUtil.isNotEmpty(applicationEvaluation.getEvaluationStatus()) && applicationEvaluation.getEvaluationStatus() != 0, APPLICATION_EVALUATION.EVALUATION_STATUS.eq(applicationEvaluation.getEvaluationStatus()))
                    .and(StringUtils.isNotEmpty(tenantId) && !tenantId.equals(""), APPLICATION_EVALUATION.TENANT_ID.eq(tenantId))
                    .and(StringUtils.isNotEmpty(userid) && !userid.equals(""), APPLICATION_EVALUATION.USER_ID.eq(userid))
                    .and(StringUtils.isNotEmpty(dept_id) && !dept_id.equals(""), APPLICATION_EVALUATION.DEPT_ID.eq(dept_id))
                    .and(APPLICATION_EVALUATION.DELETE_FLAG.eq(0))
                    .and(StringUtils.isNotEmpty(applicationEvaluation.getStartTime()) && StringUtils.isNotEmpty(applicationEvaluation.getEndTime()), APPLICATION_EVALUATION.CREATE_TIME.between(applicationEvaluation.getStartTime(), applicationEvaluation.getEndTime()))
                    .orderBy(APPLICATION_EVALUATION.CREATE_TIME.desc());
            page = page(Page.of(applicationEvaluation.getPageNo(), applicationEvaluation.getPageSize()), wrappers);
        }
        return Result.success(page);
    }
    /**
     * 新增ApplicationEvaluation
     *
     * @param applicationEvaluationParam ApplicationEvaluation
     * @return 结果
     */
    //@Transactional(rollbackFor = Exception.class)
    @Override
    public Result<ApplicationEvaluation> insertApplicationEvaluation(ApplicationEvaluationParam applicationEvaluationParam)
    {
        if(StringUtils.isEmpty(applicationEvaluationParam.getApplicationId())){
            return Result.fail("applicationId不能为空！");
        }
        //if(ObjectUtil.isEmpty(applicationEvaluationParam.getApplicationCode())){
         //   return Result.fail("applicationCode不能为空！");
       // }
        if(ObjectUtil.isEmpty(applicationEvaluationParam.getDatasetId())){
            return Result.fail("datasetId不能为空！");
        }
        if(ObjectUtil.isEmpty(applicationEvaluationParam.getCueWord())){
            return Result.fail("提示词不能为空！");
        }
        long startTime = System.currentTimeMillis();
        ApplicationEvaluation applicationEvaluation = new ApplicationEvaluation();
        BeanUtil.copyProperties(applicationEvaluationParam,applicationEvaluation);
        //数据隔离
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        if (null != tokenUserInfo&& ObjectUtil.isNotEmpty(tokenUserInfo)&&ObjectUtil.isNotEmpty(tokenUserInfo.getId())) {
            OauthUser oauthUser = userService.getById(tokenUserInfo.getId());
            if(StringUtils.isNotEmpty(tokenUserInfo.getTenantId())){
                applicationEvaluation.setTenantId(tokenUserInfo.getTenantId());
            }
            if(StringUtils.isNotEmpty(oauthUser.getAccountName())){
                applicationEvaluation.setCreateUser(oauthUser.getAccountName());
                applicationEvaluation.setUpdateUser(oauthUser.getAccountName());
            }
            if(StringUtils.isNotEmpty(oauthUser.getDeptId())){
                applicationEvaluation.setDeptId(oauthUser.getDeptId());
            }
            if(ObjectUtil.isNotEmpty(oauthUser.getId())){
                applicationEvaluation.setUserId(String.valueOf(oauthUser.getId()));
            }
        }
        applicationEvaluation.setCreateTime(DateUtil.getCurrentTime());
        applicationEvaluation.setUpdateTime(DateUtil.getCurrentTime());
        applicationEvaluation.setApplicationId(applicationEvaluationParam.getApplicationId());
        applicationEvaluation.setEvaluationTime(DateUtil.getCurrentTime());
        applicationEvaluation.setEvaluationStatus(2);
        applicationEvaluation.setEvaluationRules(applicationEvaluationParam.getCueWord());
        applicationEvaluation.setDeleteFlag(0);
        mapper.insert(applicationEvaluation);
        ApplicationEvaluationParamAsync applicationEvaluationParamAsync = new ApplicationEvaluationParamAsync(applicationEvaluation);
        BeanUtil.copyProperties(applicationEvaluationParam,applicationEvaluationParamAsync);
        applicationEvaluationParamAsync.setId(applicationEvaluation.getId());
        applicationEvaluationParamAsync.setUpdateTime(DateUtil.getCurrentTime());
        applicationContext.publishEvent(applicationEvaluationParamAsync);
        return Result.success(applicationEvaluation);
    }
    /**
     * 修改ApplicationEvaluation
     *
     * @param applicationEvaluationParam ApplicationEvaluation
     * @return 结果
     */
    @Override
    public Result<ApplicationEvaluation> updateApplicationEvaluation(ApplicationEvaluationParam applicationEvaluationParam)
    {
        ApplicationEvaluation applicationEvaluation = new ApplicationEvaluation();
        BeanUtil.copyProperties(applicationEvaluationParam,applicationEvaluation);
        //数据隔离
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        if (null != tokenUserInfo&& ObjectUtil.isNotEmpty(tokenUserInfo)&&ObjectUtil.isNotEmpty(tokenUserInfo.getId())) {
            OauthUser oauthUser = userService.getById(tokenUserInfo.getId());
            if(StringUtils.isNotEmpty(oauthUser.getTenantId())){
                applicationEvaluation.setTenantId(oauthUser.getTenantId());
            }
            if(StringUtils.isNotEmpty(oauthUser.getAccountName())){
                applicationEvaluation.setCreateUser(oauthUser.getAccountName());
                applicationEvaluation.setUpdateUser(oauthUser.getAccountName());
            }
            if(StringUtils.isNotEmpty(oauthUser.getDeptId())){
                applicationEvaluation.setDeptId(oauthUser.getDeptId());
            }
            if(ObjectUtil.isNotEmpty(oauthUser.getId())){
                applicationEvaluation.setUserId(String.valueOf(oauthUser.getId()));
            }
        }
        applicationEvaluation.setCreateTime(DateUtil.getCurrentTime());
        applicationEvaluation.setApplicationId(DateUtil.getCurrentTime());
        applicationEvaluation.setEvaluationTime(DateUtil.getCurrentTime());
        mapper.update(applicationEvaluation);
        return Result.success(applicationEvaluation);
    }
    /**
     * 删除ApplicationEvaluation信息
     *
     * @param applicationEvaluation
     * @return 结果
     */
    @Transactional
    @Override
    public Result deleteApplicationEvaluationById(ApplicationEvaluationParam applicationEvaluation)
    {
        if(StringUtils.isEmpty(applicationEvaluation.getDatasetId())){
            Result.fail("datesetId不能为空！");
        }
        if(ObjectUtil.isEmpty(applicationEvaluation.getId())){
            Result.fail("Id不能为空！");
        }
        mapper.deleteById(applicationEvaluation.getId());
        Wrappers<Object> wrappers = Wrappers.init()//makeType
                .and(applicationEvaluation.getDatasetId()!=null,APPLICATION_DATASET.DATASET_ID.eq(applicationEvaluation.getDatasetId()));
        applicationDatasetMapper.deleteByQuery(wrappers);
        return Result.success();
    }

    @Override
    public Result<List<ApplicationDataset>> answerModel(ApplicationEvaluationParam applicationEvaluation) {
        if(StringUtils.isEmpty(applicationEvaluation.getApplicationId())){
            return Result.fail("applicationId不能为空！");
        }
        if(ObjectUtil.isEmpty(applicationEvaluation.getApplicationCode())){
            return Result.fail("applicationCode不能为空！");
        }
        if(CollectionUtils.isEmpty(applicationEvaluation.getApplicationDataset())){
            return Result.fail("数据集不能为空！");
        }
        if(ObjectUtil.isEmpty(applicationEvaluation.getCueWord())){
            return Result.fail("提示词不能为空！");
        }
        String answerConsumer=applicationEvaluation.getCueWord();
        QueryWrapper wrapper = Wrappers.init()
                //.and(org.apache.commons.lang3.StringUtils.isNotBlank(applicationEvaluation.getApplicationCode()),APPLICATION_INFO.APPLICATION_CODE.eq(applicationEvaluation.getApplicationCode()))
                .and(StringUtils.isNotEmpty(applicationEvaluation.getApplicationId()), APPLICATION_INFO.APPLICATION_ID.eq(applicationEvaluation.getApplicationId()));
        List<ApplicationInfo> listApp= applicationInfoMapper.selectListByQuery(wrapper);
        ApplicationInfo applicationInfo = new ApplicationInfo();
        if(CollectionUtils.isNotEmpty(listApp)&& !listApp.isEmpty()){
            applicationInfo= listApp.get(0);
        }
        DialogueServiceImpl.APPLICATION_INFO.set(applicationInfo);
        List<ApplicationDataset> Dataset = applicationEvaluation.getApplicationDataset();
        List<ApplicationDataset> applicationDataset = new ArrayList<>();
        int i=0;
        for (ApplicationDataset in : Dataset) {
            applicationDataset.add(in);
            i++;
            if(i==3){
                break;
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        //调用智能体生成答案
        if (CollectionUtils.isNotEmpty(applicationDataset)) {
            Iterator<ApplicationDataset> iterator=applicationDataset.iterator();
            while (iterator.hasNext()) {
                ApplicationDataset appDataset = iterator.next();
                //封装数据
                DialogueServiceImpl.APPLICATION_INFO.set(applicationInfo);
                DialogueByStreamParam dialogueByStreamParam = new DialogueByStreamParam();
                dialogueByStreamParam.setClientId(UUID.randomUUID().toString());
                dialogueByStreamParam.setClientType("PC");
                dialogueByStreamParam.setApplicationId(applicationInfo.getApplicationId());
                List<KnowledgeInfo> knowledgeInfos = knowledgeInfoService.effectiveKnowledge(applicationInfo.getApplicationId());
                List<String> knowledgeIdList = new ArrayList<>();
                for (KnowledgeInfo knowledgeInfo : knowledgeInfos) {
                    knowledgeIdList.add(knowledgeInfo.getKnowledgeId());
                }
                dialogueByStreamParam.setKnowledgeIdList(knowledgeIdList);
                dialogueByStreamParam.setContent(appDataset.getInput());
                dialogueByStreamParam.setQuestion(appDataset.getInput());
                Conversation conversation = new Conversation();
                TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
                if (null != tokenUserInfo && ObjectUtil.isNotEmpty(tokenUserInfo) && ObjectUtil.isNotEmpty(tokenUserInfo.getId())) {
                    OauthUser oauthUser = userService.getById(tokenUserInfo.getId());
                    if (StringUtils.isNotEmpty(tokenUserInfo.getTenantId())) {
                        conversation.setTenantId(tokenUserInfo.getTenantId());
                    }
                    if (StringUtils.isNotEmpty(oauthUser.getAccountName())) {
                        conversation.setCreateUser(oauthUser.getAccountName());
                        conversation.setUpdateUser(oauthUser.getAccountName());
                    }
                }
                conversation.setName(appDataset.getInput());
                conversation.setCreateTime(DateUtil.getCurrentTime());
                conversation.setUpdateTime(DateUtil.getCurrentTime());
                conversation.setConversationId(IdUtil.simpleUUID());
                dialogueByStreamParam.setConversationId(conversation.getConversationId());
                conversationService.save(conversation);
                dialogueServiceImpl.dialogueByStreamEvaluation(dialogueByStreamParam);
                String answer = dialogueByStreamParam.getAnswer();
                if(StringUtils.isEmpty(answer)){
                    return Result.fail("大模型出错");
                }
                answer=answer.replace(" ", "").replace("\n", "").replace("\r", "");
                appDataset.setOutput(answer);
            }
        }
        Iterator<ApplicationDataset> iterator = applicationDataset.iterator();
        while (iterator.hasNext()){
            ApplicationDataset datasetDTO = iterator.next();
            stringBuilder.append(",").append("question_id=").append(datasetDTO.getQuestionId()).append(",")
                    .append("session_id=").append(datasetDTO.getSessionId()).append(",")
                    .append("query_id=").append(datasetDTO.getQueryId()).append(",")
                    .append("input=").append(datasetDTO.getInput()).append(",")
                    .append("output=").append(datasetDTO.getOutput()).append(",")
                    .append("reference_output=").append(datasetDTO.getReferenceOutput()).append(",");
        }
        stringBuilder.append(answerConsumer);
        //拼接输出格式
        stringBuilder.append("请严格按照Json格式\n" +
                "[{\n" +
                "    \"question_id\": \"\",\n" +
                "    \"query_id\": \"\",\n" +
                "    \"session_id\": \"\",\n" +
                "    \"input\": \"\",\n" +
                "    \"reference_output\": \"\",\n" +
                "    \"output\": \"\",\n" +
                "    \"score\": \"\",\n" +
                "    \"score_reason\": \"\"\n" +
                "}]\n" +
                "返回");

        // 获取模型信息
        applicationInfo = new ApplicationInfo();
        // 获取模型信息
        Wrappers<Object> wrappers2 = Wrappers.init()
                .where(LLM_INFO.MODEL_ID.eq(applicationEvaluation.getLlmInfoId()))
                .limit(1);
        LlmInfo llmInfo11 = llmInfoService.getOne(wrappers2);
        if (null == llmInfo11) {
            log.warn("未找到模型：{}", applicationEvaluation.getLlmInfoId());
        }
        applicationInfo.setLlmInfo(llmInfo11);
        // 生成答案
        if (BeanUtil.isNotEmpty(llmInfo11)) {
            applicationInfo.setLlmInfo(llmInfo11);
            DialogueServiceImpl.APPLICATION_INFO.set(applicationInfo);
            // 模型生成答案
            String bigModeString =answerUtils.getGenerateCommon(null,stringBuilder.toString(),  new StepEntity(),null, null);
            int startIndex1 = bigModeString.indexOf("[");
            int endIndex1 = bigModeString.lastIndexOf("]");
            if (startIndex1 != -1&&endIndex1!= -1) { // 确保找到了关键字
                bigModeString= bigModeString.substring(startIndex1, endIndex1+1);
            } else {
                return Result.fail("大模型出错");
            }
            List<DatasetDTO> datasetDTOS = JsonUtil.jsonToList(bigModeString, DatasetDTO.class);
            Iterator<ApplicationDataset> iterator2 = applicationDataset.iterator();
            while (iterator2.hasNext()){
                ApplicationDataset datasetDTO = iterator2.next();
                for(DatasetDTO dto:datasetDTOS){
                    if(datasetDTO.getQuestionId().equals(dto.getQuestion_id())){
                        datasetDTO.setScore(dto.getScore());
                        datasetDTO.setScoreReason(dto.getScore_reason());
                    }
                }
            }
        }
        return Result.success(applicationDataset);
    }

    @Override
    public Result export(ApplicationEvaluationParam applicationEvaluationParam, HttpServletResponse response) throws IOException {
        Wrappers<Object> wrappers = Wrappers.init()//makeType
                .and(applicationEvaluationParam.getId()!=null,APPLICATION_EVALUATION.ID.eq(applicationEvaluationParam.getId()));
        List<ApplicationEvaluation> applicationEvaluations = mapper.selectListByQuery(wrappers);
        List<ApplicationEvaluationDTO> applicationEvaluationDTOS =new ArrayList<>();
        for (ApplicationEvaluation applicationEvaluation : applicationEvaluations) {
            ApplicationEvaluationDTO applicationEvaluationDTO = new ApplicationEvaluationDTO();
            BeanUtil.copyProperties(applicationEvaluation,applicationEvaluationDTO);
            applicationEvaluationDTOS.add(applicationEvaluationDTO);
        }
        BeanUtil.copyProperties(applicationEvaluations,applicationEvaluations);
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode("评测数据.xlsx", String.valueOf(StandardCharsets.UTF_8)));
        //resetCellMaxTextLength();
        EasyExcel.write(response.getOutputStream(), ApplicationEvaluationDTO.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .sheet("评测数据")
                .doWrite(applicationEvaluationDTOS);
        return Result.success();
    }

    @Override
    public void downloadInterceptWordDataTemp(HttpServletResponse response) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            Resource resource = resourceLoader.getResource("classpath:temp/cozeEvaluation.xlsx");
            inputStream = resource.getInputStream();

            // 设置响应头
            response.setContentType(MediaType.TEXT_PLAIN_VALUE);
            // 获取文件名，并进行UTF-8编码
            String fileName = URLEncoder.encode("测评模板下载.xlsx", StandardCharsets.UTF_8.toString());
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

            // 将文件写入响应输出流
            outputStream = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != inputStream) {
                    inputStream.close();
                }
                if (null != outputStream) {
                    outputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void downloadInterceptWordDataTemp2(HttpServletResponse response) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            Resource resource = resourceLoader.getResource("classpath:temp/cozeEvaluation2.xlsx");
            inputStream = resource.getInputStream();

            // 设置响应头
            response.setContentType(MediaType.TEXT_PLAIN_VALUE);
            // 获取文件名，并进行UTF-8编码
            String fileName = URLEncoder.encode("测评模板下载.xlsx", StandardCharsets.UTF_8.toString());
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

            // 将文件写入响应输出流
            outputStream = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != inputStream) {
                    inputStream.close();
                }
                if (null != outputStream) {
                    outputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
