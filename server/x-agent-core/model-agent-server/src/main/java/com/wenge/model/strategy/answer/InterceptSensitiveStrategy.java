package com.wenge.model.strategy.answer;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.wenge.model.constants.AnswerStrategyContant;
import com.wenge.model.constants.RedisKey;
import com.wenge.model.dto.param.DialogueByStreamParam;
import com.wenge.model.dto.result.AnswerStepData;
import com.wenge.model.dto.result.SourceAnswerResult;
import com.wenge.model.entity.ApplicationInfo;
import com.wenge.model.entity.DialogueStep;
import com.wenge.model.entity.IpBlacklist;
import com.wenge.model.entity.StepEntity;
import com.wenge.model.enums.InterceptTypeEnum;
import com.wenge.model.enums.StepStatusEnum;
import com.wenge.model.mapper.IpBlacklistMapper;
import com.wenge.model.service.impl.DialogueServiceImpl;
import com.wenge.model.utils.AnswerUtils;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.core.constant.enums.YesNoEnum;
import com.wg.appframe.redis.service.RedisService;
import com.wg.appframe.yayi.api.YayiServer;
import com.wg.appframe.yayi.config.SecurityConfig;
import com.wg.appframe.yayi.constants.StringConstans;
import com.wg.appframe.yayi.result.SecurityResult;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.pinyin4j.PinyinHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Vector;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

import static com.wenge.model.constants.AnswerStrategyContant.*;

@Service(AnswerStrategyContant.INTERCEPT_SENSITIVE)
@Slf4j
public class InterceptSensitiveStrategy implements AnswerStrategy {

    private static final Pattern TONE_PATTERN = Pattern.compile("[0-5]");

    @Autowired
    private RedisService redisService;

    @Autowired
    private IpBlacklistMapper ipBlacklistMapper;

    @Autowired
    private YayiServer yayiServer;

    @Autowired
    private SecurityConfig securityConfig;

    @Autowired
    private AnswerUtils answerUtils;

    @Override
    public AnswerStepData getAnswer(DialogueByStreamParam dto, Vector<StepEntity> contextList, AnswerStepData answerData) {
        long start = System.currentTimeMillis();
        StepEntity lengthStep = new StepEntity();
        lengthStep.setCreateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        try {
            // 获取当前应用
            ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
            if (!YesNoEnum.YES.getName().equals(applicationInfo.getSensitiveFlag())) {
                answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
                return answerData;
            }

            // 问题后边补去掉问号
            String content = dto.getContentTemp().replaceAll("[?？]+$", StringConstant.BLANK).replace(" ", StringConstant.BLANK).trim();

            // 问题拦截
            contextList.add(lengthStep);

            // 拦截问题全词
            boolean passFlag = checkSensitive(content, applicationInfo, contextList, answerData, dto.getIpAddress(), InterceptTypeEnum.INTERCEPT_ALL_QUESTION, INTERCEPT_ALL_QUESTION,
                    (not, pinyin) -> content.equalsIgnoreCase(not) || content.equalsIgnoreCase(pinyin));
            if (!passFlag) {
                dto.getAnswerConsumer().accept(answerData);
                return answerData;
            }

            // 拦截提问
            passFlag = checkSensitive(content, applicationInfo, contextList, answerData, dto.getIpAddress(), InterceptTypeEnum.INTERCEPT_QUESTION, INTERCEPT_QUESTION,
                    (not, pinyin) -> content.toLowerCase().contains(not.toLowerCase()) || content.toLowerCase().contains(pinyin.toLowerCase()));
            if (!passFlag) {
                dto.getAnswerConsumer().accept(answerData);
                return answerData;
            }

            // 拦截禁用词
            passFlag = checkSensitive(content, applicationInfo, contextList, answerData, dto.getIpAddress(), InterceptTypeEnum.FORBIDDEN_WORD, INTERCEPT_QUESTION_FORBIDDEN,
                    (not, pinyin) -> content.toLowerCase().contains(not.toLowerCase()) || content.toLowerCase().contains(pinyin.toLowerCase()));
            if (!passFlag) {
                dto.getAnswerConsumer().accept(answerData);
                return answerData;
            }

            // YAYI拦截敏感词
            boolean sensitive = checkYayiSensitive(content, contextList, answerData, applicationInfo, dto.getIpAddress());
            if (sensitive) {
                dto.getAnswerConsumer().accept(answerData);
                return answerData;
            }
        } catch (Exception e) {
            log.error("InterceptSensitiveStrategy error:{}", e.getMessage(), e);
            answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
            lengthStep.setResult(ERROR_STEP + ":" + e.getMessage());
        } finally {
            lengthStep.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
            lengthStep.setCostTime(System.currentTimeMillis() - start);
        }
        return answerData;
    }


