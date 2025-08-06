package com.wenge.model.service;


import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.NodeTableInfoParam;
import com.wenge.model.entity.ComponentNodeTableInfo;
import com.wg.appframe.core.bean.Result;

import java.util.List;

/**
 * Description: 数据库节点表数据服务类
 * @Author: antord
 * Version: 1.0
 * Create Date Time: 2025-05-19 14:11:33
 *
 */
public interface ComponentNodeTableInfoService extends IService<ComponentNodeTableInfo> {

    Result addComponentNodeTableInfo(ComponentNodeTableInfo componentNodeTableInfo);

    Result<Page<ComponentNodeTableInfo>> getComponentNodeTableInfoList(NodeTableInfoParam componentNodeTableInfo);

    /**
     * 根据业务id查询
     * @param tableId
     * @return
     */
    ComponentNodeTableInfo getByTableId(String tableId);

    Result deleteNodeTableInfo(List<String> idList);

    /**
     * 根据表名查询
     * @param tableName
     * @return
     */
    ComponentNodeTableInfo getByTableName(String tableName);

}