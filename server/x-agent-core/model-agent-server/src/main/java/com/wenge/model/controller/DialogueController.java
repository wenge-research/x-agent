package com.wenge.model.controller;

import com.wenge.model.dto.param.*;
import com.wenge.model.dto.result.*;
import com.wenge.model.entity.DialogueStep;
import com.wenge.model.service.DialogueService;
import com.wenge.model.service.RecommendQuestionService;
import com.wenge.oauth.annotation.UmsOperationLog;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.redis.annotation.RedisLimit;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dialogue")
@Slf4j
public class DialogueController {

    @Autowired
    private DialogueService dialogueService;

    @Autowired
    private RecommendQuestionService recommendQuestionService;

    /**
     * 流式对话
     * 限流，10秒内最多访问60次
     *
     * @param param
     * @return
     */
    @PostMapping(value = "/dialogueByStream", produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    @ApiOperation(value = "[流式响应] 进行对话")
    @RedisLimit(period = 10, count = 50)
    @UmsOperationLog(description = "[流式响应] 进行对话", logType = 10, belongModule = "appmanage", belongModuleName = "应用对话", objectType = "应用", objectName = "--", objectUuid = "--")
    public SseEmitter dialogueByStream(@RequestBody DialogueByStreamParam param, HttpServletRequest request) {
        return dialogueService.dialogueByStream(param, request);
    }

    /**
     * 推荐问题
     *
     * @param param
     * @return
     */
    @PostMapping(value = "/recommendQuestion")
    @ApiOperation(value = "推荐问题")
    public Result<List<RecommendQuestionResult>> recommendQuestion(@RequestBody RecommendQuestionParam param) {
        return recommendQuestionService.recommendQuestion(param);
    }

    /**
     * 联想问题
     *associationNum
     * @param param
     * @return
     */
    @PostMapping(value = "/associationQuestion")
    @ApiOperation(value = "联想问题")
    public Result<List<AssociateQuestionResult>> associationQuestion(@RequestBody AssociateQuestionParam param) {
        return recommendQuestionService.associationQuestion(param);
    }

    /**
     * 查看答案依据来源
     *
     * @param sourceAnswerParam
     * @return
     */
    @PostMapping(value = "/sourceAnswer")
    @ApiOperation(value = "查看答案依据来源")
    public Result<SourceAnswerFinalResult> sourceAnswer(@RequestBody SourceAnswerParam sourceAnswerParam) {
        return recommendQuestionService.sourceAnswer(sourceAnswerParam);
    }

    /**
     * 文档对话
     * 限流，10秒内最多访问60次
     *
     * @param param
     * @return
     */
    @PostMapping(value = "/documentDialogue", produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    @ApiOperation(value = "[文档对话] 进行对话")
    @RedisLimit(period = 10, count = 10)
    public SseEmitter documentDialogue(@RequestBody DocumentDialogueParam param, HttpServletRequest request) throws IOException {
        return dialogueService.documentDialogue(param, request);
    }

    /**
     * 查看知识库分数详情
     *
     * @param scoreDataParam
     * @return
     */
    @PostMapping(value = "/getScoreData")
    @ApiOperation(value = "查看知识库分数详情")
    public Result<List<ScoreDataResult>> getScoreData(@RequestBody ScoreDataParam scoreDataParam) {
        return recommendQuestionService.getScoreData(scoreDataParam);
    }
    /**
     * 匹配招投标条件
     */
    @PostMapping(value = "/matching")
    @ApiOperation(value = "匹配招投标条件")
    public Result<Map<String, String>> matching(@RequestBody BiddingMatchingParam param) {
        return Result.success(dialogueService.matching(param));
    }

    /**
     * 查看问答全过程的步骤
     *
     * @param stepParam
     * @return
     */
    @PostMapping(value = "/getStepByDialogId")
    @ApiOperation(value = "查看问答全过程的步骤")
    public Result<List<DialogueStep>> getStepByDialogId(@RequestBody StepByDialogIdParam stepParam) {
        return recommendQuestionService.getStepByDialogId(stepParam);
    }

    /**
     * 翻译文本
     *
     * @param stepParam
     * @return
     */
    @PostMapping(value = "/translateText")
    @ApiOperation(value = "翻译文本")
    public Result<String> translateText(@RequestBody TranslateParam stepParam) {
        return dialogueService.translateText(stepParam);
    }

    /**
     * 查看改写问题
     * @return
     */
    @PostMapping("/getReviseQuestion")
    public Result<DialogueStep> getReviseQuestion(@RequestBody SourceAnswerParam sourceAnswerParam) {
        return recommendQuestionService.getReviseQuestion(sourceAnswerParam);
    }

    /**
     * 大学城智能问答政策帮h5专用-根据大学城系统的用户信息获取推荐问题
     * @return
     */
    @PostMapping("/getRecommendByUserInfo")
    public Result<PolicyRecommendResult> getRecommendByUserInfo(@RequestBody UserInfoParam param) {
        return recommendQuestionService.getRecommendByUserInfo(param);
    }

    /**
     * 检测是否存在敏感词
     *
     * @param param
     * @return
     */
    @PostMapping(value = "/checkSensitiveWord")
    public Result<String> checkSensitiveWord(@RequestBody SensitiveWordCheck param) {
        return dialogueService.checkSensitiveWord(param);
    }

    /**
     * 通过大模型生成 echarts 图
     *
     * @param param
     * @return
     */
    @PostMapping(value = "/genEChartsDataFromModel")
    public Result genEChartsDataFromModel(@RequestBody GenEchartsParam param) {
        return dialogueService.genEChartsDataFromModel(param);
    }

    /**
     * 对话内容总结
     * @param param
     * @return
     */
    @PostMapping(value = "/conversationSummary")
    public Result conversationSummary(@RequestBody BiddingMatchingParam param) {
        return dialogueService.conversationSummary(param);
    }

    /**
     * 根据“问题”导出指定对话日志里面的上下文里面的“汇总-重排”步骤信息
     * @param param
     * @return
     */
    @PostMapping(value = "/queryCollectRearangeStepInfo")
    public Result queryCollectRearangeStepInfo(DialogueCollectRerrangeStepParam param) {
        return dialogueService.queryCollectRearangeStepInfo(param);
    }



}
