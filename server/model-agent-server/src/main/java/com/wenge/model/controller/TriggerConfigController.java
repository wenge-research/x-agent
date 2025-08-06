package com.wenge.model.controller;

import com.mybatisflex.core.paginate.Page;
import com.wenge.model.dto.param.TriggerConfigParam;
import com.wenge.model.service.TriggerConfigService;
import com.wenge.model.workflow.entity.TriggerConfig;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tableDirectoryInfo")
@Slf4j
public class TriggerConfigController {

    @Autowired
    private TriggerConfigService triggerConfigService;

    /**
     * 新增或修改
     *
     * @param triggerConfig
     * @return
     */
    @PostMapping("/addOrUpdate")
    @ApiOperation(value = "新增或修改")
    public Result addOrUpdate(@RequestBody TriggerConfig triggerConfig) {
        return triggerConfigService.addOrUpdate(triggerConfig);
    }

    /**
     * 查询全部的触发器
     *
     * @param param
     * @return
     */
    @GetMapping("/queryAll")
    @ApiOperation(value = "查询触发器列表")
    public Result<Page<TriggerConfig>> queryAll(TriggerConfigParam param) {
        return triggerConfigService.queryAll(param);
    }

    /**
     * 查询全部的触发器
     *
     * @param param
     * @return
     */
    @GetMapping("/queryDetails")
    @ApiOperation(value = "查询触发器详情信息")
    public Result queryDetails(TriggerConfigParam param) {
        return triggerConfigService.queryDetails(param);
    }
}
