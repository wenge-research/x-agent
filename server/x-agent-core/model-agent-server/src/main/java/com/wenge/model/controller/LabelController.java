package com.wenge.model.controller;


import com.alibaba.cloud.commons.lang.StringUtils;
import com.wenge.model.dto.param.LabelParam;
import com.wenge.model.entity.Label;
import com.wenge.model.service.LabelService;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@Api(tags = "标签")
@RestController
@RequestMapping("/label")
@Slf4j
public class LabelController {

    @Autowired
    private LabelService labelService;

    /**
     * 查询大模型信息表列表
     */
    @GetMapping("/getLabelList")
    public Result getLabelList(LabelParam label) {
        if (label == null || StringUtils.isEmpty(label.getLabelType())
                || StringUtils.isEmpty(label.getLabelName())) {
            return Result.success(Collections.emptyList());
        }
        return Result.success(labelService.getLabelsByWord(label));
    }
}
