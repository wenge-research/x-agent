package com.wenge.model.controller;

import com.wenge.oauth.dto.param.CacheClearParam;
import com.wenge.model.service.CacheClearService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.StringParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 清除本地缓存
 */
@RestController
@RequestMapping("/cacheClear")
@Slf4j
public class CacheClearController {

    @Autowired
    private CacheClearService cacheClearService;

    /**
     * 清除词库、场景缓存
     */
    @PostMapping("/cleanWord")
    public Result cleanWord() {
        return cacheClearService.cleanWord();
    }

    /**
     * 保存组件草稿
     * @return
     */
    @PostMapping("/clearFlowCache")
    public Result clearFlowCache(@RequestBody StringParam param){
        return cacheClearService.clearFlowCache(param);
    }

    /**
     * 清理向量化缓存
     *
     * @return
     */
    @PostMapping("/clearDenseVector")
    public Result clearDenseVector() {
        return cacheClearService.clearDenseVector();
    }

    /**
     * 清理应用配置缓存
     * @return
     */
    @PostMapping("/clearAppConfig")
    public Result clearAppConfig(@RequestBody CacheClearParam param) {
        return cacheClearService.clearAppConfig(param);
    }

}
