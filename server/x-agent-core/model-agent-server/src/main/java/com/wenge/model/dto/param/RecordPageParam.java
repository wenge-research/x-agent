package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class RecordPageParam extends WgPageInfo {

    private static final long serialVersionUID = 2633065896977765066L;

    /**
     * 开始时间
     */
    private String timeStart;

    /**
     * 结束时间
     */
    private String timeEnd;

    /**
     *  问题
     */
    private String question;

    /**
     * 回答
     */
    private String answer;

    /**
     * 应用id
     */
    private String applicationId;
    private List<String> applicationIds;

    /**
     * 部门id
     */
    private String deptId;

    /**
     * 来源id
     */
    private String userId;

    /**
     * 来源名称
     */
    private String userName;

    /**
     * 核实状态，0-待核实；1-已核实修改，2-已核实正确，3-恶意问题，4-不处置，5-待重新核实
     */
    private String verifyStatus;

    /**
     * 检索文本
     */
    private String text;

    /**
     * 核实部门id
     */
    private String verifyDeptId;

    /**
     * 对话id
     */
    private String conversationId;


    /**
     * 是否检索赞踩记录
     */
    private boolean likeFlag;

    /**
     * 检索不能回答的问题  true-是，false-否
     */
    private Boolean noAnswerFlag;

    /**
     * 反馈类型：1 赞；0 踩
     */
    private String feedbackType;

    /**
     * 用户类型 需要有模糊查询用户表ids 然后in到对话记录中
     */
    private String userType;

    /**
     * 是否删除，0-未删除，1-已删除
     */
    private Integer deleted;

    /**
     * 来源哪个tab页面：allocate-日志分拨
     */
    private String fromTab;

    /**
     * 是否只查看自己的记录 默认是查全部
     */
    private Integer isMyRecord = 0;
}
