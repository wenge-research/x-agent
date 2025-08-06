package com.wenge.model.controller;


import com.wenge.model.dto.param.HotSearchWordParam;
import com.wenge.model.service.HotSearchWordService;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/hotSearchWord")
@Slf4j
@Api(tags = "热门词搜索接口")
public class HotSearchWordController {

    @Autowired
    private HotSearchWordService hotSearchWordService;


    /**
     * 查询热门搜索词排名前N的数据
     */
    @ApiOperation(value = "查询热门搜索词排名前N的数据",tags = "查询热门搜索词排名前N的数据", notes = "查询热门搜索词排名前N的数据", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/queryHotSearchWordTopN")
    public Result queryHotSearchWord(@RequestBody HotSearchWordParam hotSearchWordParam) {
        return Result.success(hotSearchWordService.queryHotSearchWordTopN(hotSearchWordParam));
    }

    /**
     * 添加热门搜索
     */
    @ApiOperation(value = "添加热门搜索", tags = "添加热门搜索", notes = "添加热门搜索",
            response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/addHotSearchWord")
    public Result addHotSearchWord(@RequestBody HotSearchWordParam hotSearchWordParam) {
        if (StringUtils.isEmpty(hotSearchWordParam.getQuestion()) || StringUtils.isEmpty(hotSearchWordParam.getApplicationId())) {
            return Result.fail("参数不能为空");
        }
        return Result.success(hotSearchWordService.addHotSearchWord(hotSearchWordParam.getQuestion(),
                hotSearchWordParam.getApplicationId()));
    }


    /**
     * 订阅推荐列表
     */
    @ApiOperation(value = "订阅推荐列表",tags = "订阅推荐列表", notes = "订阅推荐列表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/subscribeList")
    public Result subscribeList(@RequestBody HotSearchWordParam hotSearchWordParam) {

        return Result.success();
    }

}
