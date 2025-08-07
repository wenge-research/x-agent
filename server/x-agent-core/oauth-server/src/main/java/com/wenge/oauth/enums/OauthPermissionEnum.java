package com.wenge.oauth.enums;

import com.mybatisflex.core.query.QueryColumn;
import com.wenge.oauth.service.TablePermissionService;
import lombok.Getter;

import static com.wenge.oauth.entity.table.OauthDeptTableDef.OAUTH_DEPT;
import static com.wenge.oauth.entity.table.OauthUserTableDef.OAUTH_USER;
import static com.wenge.oauth.entity.table.RoleTableDef.ROLE;
import static com.wenge.oauth.entity.table.TenantTableDef.TENANT;

@Getter
public enum OauthPermissionEnum implements TablePermissionService {

    /**
     * 租户
     */
    TENANT_TABLE(TENANT.TENANT_ID, TENANT.CREATE_USER, TENANT.TENANT_ID, ""),

    /**
     * 用户
     */
    USER(OAUTH_USER.TENANT_ID, OAUTH_USER.CREATE_USER, OAUTH_USER.ID, ""),

    /**
     * 部门
     */
    DEPT(OAUTH_DEPT.TENANT_ID, OAUTH_DEPT.CREATE_USER, OAUTH_DEPT.DEPT_ID, ""),

    /**
     * 角色
     */
    ROLE_TABLE(ROLE.TENANT_ID, ROLE.CREATE_USER, ROLE.ROLE_ID, "");
    ;

    // 租户id，创建用户，数据id，数据类型
    private QueryColumn tenantId;
    private QueryColumn createUser;
    private QueryColumn publishAppStore;
    private QueryColumn dataId;
    private QueryColumn ownerType;
    private String dataType;

    OauthPermissionEnum(QueryColumn tenantId, QueryColumn createUser, QueryColumn dataId, String dataType) {
        this.tenantId = tenantId;
        this.createUser = createUser;
        this.publishAppStore = null;
        this.dataId = dataId;
        this.dataType = dataType;
    }
}
