package com.wenge.model.controller;

import cn.hutool.json.JSONObject;
import com.wenge.model.dto.param.*;
import com.wenge.model.dto.result.*;
import com.wenge.model.entity.DecisionJsonData;
import com.wenge.model.service.DecisionOneService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.EmptyParam;
import com.wg.appframe.core.dto.params.StringParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 推演服务，服务由中科院自动化所培杰提供
 */
@RestController
@RequestMapping("/decisionOne")
public class DecisionOneController {

    @Autowired
    private DecisionOneService decisionOneService;

    /**
     * 获取场景
     * @param param
     * @return
     */
    @PostMapping("/scenarios")
    public Result<List<DecisionScenariosResult>> scenarios(@RequestBody EmptyParam param) {
        return decisionOneService.scenarios(param);
    }

    /**
     * 连接推演服务
     * @param param
     * @return
     */
    @PostMapping("/connectDecision")
    public SseEmitter connectDecision(@RequestBody DecisionProcessParam param) {
        return decisionOneService.connectDecision(param);
    }

    /**
     * 初始化推演
     * @param param
     * @return
     */
    @PostMapping("/initDecision")
    public Result<DecisionInitResult> initDecision(@RequestBody StringParam param) {
        return decisionOneService.initDecision(param);
    }

    /**
     * 查询推演状态
     * @param param
     * @return
     */
    @PostMapping("/decisionStatus")
    public Result<DecisionStatusResult> decisionStatus(@RequestBody StringParam param) {
        return decisionOneService.decisionStatus(param);
    }

    /**
     * 导出推演结果
     *
     * @param param
     * @return
     */
    @PostMapping("/exportDecision")
    public void exportDecision(@RequestBody StringParam param, HttpServletResponse response) {
        decisionOneService.exportDecision(param, response);
    }

    /**
     * 运行推演
     * @param param
     * @return
     */
    @PostMapping("/runDecision")
    public Result<DecisionRunResult> runDecision(@RequestBody DecisionRunParam param) {
        return decisionOneService.runDecision(param);
    }

    /**
     * 获取推演计划
     * @param param
     * @return
     */
    @PostMapping("/plans")
    public Result<JSONObject> plans(@RequestBody DecisionPlansParam param) {
        return decisionOneService.plans(param);
    }

    /**
     * 获取推演智能体
     * @param param
     * @return
     */
    @PostMapping("/agents")
    public Result<List<JSONObject>> agents(@RequestBody StringParam param) {
        return decisionOneService.agents(param);
    }

    /**
     * 获取推演环境
     * @param param
     * @return
     */
    @PostMapping("/environment")
    public Result<JSONObject> environment(@RequestBody StringParam param) {
        return decisionOneService.environment(param);
    }

    /**
     * 保存推演 json 文件
     * @param param
     * @return
     */
    @PostMapping("/saveDecisionJson")
    public Result<String> saveJson(@RequestBody DecisionJsonSaveParam param) {
        return decisionOneService.saveJson(param);
    }

    /**
     * 获取推演 json 文件
     * @param param
     * @return
     */
    @PostMapping("/getDecisionJson")
    public Result<DecisionJsonData> getDecisionJson(@RequestBody StringParam param) {
        return decisionOneService.getDecisionJson(param);
    }

    /**
     * 获取智能体最新的交互动作信息
     * @param param
     * @return
     */
    @PostMapping("/interactions")
    public Result<JSONObject> interactions(@RequestBody StringParam param) {
        return decisionOneService.interactions(param);
    }

    /**
     * 获取智能体最新的交互内容
     * @param param
     * @return
     */
    @PostMapping("/actions")
    public Result<JSONObject> actions(@RequestBody StringParam param) {
        return decisionOneService.actions(param);
    }

    /**
     * 获取智能体决策
     *
     * @param param
     * @return
     */
    @PostMapping("/decisions")
    public Result<JSONObject> decisions(@RequestBody StringParam param) {
        return decisionOneService.decisions(param);
    }

    /**
     * 获取智能体决策报告
     * @param param
     * @return
     */
    @PostMapping("/decisionsReport")
    public SseEmitter decisionsReport(@RequestBody DecisionsReportParam param) {
        return decisionOneService.decisionsReport(param);
    }

    /**
     * 总结当前轮次
     * @param param
     * @return
     */
    @PostMapping("/decisionsSummary")
    public SseEmitter decisionsSummary(@RequestBody DecisionsReportParam param) {
        return decisionOneService.decisionsSummary(param);
    }


}
