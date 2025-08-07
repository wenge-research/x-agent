package com.wenge.model.controller;

import com.wenge.model.dto.param.*;
import com.wenge.model.entity.Dialogue;
import com.wenge.model.service.DialogueRecordService;
import com.wenge.oauth.annotation.UmsOperationLog;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.dromara.easyes.core.biz.EsPageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/record")
@Slf4j
public class DialogueRecordController {

    @Autowired
    private DialogueRecordService dialogueRecordService;

    /**
     * 查询对话记录
     * @param param
     * @return
     */
    @ApiOperation(value = "查询对话记录",tags = "查询对话记录", notes = "查询对话记录", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/getRecord")
    @UmsOperationLog(description = "应用详情-查阅对话记录", logType = 1, belongModule = "appmanage", belongModuleName = "应用管理", objectType = "应用", objectName = "--", objectUuid = "--")
    public Result<EsPageInfo<Dialogue>> getRecord(@RequestBody RecordPageParam param) {
        return dialogueRecordService.getRecord(param);
    }

    @ApiOperation(value = "查阅最新会话的对话记录",tags = "查阅最新会话的对话记录", notes = "查阅最新会话的对话记录", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/getLastOneRecord")
    @UmsOperationLog(description = "查阅最新会话的对话记录", logType = 1, belongModule = "appmanage", belongModuleName = "应用管理", objectType = "应用", objectName = "--", objectUuid = "--")
    public Result<EsPageInfo<Dialogue>> getLastOneRecord(@RequestBody LastestRecordPageParam param) {
        return dialogueRecordService.getLastOneRecord(param);
    }

    /**
     * 导出对话记录
     *
     * @param param
     * @return
     */
    @PostMapping("/exportRecord")
    @UmsOperationLog(description = "应用详情-导出对话记录", logType = 5, belongModule = "appmanage", belongModuleName = "应用管理", objectType = "应用", objectName = "--", objectUuid = "--")
    public void exportRecord(@RequestBody RecordPageParam param, HttpServletResponse response) {
        dialogueRecordService.exportRecord(param, response);
    }

    /**
     * 反馈[点赞点踩]
     *
     * @param FeedbackParam
     * @return
     */
    @PostMapping(value = "/feedback")
    @ApiOperation(value = "反馈[点赞点踩]")
    @UmsOperationLog(description = "操作应用-点赞点踩", logType = 3, belongModule = "appmanage", belongModuleName = "应用管理", objectType = "应用", objectName = "--", objectUuid = "--")
    public Result feedback(@RequestBody FeedbackParam FeedbackParam) {
        return dialogueRecordService.feedback(FeedbackParam);
    }

    /**
     * 查询答案审核列表
     * @param param
     * @return
     */
    @PostMapping("/getCheckRecord")
    @UmsOperationLog(description = "应用详情-查阅答案审核", logType = 1, belongModule = "appmanage", belongModuleName = "应用管理", objectType = "应用", objectName = "--", objectUuid = "--")
    public Result<EsPageInfo<Dialogue>> getCheckRecord(@RequestBody RecordCheckParam param) {
        return dialogueRecordService.getCheckRecord(param);
    }

    /**
     * 核实对话记录
     * @param param
     * @return
     */
    @PostMapping("/verifyRecord")
    public Result verifyRecord(@RequestBody RecordVerifyParam param) {
        return dialogueRecordService.verifyRecord(param);
    }

    /**
     * 审核对话记录
     * @param param
     * @return
     */
    @PostMapping("/checkRecord")
    public Result checkRecord(@RequestBody RecordAuditParam param) {
        return dialogueRecordService.checkRecord(param);
    }

    /**
     * 逻辑删除对话日志，不影响后台查询的接口
     * @param param
     * @return
     */
    @PostMapping("/logicDelete")
    public Result logicDelete(@RequestBody RecordAuditParam param) {
        return dialogueRecordService.logicDelete(param);
    }


    /**
     * 审核对话记录点赞点踩
     * @param param
     * @return
     */
    @PostMapping("/checkFeedbackAuditRecord")
    public Result checkFeedbackAuditRecord(@RequestBody RecordFeedbackAuditParam param) {
        return dialogueRecordService.checkFeedbackAuditRecord(param);
    }



    /**
     * 上传的图片（营业执照地址）更新到对应的日志记录中
     * @param param
     * @return
     */
    @PostMapping("/updateYYZFPicUrl")
    public Result updateYYZFPicUrl(@RequestBody RecordVerifyParam param) {
        return dialogueRecordService.updateYYZFPicUrl(param);
    }

    /**
     * 获取对话记录数量
     *
     * @param param
     * @return
     */
    @PostMapping("/getCountByAppId")
    public Result getCountByAppId(@RequestBody RecordPageParam param) {
        return dialogueRecordService.getCountByAppId(param);
    }

    /**
     * 绑定审核通过推送的 qa 知识库
     * @param param
     * @return
     */
    @PostMapping("/bindKnn")
    public Result bindKnn(@RequestBody KnnBindParam param) {
        return dialogueRecordService.bindKnn(param);
    }

    /**
     * 获取绑定审核通过推送的状态
     * @param param
     * @return
     */
    @PostMapping("/getBindKnn")
    public Result<String> getBindKnn(@RequestBody KnnBindParam param) {
        return dialogueRecordService.getBindKnn(param);
    }

}
