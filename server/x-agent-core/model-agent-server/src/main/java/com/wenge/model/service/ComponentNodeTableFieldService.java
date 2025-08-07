package com.wenge.model.service;


import cn.hutool.json.JSONObject;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.NodeTableDataAddParam;
import com.wenge.model.dto.param.NodeTableDataListParam;
import com.wenge.model.dto.param.NodeTableFieldAddParam;
import com.wenge.model.entity.ComponentNodeTableField;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.StringParam;

import java.util.List;

/**
 * Description: 数据库节点表字段数据服务类
 * @Author: antord
 * Version: 1.0
 * Create Date Time: 2025-05-19 14:47:59
 *
 */
public interface ComponentNodeTableFieldService extends IService<ComponentNodeTableField> {

    Result addComponentNodeTableField(NodeTableFieldAddParam param);

    Result<Page<JSONObject>> getComponentNodeTableFieldList(NodeTableDataListParam param);

    Result<List<ComponentNodeTableField>> getNodeTableFields(StringParam param);

    Result addNodeTableData(NodeTableDataAddParam param);

    /**
     * 根据表id删除字段
     * @param idList
     */
    void deleteFieldByTableId(List<String> idList);
}