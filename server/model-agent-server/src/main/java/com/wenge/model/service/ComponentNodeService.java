package com.wenge.model.service;

import com.wenge.model.workflow.entity.ComponentNode;
import com.wenge.model.workflow.entity.MetaParam;
import com.wenge.model.workflow.enums.ComponentNodeEnum;
import com.wenge.model.workflow.enums.DirectionEnum;
import com.wg.appframe.core.bean.Result;

import java.util.List;


public interface ComponentNodeService {


    /**
     * 保存节点
     * @param component
     * @return
     */
    Result<ComponentNode> saveNode(ComponentNode component);

    Result deleteNode(ComponentNode component);

    Result<List<ComponentNode>> queryByComponentNode(ComponentNode component);

    List<MetaParam> selectParamByComponentId(String componentId, ComponentNodeEnum nodeType, DirectionEnum direction);

    ComponentNode selectByNodeId(String nodeId);

    /**
     * 根据组件id查询节点
     * @param componentId
     * @return
     */
    List<ComponentNode> getNodeListByComponentId(String componentId);
}
