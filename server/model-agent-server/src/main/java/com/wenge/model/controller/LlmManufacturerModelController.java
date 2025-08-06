package com.wenge.model.controller;

import com.wenge.model.dto.param.LlmManufacturerModelParam;
import com.wenge.model.service.LlmManufacturerModelService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.StringParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/llmManufacturerModel")
@Slf4j
public class LlmManufacturerModelController {

    @Autowired
    private LlmManufacturerModelService llmManufacturerModelService;

    /**
     * 查询大模型厂商列表
     */
    @GetMapping("/getLlmManufacturer")
    public Result getLlmManufacturer() {
        return llmManufacturerModelService.getLlmManufacturer();
    }

    /**
     * 查询厂商下模型列表
     */
    @GetMapping("/getLlmManufacturerModelList")
    public Result getLlmManufacturerModelList(StringParam param) {
        return llmManufacturerModelService.getLlmManufacturerModelList(param);
    }

    /**
     * 查询模型信息
     */
    @GetMapping("/getLlmManufacturerModel")
    public Result getLlmManufacturerModel(LlmManufacturerModelParam param) {
        return llmManufacturerModelService.getLlmManufacturerModel(param);
    }
}
