package com.wenge.model.dto.result;

import com.wenge.model.entity.ApplicationInfo;
import com.wenge.model.workflow.entity.Component;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class WorkFlowDto {

    private Component component;

    private ApplicationInfo appInfo;
}