    /**
     * 汉字转拼音
     *
     * @param chinese
     * @return
     */
    public String toPinyin(String chinese) {
        StringBuilder out = new StringBuilder();
        for (char c : chinese.toCharArray()) {
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c);
            if (pinyinArray != null) {
                String pinyin = pinyinArray[0];
                // 移除声调
                pinyin = TONE_PATTERN.matcher(pinyin).replaceAll("");
                out.append(pinyin);
            } else {
                out.append(c);
            }
        }
        return out.toString().replace(" ", "");
    }

    private void checkSensitiveNum(String ipAddress, String word) {
        if (StringUtils.isBlank(ipAddress)) {
            return;
        }
        String[] split = ipAddress.split(",");
        for (String ip : split) {
            String redisKey = RedisKey.SENSITIVE_IP + ip;
            String numStr = redisService.getStr(redisKey);
            if (StringUtils.isNotBlank(numStr)) {
                long num = Long.parseLong(numStr);
                num = num + 1;
                if (num > 3) {
                    saveIpBlack(ip, word);
                }
                redisService.set(redisKey, num, 100 * 60);
            } else {
                redisService.set(redisKey, 1, 100 * 60);
            }
        }
    }

    /**
     * 敏感词触发，禁用IP
     *
     * @param ipAddress
     * @param word
     */
    private void saveIpBlack(String ipAddress, String word) {
        IpBlacklist ipBlacklist = new IpBlacklist();
        ipBlacklist.setIpAddress(ipAddress);
        ipBlacklist.setRemark("触发敏感词次数，最近一次敏感词：" + word);
        ipBlacklistMapper.insert(ipBlacklist, true);

    }

    /**
     * 检测文本是否敏感
     *
     * @param content
     * @return
     */
    private SecurityResult isSensitive(String content, List<StepEntity> contextList) {
        // 调用方已经保存了step
//        StepEntity step = new StepEntity();
//        contextList.add(step);
//        step.setStep(INTERCEPT_YAYI_SECRI);
//        step.setPrompt(content);
//        step.setResult(INTERCEPT_PASS);
        return yayiServer.security(content);
        //if (StringConstans.CODE.equals(security.getCode())) {
        //    SecurityResult.Data data = security.getData();
        //    if (null != data) {
        //        if (data.getIs_danger() == 1) {
        //            // 指定的敏感等级不算敏感词
        //            if (CollectionUtil.isNotEmpty(securityConfig.getSecurityLevel())) {
        //                for (String level : securityConfig.getSecurityLevel()) {
        //                    if (StringUtils.isNotBlank(data.getDanger_words()) && data.getDanger_words().contains(level)) {
        //                        sensitive = false;
        //                    }
        //                }
        //            }
        //            sensitive = true;
        //        }
        //        sensitive = false;
        //    }
        //}
        ////sensitive = yayiServer.securityBoolean(content);
        //if (sensitive) {
        //    log.info(">>>>>>>>>文本出现敏感词<<<<<<<<<<<<<");
        //} else {
        //    step.setResult(INTERCEPT_YAYI_SECRI_NOT);
        //}
        //return sensitive;
    }

    @Override
    public List<SourceAnswerResult> notAnswer(List<DialogueStep> dialogueSteps, boolean interceptFlag) {
        List<SourceAnswerResult> results = Lists.newArrayList();
        // 非拦截
        if (!interceptFlag) {
            return results;
        }
        Optional<DialogueStep> any = dialogueSteps.stream()
                .filter(p -> StringUtils.isNotBlank(p.getStep()) && p.getStep().contains(INTERCEPT)
                        && null != p.getResult() && p.getResult().toString().contains(INTERCEPT_NOT_PASS))
                .findAny();
        // 判断是否有安全拦截
        if (any.isPresent()) {
            DialogueStep dialogueStep = any.get();
            SourceAnswerResult result = new SourceAnswerResult();
            result.setAnswerFlag("否");
            result.setText(dialogueStep.getResult().toString().replace("\"", ""));
            result.setRoute(ListUtil.toList(dialogueStep.getStep()));
            results.add(result);
        }
        return results;
    }

    /**
     * 检测文本是否敏感
     *
     * @param applicationInfo
     * @param contextList
     * @param answerData
     * @param ipAddr
     * @return
     */
    private boolean checkSensitive(String content, ApplicationInfo applicationInfo, Vector<StepEntity> contextList, AnswerStepData answerData, String ipAddr, InterceptTypeEnum interceptTypeEnum, String stepName, BiFunction<String, String, Boolean> condition) {
        boolean passFlag = true;
        List<String> notQuestion = answerUtils.getInterceptWord(applicationInfo.getApplicationId(), interceptTypeEnum.getName());
        String pinyin;
        StepEntity step = new StepEntity();
        step.setStep(stepName);
        step.setResult(INTERCEPT_PASS);
        contextList.add(step);
        if (CollectionUtils.isNotEmpty(notQuestion)) {
            for (String not : notQuestion) {
                pinyin = toPinyin(not);
                if (condition.apply(not, pinyin)) {
                    step.setResult(INTERCEPT_ALL_QUESTION_NOT + not);
                    step.setPrompt(content);
                    String failureTalk = applicationInfo.getFailureTalk();
                    if (StringUtils.isBlank(failureTalk)) {
                        failureTalk = "抱歉，我们无法回答您的问题，您可以换个问题试试";
                    }
                    answerData.setAnswer(failureTalk);
                    answerData.setStatus(StepStatusEnum.ANSWER_COMPLETE);
                    checkSensitiveNum(ipAddr, not);
                    return false;
                }
            }
        }
        return passFlag;
    }

    /**
     * 通过雅意安全能力检测是否包含敏感词
     * @param content
     * @param contextList
     * @param answerData
     * @param applicationInfo
     * @param ipAddr
     */
    private boolean checkYayiSensitive(String content, Vector<StepEntity> contextList, AnswerStepData answerData, ApplicationInfo applicationInfo, String ipAddr) {
        boolean sensitive = false;
        String dangerWords = "";
        StepEntity stepEntity = new StepEntity();
        contextList.add(stepEntity);
        stepEntity.setStep(INTERCEPT_YAYI_SECRI);
        stepEntity.setPrompt(content);
        stepEntity.setResult(INTERCEPT_PASS);
        SecurityResult security = isSensitive(content, contextList);
        if (StringConstans.CODE.equals(security.getCode())) {
            SecurityResult.Data data = security.getData();
            if (null != data) {
                if (data.getIs_danger() == 1) {
                    dangerWords = data.getDanger_words();
                    // 指定的敏感等级不算敏感词
                    if (CollectionUtil.isNotEmpty(securityConfig.getSecurityLevel())) {
                        for (String level : securityConfig.getSecurityLevel()) {
                            if (StringUtils.isNotBlank(data.getDanger_words()) && data.getDanger_words().contains(level)) {
                                break;
                            }
                        }
                    }
                    sensitive = true;
                }
            }
        }
        answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
        if (sensitive) {
            answerData.setAnswer(applicationInfo.getFailureTalk());
            checkSensitiveNum(ipAddr, dangerWords);
            stepEntity.setResult(INTERCEPT_YAYI_SECRI_NOT + dangerWords);
            answerData.setStatus(StepStatusEnum.ANSWER_COMPLETE);
        }
        return sensitive;
    }
}
