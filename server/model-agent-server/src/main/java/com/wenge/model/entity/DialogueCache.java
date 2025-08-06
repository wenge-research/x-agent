package com.wenge.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DialogueCache implements Serializable {

    private static final long serialVersionUID = 7010729784945401865L;

    // 核心对话信息
    private String id;
    private String dialogueId;
    private String question;
    private String answer;
    private String conversationId;
    private String applicationId;

    // 时间相关字段
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date feedbackTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date auditTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date divisionTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date yyzhUrlUploadTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date feedbackAuditTime;

    // 基础信息字段
    private String traceId;
    private String ipAddress;
    private String clientType;
    private String yyzhUrl;

    // 用户身份信息
    private String userName;
    private String userId;
    private String verifyUserId;
    private String verifyUserName;
    private String feedbackUserId;
    private String feedbackUserName;
    private String auditUserId;
    private String auditUserName;
    private String feedbackAuditUserId;
    private String feedbackAuditUserName;

    // 部门组织信息
    private String verifyDeptId;
    private String verifyDeptName;
    private String feedbackDeptId;
    private String feedbackDeptName;
    private String divisionDeptName;

    // 状态标识字段
    private String verifyStatus;
    private String answerFlag;
    private String auditStatus;
    private String feedbackAuditStatus;
    private Integer deleted;

    // 业务内容字段
    private String verifyAnswer;
    private String feedbackType;
    private String basePublic;
    private String feedbackContent;
    private String feedbackPullKnowledgeId;
    private String auditComment;
    private String feedbackAuditComment;
    private String divisionWork;
    private String verityDialogueId;

    // 数值型字段
    private Integer score;
    private Integer consumeTokensTotal;
}
