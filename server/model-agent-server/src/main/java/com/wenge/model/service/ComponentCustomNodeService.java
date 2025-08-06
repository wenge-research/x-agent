package com.wenge.model.service;

import com.wenge.model.dto.param.workflow.ComponentCustomNodeParam;
import com.wenge.model.dto.result.workflow.ComponentCustomNodeResult;
import com.wenge.model.enums.WorkFlowNodeEnum;

import java.util.List;

public interface ComponentCustomNodeService {

    ComponentCustomNodeResult save(ComponentCustomNodeParam param);

    Boolean update(ComponentCustomNodeParam param);

    Boolean delete(Long id);

    ComponentCustomNodeResult getById(Long id);

    List<ComponentCustomNodeResult> getList(WorkFlowNodeEnum type);
}
