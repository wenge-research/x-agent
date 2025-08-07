package com.wg.appframe.core.bean.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *  角色对象
 * </p>
 *
 * @author yangyunjun
 * @since 2022-04-18
 */
@Data
@ApiModel(value = "Role对象", description = "")
public class OauthRole implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("角色ID")
    private Long id;

    @ApiModelProperty("租户ID")
    private Long tenantId;
    
    @ApiModelProperty("角色代码")
    private String roleCode;

    @ApiModelProperty("角色名称")
    private String roleName;

    @ApiModelProperty("角色类型（1.平台角色 2.业务角色）")
    private Integer roleType;

    @ApiModelProperty("是否是超级管理员(1是， 0否)")
    private Integer superAdmin;

    @ApiModelProperty("父id")
    private Long pid;

}
