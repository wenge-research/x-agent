package com.wenge.model.controller;

import com.wenge.model.constants.LlmConstant;
import com.wenge.model.entity.ApplicationInfo;
import com.wenge.model.entity.LlmInfo;
import com.wenge.model.entity.StepEntity;
import com.wenge.model.service.DemoService;
import com.wg.appframe.core.bean.Result;
import com.wenge.model.service.impl.DialogueServiceImpl;
import com.wenge.model.strategy.llmStrategy.LlmStrategy;
import com.wg.appframe.core.config.CoreContextProvider;
import com.wg.appframe.core.bean.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    private DemoService demoService;

    /**
     * 用雅意大模型跑一下对话记录
     *
     * @return
     */
    @GetMapping("/runDialogYayi")
    public void runDialogYayi(HttpServletResponse response, String startDate) throws IOException {
        demoService.runDialogYayi(response, startDate);
    }

    @PostMapping("/echo")
    public Result echo(@RequestBody DemoParam param) throws IOException {
        return Result.success("测试成功：" + param.msg);
    }

    @GetMapping("/runDoubao")
    public void runDoubao() throws IOException {
        ApplicationContext context = CoreContextProvider.getContext();
        LlmStrategy bean = context.getBean(LlmConstant.DOUBAO, LlmStrategy.class);
        ApplicationInfo applicationInfo = new ApplicationInfo();
        applicationInfo.setLlmInfo(new LlmInfo());
        DialogueServiceImpl.APPLICATION_INFO.set(applicationInfo);
//        String result = bean.generate("你好,请问你是谁", null, new StepEntity(), false);
        String result = bean.generate("你好,请问你是谁", null, new StepEntity(), false, answerStepData -> System.out.println(answerStepData.getAnswer()));
        System.out.println(result);
    }

    public static class DemoParam {
        String msg;
    }
}
