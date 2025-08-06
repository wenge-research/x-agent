package com.wenge.model.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.mapper.ComponentNodeMapper;
import com.wenge.model.mapper.MetaParamMapper;
import com.wenge.model.service.ComponentNodeService;
import com.wenge.model.workflow.entity.ComponentNode;
import com.wenge.model.workflow.entity.MetaParam;
import com.wenge.model.workflow.enums.ComponentNodeEnum;
import com.wenge.model.workflow.enums.DirectionEnum;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.wenge.model.workflow.entity.table.ComponentNodeTableDef.COMPONENT_NODE;
import static com.wenge.model.workflow.entity.table.MetaParamTableDef.META_PARAM;

@Service
public class ComponentNodeImpl extends ServiceImpl<ComponentNodeMapper, ComponentNode> implements ComponentNodeService {

    @Autowired
    private MetaParamMapper metaParamMapper;

    @Override
    public Result<ComponentNode> saveNode(ComponentNode componentNode) {
        if (StrUtil.isBlank(componentNode.getComponentId())) {
            throw new RuntimeException("组件ID不能为空");
        }
        if (StrUtil.isBlank(componentNode.getNodeName())) {
            throw new RuntimeException("节点名称不能为空");
        }
        if (StrUtil.isBlank(componentNode.getNodeId())) {
            componentNode.setNodeId(IdUtil.simpleUUID());
        }
        saveOrUpdate(componentNode);
        return Result.success(componentNode);
    }

    @Override
    public Result deleteNode(ComponentNode component) {
        removeById(component.getId());
        return Result.success();
    }

    @Override
    public Result<List<ComponentNode>> queryByComponentNode(ComponentNode node) {
        Wrappers<ComponentNode> where = Wrappers.of(mapper)
                .where(StrUtil.isNotBlank(node.getComponentId()), COMPONENT_NODE.COMPONENT_ID.eq(node.getComponentId()))
                .and(StrUtil.isNotBlank(node.getNodeName()), COMPONENT_NODE.NODE_NAME.eq(node.getNodeName()))
                .and(StrUtil.isNotBlank(node.getNodeId()), COMPONENT_NODE.NODE_ID.eq(node.getNodeId()))
                .and(node.getNodeType() != null, COMPONENT_NODE.NODE_TYPE.eq(node.getNodeType()));
        return Result.success(list(where));
    }

    @Override
    public List<MetaParam> selectParamByComponentId(String componentId, ComponentNodeEnum nodeType, DirectionEnum direction) {
        List<MetaParam> result = Wrappers.of(metaParamMapper)
                .select(META_PARAM.ALL_COLUMNS)
                .from(COMPONENT_NODE)
                .leftJoin(META_PARAM)
                .on(COMPONENT_NODE.NODE_ID.eq(META_PARAM.NODE_ID))
                .where(COMPONENT_NODE.COMPONENT_ID.eq(componentId))
                .and(COMPONENT_NODE.NODE_TYPE.eq(nodeType))
                .and(META_PARAM.DIRECTION.eq(direction))
                .list();
        return result;
    }

    @Override
    public ComponentNode selectByNodeId(String nodeId) {
        return Wrappers.of(mapper)
                .where(COMPONENT_NODE.NODE_ID.eq(nodeId)).list().get(0);
    }

    @Override
    public List<ComponentNode> getNodeListByComponentId(String componentId) {
        if (StringUtils.isBlank(componentId)) {
            return Lists.newArrayList();
        }
        return Wrappers.of(mapper)
                .where(COMPONENT_NODE.COMPONENT_ID.eq(componentId))
                .list();
    }
}
