package com.wenge.model.workflow.component.dto;

import com.wenge.model.entity.InterceptWordHouse;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class InterceptWordSetting implements Serializable {

    private static final long serialVersionUID = 5376748282447114737L;

    /**
     * 自定义拦截词配置
     */
    private List<InterceptWordHouse> configurations;

}
