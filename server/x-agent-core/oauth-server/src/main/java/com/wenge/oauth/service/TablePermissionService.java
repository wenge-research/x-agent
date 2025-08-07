package com.wenge.oauth.service;

import com.mybatisflex.core.query.QueryColumn;

/**
 * 授权数据
 */
public interface TablePermissionService {

    QueryColumn getTenantId();

    QueryColumn getPublishAppStore();

    QueryColumn getCreateUser();

    QueryColumn getDataId();

    QueryColumn getOwnerType();

    String getDataType();

}
