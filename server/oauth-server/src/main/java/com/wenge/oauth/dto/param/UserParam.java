package com.wenge.oauth.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserParam extends WgPageInfo {

    private static final long serialVersionUID = -6408959169114803535L;

    /**
     * 账号
     */
    private String accountName;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 部门id
     */
    private String deptId;

    /**
     * 开始时间
     */
    private String startDate;

    /**
     * 结束时间
     */
    private String endDate;

    /**
     * 用户状态 （0.未激活 1.正常 2.锁定 3.删除，4-注销）
     */
    private Integer status;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 用户类型，gov-政务用户，resident：微信公众号的普通居民，staff：工作人员
     */
    private String userType;

    /**
     * 查询条件
     */
    private String condition;

    /**
     * 工作职务
     */
    private String workPosition;

    /**
     * 工作状态，在职-on，离职-off，调离-transferred，休假-retired，病休-sick，退休-retire
     */
    private String workStatus;

    /**
     * 用户类型，前端根据页签写死，gov-政务用户，resident：微信公众号的普通居民，staff：工作人员
     */
    private String moduleType;

    /**
     * 用户状态是否有效标识 （0-有效，1-无效）
     */
    private String cancelFlag;

    /**
     * 普通用户是否可以查询当前租户下的所有用户标识
     */
    private Boolean queryCurrentTenantUserFlag = false;

}
