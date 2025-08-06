package com.wenge.model.controller;

import com.wenge.model.dto.param.MatterDataParam;
import com.wenge.model.entity.MatterData;
import com.wenge.model.service.MatterDataService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.StringParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/matterData")
@Slf4j
public class MatterDataController {

    @Autowired
    private MatterDataService matterDataService;

    /**
     * 获取列表数据
     */
    @RequestMapping("/getMatterDataList")
    public Result getMatterDataList(@RequestBody MatterDataParam param) {
        return matterDataService.getMatterDataList(param);
    }

    /**
     * 获取事项详情
     */
    @RequestMapping("/getMatterDataDetail")
    public Result<MatterData> getMatterDataDetail(@RequestBody StringParam param) {
        return matterDataService.getMatterDataDetail(param);
    }

}
