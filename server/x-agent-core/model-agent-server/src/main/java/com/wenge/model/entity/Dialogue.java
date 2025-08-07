package com.wenge.model.entity;

import lombok.Data;
import org.dromara.easyes.annotation.IndexField;
import org.dromara.easyes.annotation.IndexId;
import org.dromara.easyes.annotation.IndexName;
import org.dromara.easyes.annotation.Score;
import org.dromara.easyes.annotation.rely.FieldType;
import org.dromara.easyes.annotation.rely.IdType;

import java.io.Serializable;

@Data
@IndexName
public class Dialogue implements Serializable {

    private static final long serialVersionUID = 8901334677509447839L;

    /**
     * 数据id
     */
    @IndexId(type = IdType.UUID)
    private String id;

    /**
     * 对话id
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String dialogueId;

    /**
     * 原始问题
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String question;

    /**
     * 原始回答
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String answer;

    /**
     * 会话id
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String conversationId;

    /**
     * 应用id
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String applicationId;

    /**
     * 流水id
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String traceId;

    /**
     * 创建时间
     */
    @IndexField(fieldType = FieldType.DATE, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private String createTime;

    /**
     * 用户IP
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String ipAddress;

    /**
     * 用户姓名
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String userName;


    /**
     * 用户ID
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String userId;

    /**
     * 核实后的答案
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String verifyAnswer;

    /**
     * 核实状态，0-待核实；1-已核实修改，2-已核实正确，3-恶意问题，4-不处置，5-待重新核实
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String verifyStatus;

    /**
     * 核实人姓名
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String verifyUserName;

    /**
     * 核实人id
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String verifyUserId;

    /**
     * 反馈类型：1 赞；0 踩
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String feedbackType;

    /**
     * 页面是否公开 传值 true false
     */
    @IndexField(fieldType = FieldType.BOOLEAN)
    private Boolean basePublic;

    /**
     * 反馈内容
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String feedbackContent;

    /**
     * 反馈人id
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String feedbackUserId;

    /**
     * 反馈人姓名
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String feedbackUserName;

    /**
     * 反馈人部门ID
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String feedbackDeptId;

    /**
     * 反馈人部门名称
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String feedbackDeptName;

    /**
     * 反馈人 踩 推送数据到知识库的ID  字段为null没有推送 不为空表示推送
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String feedbackPullKnowledgeId;

    /**
     * 反馈人时间
     */
    @IndexField(fieldType = FieldType.DATE, dateFormat = "yyyy-MM-dd||yyyy-MM-dd HH:mm:ss||epoch_millis")
    private String feedbackTime;

    /**
     * 审核状态：0-待审核；1-审核通过；2-审核不通过，3-不处理
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String auditStatus;

    /**
     * 审核意见
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String auditComment;

    /**
     * 审核人id
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String auditUserId;

    /**
     * 审核人姓名
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String auditUserName;

    /**
     * 审核人时间
     */
    @IndexField(fieldType = FieldType.DATE, dateFormat = "yyyy-MM-dd||yyyy-MM-dd HH:mm:ss||epoch_millis")
    private String auditTime;

    /**
     * 核实部门id
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String verifyDeptId;

    /**
     * 核实部门名称
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String verifyDeptName;

    /**
     * 系统审核 记录比对的那条数据的dialogueId
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String verityDialogueId;

    @Score
    private Float score;

    /**
     *  客户端类型来源 PC APP
     **/
    @IndexField(fieldType = FieldType.KEYWORD)
    private String clientType;



    /**
     * 对话提交赞踩记录-客户端审核审核时间
     */
    @IndexField(fieldType = FieldType.DATE, dateFormat = "yyyy-MM-dd||yyyy-MM-dd HH:mm:ss||epoch_millis")
    private String feedbackAuditTime;

    /**
     * 对话提交赞踩记录-客户端审核审核人id
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String feedbackAuditUserId;

    /**
     * 对话提交赞踩记录-客户端审核审核人姓名
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String feedbackAuditUserName;

    /**
     * 对话提交赞踩记录-客户端审核 审核状态：0-赞同 1-不赞同
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String feedbackAuditStatus;

    /**
     * 对话提交赞踩记录-客户端审核 审核意见
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String feedbackAuditComment;

    /**
     * 职能划分部门名称
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String divisionDeptName;

    /**
     * 职能划分工作内容
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String divisionWork;

    /**
     * 职能划分时间
     */
    @IndexField(fieldType = FieldType.DATE, dateFormat = "yyyy-MM-dd||yyyy-MM-dd HH:mm:ss||epoch_millis")
    private String divisionTime;

    /**
     *  是否删除 0-未删除 1-已删除
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private Integer deleted = 0;

    /**
     * 是否无法回答：1 是；0 否
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String answerFlag;


    /**
     * 营业执照url 市监局
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String yyzhUrl;

    /**
     * 职能划分时间
     */
    @IndexField(fieldType = FieldType.DATE, dateFormat = "yyyy-MM-dd||yyyy-MM-dd HH:mm:ss||epoch_millis")
    private String yyzhUrlUploadTime;


    /**
     * 本次对话消耗的token数
     */
    @IndexField(fieldType = FieldType.INTEGER)
    private Integer consumeTokensTotal;

}
