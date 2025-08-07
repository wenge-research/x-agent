package com.wenge.model.mapper;

import com.mybatisflex.core.BaseMapper;
import com.wenge.model.workflow.entity.WorkflowNodeExecution;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WorkflowNodeExecutionMapper extends BaseMapper<WorkflowNodeExecution> {
}
