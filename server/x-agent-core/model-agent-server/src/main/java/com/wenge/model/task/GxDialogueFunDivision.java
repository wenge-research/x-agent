package com.wenge.model.task;

import cn.hutool.core.net.NetUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.wenge.model.constants.RedisKey;
import com.wenge.model.entity.Dialogue;
import com.wenge.model.mapper.es.DialogueMapper;
import com.wenge.model.utils.AnswerUtils;
import com.wenge.oauth.constants.AppConfigContant;
import com.wenge.oauth.constants.RedisConstant;
import com.wg.appframe.core.utils.DateUtil;
import com.wg.appframe.redis.service.RedisService;
import com.wg.appframe.yayi.api.YayiServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.core.biz.SAPageInfo;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 关芯客服对话功能划分
 */
@RestController
@RequestMapping("/agentTask/document")
@Slf4j
public class GxDialogueFunDivision {

    @Value("${task.funDivision.enable}")
    private boolean enable;

    @Autowired
    private DialogueMapper dialogueMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private YayiServer yayiServer;

    @PostConstruct
    public void init() {

        String value = redisService.get(RedisKey.DIALOG_DIVISION, String.class);
        String localhostStr = NetUtil.getLocalhostStr();
        if (StringUtils.isNotBlank(value) && value.startsWith(localhostStr)) {
            redisService.del(RedisKey.DIALOG_DIVISION);
            redisService.unlock(RedisConstant.LOCK_DIVISION);
        }
    }

    /**
     * 关芯客服对话功能划分
     */
    @RequestMapping("/funDivision")
    // @Scheduled(cron = "${task.document.cron}")
    public void funDivision() {
        if (!enable) {
            return;
        }
        String redisKey = RedisKey.DIALOG_DIVISION;
        if (redisService.hasKey(redisKey)) {
            log.info("定时器正在执行，本次跳过");
            return;
        }

        boolean lock = redisService.lock(RedisConstant.LOCK_DIVISION);
        if (!lock) {
            log.info("==>任务未获取到锁，跳过当前任务");
            return;
        }

        String localhostStr = NetUtil.getLocalhostStr();
        String currentDateString = DateUtil.getCurrentDateString();
        redisService.set(redisKey, localhostStr + "_" + currentDateString, 60 * 60 * 24 * 7);

        try {
            String appId = AppConfigContant.getConfiguration(AppConfigContant.FUN_DIVISION_APPID);
            String funDivisionPrompt = AppConfigContant.getConfiguration(AppConfigContant.FUN_DIVISION_PROMPT);
            if (StringUtils.isBlank(appId) || StringUtils.isBlank(funDivisionPrompt)) {
                log.error("appId或funDivisionPrompt为空");
                return;
            }
            String currentTime = com.wenge.model.utils.DateUtil.getCurrentTime();

            int size = 50;
            LambdaEsQueryWrapper<Dialogue> wrapper = EsWrappers.lambdaQuery(Dialogue.class)
                    .eq(Dialogue::getApplicationId, appId)
                    .not(not -> not.exists(Dialogue::getDivisionDeptName))
                    .orderByAsc(Dialogue::getId)
                    .size(size);
            SAPageInfo<Dialogue> dialogueSAPageInfo = dialogueMapper.searchAfterPage(wrapper, null, size);
            List<Object> nextSearchAfter = Lists.newArrayList();
            while (true) {
                nextSearchAfter = dialogueSAPageInfo.getNextSearchAfter();
                if (CollectionUtils.isEmpty(dialogueSAPageInfo.getList())) {
                    break;
                }
                dialogueSAPageInfo.getList().forEach(dialogue -> {
                    try {
                        // 对话功能划分
                        //doubaoCompletionStrategy.run(dialogue.getContent(), new DoubaoCompletionParam());
                        String prompt = funDivisionPrompt.replace("{{question}}", dialogue.getQuestion());
                        String generated = yayiServer.generate30BStr(prompt);
                        if (StringUtils.isBlank(generated)) {
                            return;
                        }
                        generated = AnswerUtils.dealLLmJson(generated);

                        if (JSONUtil.isTypeJSONObject(generated)) {
                            JSONObject entries = JSONUtil.parseObj(generated);
                            String divisionDeptName = entries.getStr("部门");
                            String divisionWork = entries.getStr("工作内容");
                            dialogue.setDivisionWork(divisionWork);
                            dialogue.setDivisionDeptName(divisionDeptName);
                            dialogue.setDivisionTime(currentTime);
                            dialogueMapper.updateById(dialogue);
                        }
                    }catch (Exception e){
                        log.error("对话功能划分异常", e);
                    }
                });
                dialogueSAPageInfo = dialogueMapper.searchAfterPage(wrapper, nextSearchAfter, size);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            redisService.del(redisKey);
            redisService.unlock(RedisConstant.LOCK_DIVISION);
        }
    }
}
