package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FeedbackParam extends WGParam {

    private static final long serialVersionUID = 666984177164752391L;

    /**
     * 应用id
     */
    private String applicationId;

    /**
     * 对话id
     */
    private String dialogueId;

    /**
     * 反馈类型：1 赞；0 踩
     */
    private String feedbackType;

    /**
     * 反馈内容
     */
    private String feedbackContent;

    /**
     * 反馈人id
     */
    private String feedbackUserId;

    /**
     * 反馈人姓名
     */
    private String feedbackUserName;

    /**
     * 反馈人时间
     */
    private String feedbackTime;


    /**
     * 问题
     */
    private String title;

    /**
     * 答案
     */
    private String content;

    /**
     * 是否公开 -默认不公开
     **/
    private boolean basePublic = false;


    /**
     *  关心助理 还是关芯客服的区分  前端传参
     *      主要针对点赞点踩需求中点踩推数据到哪个知识库做处理
     *
     *      guanxin-zhuli   guanxin-kefu
     **/
    private String applicationType = "guanxin-zhuli";

}
