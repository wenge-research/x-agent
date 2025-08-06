package com.wenge.model.service.impl;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson2.JSON;
import com.wenge.model.constants.AnswerStrategyContant;
import com.wenge.model.entity.DialogueStep;
import com.wenge.model.mapper.es.DialogueStepMapper;
import com.wenge.model.service.DemoService;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.yayi.api.YayiServer;
import com.wg.appframe.yayi.entity.YayiMessage;
import com.wg.appframe.yayi.param.DeepseekCompletionParam;
import com.wg.appframe.yayi.param.Generate30BParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.common.utils.CollectionUtils;
import org.dromara.easyes.core.biz.EsPageInfo;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class DemoServiceImpl implements DemoService {

    @Autowired
    private DialogueStepMapper dialogueStepMapper;

    @Autowired
    private YayiServer yayiServer;

    @Override
    public void runDialogYayi(HttpServletResponse response, String startDate) throws IOException {
        int pageNo = 1;
        int size = 10;
        EsPageInfo<DialogueStep> dialogueStepEsPageInfo;
        List<List<String>> dataList = Lists.newArrayList();
        dataList.add(ListUtil.toList("yayi-prompt", "雅意大模型回答", "deepSeek-prompt", "deepSeek大模型回答"));
        List<String> data = ListUtil.toList();
        String result;
        log.info("======>rerun yayi.pageNo:{}", pageNo);
        while (true) {
            try {
                log.info("======>rerun yayi.pageNo:{}", pageNo);
                LambdaEsQueryWrapper<DialogueStep> wrapper = EsWrappers.lambdaQuery(DialogueStep.class)
                        .eq(DialogueStep::getStep, AnswerStrategyContant.FINAL_COLLECT_ANSWER)
                        .ge(StringUtils.isNotBlank(startDate), "createTime.keyword", startDate);

                dialogueStepEsPageInfo = dialogueStepMapper.pageQuery(wrapper, pageNo, size);
                if (CollectionUtils.isEmpty(dialogueStepEsPageInfo.getList())) {
                    break;
                }
                List<DialogueStep> list = dialogueStepEsPageInfo.getList();
                for (DialogueStep dialogueStep : list) {
                    try {
                        log.info("======>rerun yayi.dialogueId:{}", dialogueStep.getDialogueId());
                        Object prompt = dialogueStep.getPrompt();
                        if (null == prompt || !JSONUtil.isTypeJSONObject(prompt.toString())) {
                            continue;
                        }
                        DeepseekCompletionParam deepseekCompletionParam = JSON.parseObject(prompt.toString(), DeepseekCompletionParam.class);
                        Generate30BParam param = new Generate30BParam();
                        List<YayiMessage> messages = deepseekCompletionParam.getMessages();
                        param.setMessages(messages);
                        result = yayiServer.generate30BStr("", param);
                        data = ListUtil.toList(JSON.toJSONString(param), result, dialogueStep.getPrompt().toString(), null == dialogueStep.getResult() ? StringConstant.BLANK : dialogueStep.getResult().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                        data = ListUtil.toList("大模型异常", e.getMessage());
                    }
                    dataList.add(data);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                log.info("======>rerun yayi.yayi.xlsx");
                ExcelWriter writer = ExcelUtil.getWriter("/u01/isi/agent/yayi.xlsx");
                writer.write(dataList, true);
                writer.flush();
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
                log.info("======>rerunee yayi.yayi.xlsx");
            }
            pageNo++;
        }
        //EasyExcelUtil.export(dataList, response.getOutputStream());
    }
}
