package com.wenge.model.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSONObject;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.workflow.ComponentCustomNodeParam;
import com.wenge.model.dto.result.workflow.ComponentCustomNodeResult;
import com.wenge.model.entity.ComponentCustomNode;
import com.wenge.model.enums.EnableEnum;
import com.wenge.model.enums.WorkFlowNodeEnum;
import com.wenge.model.mapper.ComponentCustomNodeMapper;
import com.wenge.model.service.ComponentCustomNodeService;
import com.wenge.model.workflow.util.JsonUtil;
import com.wenge.oauth.holder.ContextHolders;
import com.wg.appframe.core.config.CoreContextProvider;
import com.wg.appframe.core.constant.ResultCodeBase;
import com.wg.appframe.core.constant.enums.ResultCodeEnum;
import com.wg.appframe.core.exception.GlobalException;
import com.wg.appframe.core.exception.ParamException;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

import static com.wenge.model.entity.table.ComponentCustomNodeTableDef.COMPONENT_CUSTOM_NODE;

@Service
public class ComponentCustomNodeServiceImpl extends ServiceImpl<ComponentCustomNodeMapper, ComponentCustomNode> implements ComponentCustomNodeService {


    @Autowired
    private ComponentCustomNodeMapper componentCustomNodeMapper;

    /**
     * 新增自定义节点
     * @param param 参数body
     * @return 新增结果对象
     */
    @Override
    public ComponentCustomNodeResult save(ComponentCustomNodeParam param) {
        // 数据校验
        param.notBlank(param.getNodeName());

        // 用户账户名
        Long userId = ContextHolders.getTokenUserInfo().getId();

        // 校验节点名称是否重复
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(COMPONENT_CUSTOM_NODE.NODE_NAME.eq(param.getNodeName()))
                .and(COMPONENT_CUSTOM_NODE.IS_DELETE.eq(0))
                .and(COMPONENT_CUSTOM_NODE.CREATE_USER.eq(userId));
        long count = this.count(queryWrapper);
        if (count > 0) {
            throw new GlobalException("1002", param.getNodeName() +"已存在");
        }

        // 新增节点
        ComponentCustomNode customNode = ComponentCustomNode.builder()
                .componentId(IdUtil.simpleUUID())
                .nodeName(param.getNodeName())
                .nodeDesc(param.getNodeDesc())
                .icon(param.getIcon())
                .code(param.getCode())
                .codeLanguage(param.getCodeLanguage())
                .input(param.getInput())
                .output(param.getOutput())
                .startStatus(0)
                .createUser(userId)
                .updateUser(userId)
                .build();
        componentCustomNodeMapper.insertSelective(customNode);

        ComponentCustomNodeResult nodeResult = new ComponentCustomNodeResult();
        BeanUtil.copyProperties(customNode, nodeResult);
        return nodeResult;
    }


    /**
     * 更新自定义节点
     * @param param 参数body
     * @return 更新结果对象
     */
    @Override
    public Boolean update(ComponentCustomNodeParam param) {
        // 参数校验
       if (ObjectUtils.isEmpty(param.getId())) {
           throw new ParamException(ResultCodeEnum.PARAM_NOT_BLANK);
       }
        Long userId = ContextHolders.getTokenUserInfo().getId();

        // 转换对象
        ComponentCustomNode customNode = new ComponentCustomNode();
        BeanUtil.copyProperties(param, customNode);
        customNode.setUpdateUser(userId);

        // 更新节点
        componentCustomNodeMapper.insertOrUpdateSelective(customNode);
        return Boolean.TRUE;
    }

    /**
     * 删除自定义节点
     * @param id 主键id
     * @return 删除结果
     */
    @Override
    public Boolean delete(Long id) {
        if (null == id) {
            throw new GlobalException(ResultCodeEnum.PARAM_NOT_BLANK);
        }
        componentCustomNodeMapper.deleteById(id);
        return Boolean.TRUE;
    }


    /**
     * 查询单个节点
     * @param id 组件id
     * @return 响应对象
     */
    @Override
    public ComponentCustomNodeResult getById(Long id) {
        if (null == id) {
            throw new GlobalException(ResultCodeEnum.PARAM_NOT_BLANK);
        }
        // 转换对象
        return BeanUtil.copyProperties(componentCustomNodeMapper.selectOneById(id), ComponentCustomNodeResult.class);
    }


    /**
     * 查询列表
     * 当前登录用户只能查看自己创建的节点
     * @return 查询结果列表
     */
    @Override
    public List<ComponentCustomNodeResult> getList(WorkFlowNodeEnum type) {
        // 用户账号名
        Long userId = ContextHolders.getTokenUserInfo().getId();
        String userName = ContextHolders.getTokenUserInfo().getUserName();
        boolean adminFlag = !"超级管理员".equals(userName);
        boolean startFlag = WorkFlowNodeEnum.CUSTOM.equals(type);

        // 查询当前用户节点数据
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select()
                .from(COMPONENT_CUSTOM_NODE)
                .where(COMPONENT_CUSTOM_NODE.CREATE_USER.eq(userId).when(adminFlag))
                .and(COMPONENT_CUSTOM_NODE.START_STATUS.eq(0).when(startFlag))
                .orderBy(COMPONENT_CUSTOM_NODE.CREATE_TIME.desc());
        List<ComponentCustomNode> nodes = componentCustomNodeMapper.selectListByQuery(queryWrapper);
        List<ComponentCustomNodeResult> componentCustomNodeResults = BeanUtil.copyToList(nodes, ComponentCustomNodeResult.class);
        ApplicationContext context = CoreContextProvider.getContext();
        String url = context.getEnvironment().getProperty("workflow.default.codeApi");
        for (ComponentCustomNodeResult componentCustomNodeResult : componentCustomNodeResults) {
            JSONObject settings = new JSONObject();
            settings.put("url", url);
            settings.put("code", componentCustomNodeResult.getCode());
            settings.put("language", componentCustomNodeResult.getCodeLanguage());
            componentCustomNodeResult.setSettings(settings);

            List<JSONObject> output = new LinkedList<>();
            for (JSONObject jsonObject : componentCustomNodeResult.getOutput()) {
                jsonObject.put("label", jsonObject.get("label"));
                jsonObject.put("value", jsonObject.get("type"));
                jsonObject.put("valueType", jsonObject.get("type"));
                jsonObject.put("variable", jsonObject.get("label"));
                jsonObject.put("valueType", jsonObject.get("type"));
                jsonObject.put("desc", jsonObject.get("desc"));
                jsonObject.put("type", jsonObject.get("type"));
                jsonObject.put("maxLength", jsonObject.getOrDefault("maxLength", 200));
                jsonObject.put("required", jsonObject.getOrDefault("required", false));
                output.add(jsonObject);
            }
            componentCustomNodeResult.setOutput(output);
        }
        // 转换对象
        return componentCustomNodeResults;
    }

}
