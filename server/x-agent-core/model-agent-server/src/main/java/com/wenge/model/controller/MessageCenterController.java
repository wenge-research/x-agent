package com.wenge.model.controller;

import com.mybatisflex.core.paginate.Page;
import com.wenge.model.dto.param.FileDataPageParam;
import com.wenge.model.dto.param.MessageCenterPageParam;
import com.wenge.model.entity.FileData;
import com.wenge.model.entity.MessageCenter;
import com.wenge.model.service.FileDataService;
import com.wenge.model.service.MessageCenterService;
import com.wenge.model.utils.PageInfo;
import com.wenge.oauth.annotation.OperaLogs;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.ListStringParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.dromara.easyes.core.biz.EsPageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/messageCenter")
@Slf4j
@Api("消息中心")
public class MessageCenterController {

    @Autowired
    private MessageCenterService messagesCenterService;

    /**
     * 新增消息
     */
    @ApiOperation(value = "新增消息",tags = "新增消息", notes = "新增消息", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/add")
    @OperaLogs
    public Result add(@RequestBody MessageCenter messageCenter) {
        return messagesCenterService.add(messageCenter);
    }


    /**
     * 删除文件切片
     */
    @ApiOperation(value = "删除消息",tags = "删除消息", notes = "删除消息", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/delete")
    @OperaLogs
    public Result delete(@RequestBody MessageCenter messageCenter) {
        return messagesCenterService.delete(messageCenter);
    }

    /**
     * 查询 分页
     */
    @ApiOperation(value = "查询消息 分页",tags = "查询消息 分页", notes = "查询消息 分页", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/getListPage")
    public Result<Page<MessageCenter>> getListPage(@RequestBody MessageCenterPageParam param) {
        return messagesCenterService.getListPage(param);
    }

    /**
     * 详情
     */
    @ApiOperation(value = "消息详情", tags = "消息详情", notes = "消息详情", response = Result.class, httpMethod = "GET", produces = "application/json", consumes = "application/json")
    @GetMapping("/getDataById")
    public Result<MessageCenter> getDataById(@RequestParam(value = "id", required = true) Long id) {
        return messagesCenterService.getDataById(id);
    }
}
