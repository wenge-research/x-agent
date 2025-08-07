package com.wenge.model.controller.workflow;

import com.mybatisflex.core.paginate.Page;
import com.wenge.model.dto.param.MemoryParam;
import com.wenge.model.service.MemoryService;
import com.wenge.model.workflow.entity.Memory;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 长期记忆数据
 */
@Api(tags = "长期记忆管理")
@RestController
@RequestMapping("/memory")
@Slf4j
public class MemoryController {

    @Autowired
    private MemoryService memoryService;


    /**
     * 手动更新记忆内容
     * @param memory
     * @return
     */
    @PostMapping("/update")
    @ApiOperation(value = "更新记忆内容")
    public Result<Memory> update(@RequestBody Memory memory) {
        return Result.success(memoryService.update(memory));
    }


    /**
     * 查询记忆列表
     * @param memory
     * @return
     */
    @PostMapping("/page")
    @ApiOperation(value = "更新记忆内容")
    public Result<Page<Memory>> page(@RequestBody MemoryParam memory) {
        return Result.success(memoryService.page(memory));
    }


}
