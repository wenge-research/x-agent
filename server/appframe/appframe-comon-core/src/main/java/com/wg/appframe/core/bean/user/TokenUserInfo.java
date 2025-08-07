/*
 * Copyright 2021-2021 Wenge Group Holding Ltd.
 */

package com.wg.appframe.core.bean.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * </p>
 * Token 携带的户相关信息
 *
 * @author yangyunjun
 * @since 2021-11-10
 */
@Data
public class TokenUserInfo implements Serializable {

    /**
     * 用户id
     */
    private Long userId;


    /**
     * 用户账号名称
     */
    private String username;

    /**
     * 用户中文名
     */
    private String cnName;

    /**
     * 应用clientId
     */
    private String clientId;

    /**
     * 手机号
     */
    private String phone;

    @ApiModelProperty("座机")
    private String calls;

    /**
     * 身份证
     */
    private String idNo;

    /**
     * 用户租户id
     */
    private Map<String, String> tenantId;

    /**
     * 用户角色
     */
    private List<OauthRole> roles;

    /**
     * 部门ID
     */
    private Integer deptId;

    /**
     * 部门CODE
     */
    private String deptCode;

    /**
     * 简道云userName
     */
    private String jdyUsername;

    /**
     * 尝试拿社区code,如果是社区以下的如玉翠网格1则返回玉翠社区。 如果是高于社区则直接返回
     */
    private String trySqDeptCode;

    public String getTrySqDeptCode() {
        if (deptCode!=null &&  this.deptCode.length() > 12) {
            return deptCode.substring(0, 12);
        }
        return deptCode;
    }

    public String getTrySpitDeptCode(String sqit) {
        if(deptCode.contains("_")){
            return deptCode.substring(0,deptCode.indexOf("_"));
        }
        return deptCode;
    }

    public String getCurrentTenantId() {
        Map<String, String> tenantIdMap = getTenantId();
        // 默认租户id为 0-无查看数据权限
        String tenantId = "0";
        if (tenantIdMap != null && tenantIdMap.size() > 0) {
            tenantId = tenantIdMap.get("currentTenant") != null ? tenantIdMap.get("currentTenant") : "0";
        }
        return tenantId;
    }


}
