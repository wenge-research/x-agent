package com.wenge.model.controller;

import com.wenge.model.dto.param.VirtualHumanComponentInfoPageParam;
import com.wenge.model.dto.param.VirtualHumanManageParam;
import com.wenge.model.entity.VirtualHumanComponentInfo;
import com.wenge.model.service.VirtualHumanComponentInfoService;
import com.wg.appframe.core.bean.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;


/**
 * @author DELL
 */
@RestController
@RequestMapping("/virtualHumanComponentInfo")
@Slf4j
public class VirtualHumanComponentInfoController {


    @Value("${xiaoice.subscription}")
    private String xiaoiceSubscription;

    @Autowired
    private VirtualHumanComponentInfoService virtualHumanComponentInfoService;

    @PostMapping("/getInfoList")
    public Result getInfoList(@RequestBody VirtualHumanComponentInfoPageParam param) {
        return virtualHumanComponentInfoService.getInfoList(param);
    }

    @PostMapping("/addInfo")
    public Result addInfo(@RequestBody VirtualHumanComponentInfo param) {
        return virtualHumanComponentInfoService.addInfo(param);
    }

    @DeleteMapping("/delInfo/{componentId}")
    public Result delInfo(@PathVariable(name = "componentId") String componentId) {
        return virtualHumanComponentInfoService.delInfo(componentId);
    }

	@PutMapping("/updateInfo")
	public Result updateInfo(@RequestBody VirtualHumanComponentInfo param) {
		return virtualHumanComponentInfoService.updateInfo(param);
	}

    @GetMapping("/getInfo/{componentId}")
    public Result getInfo(@PathVariable(name = "componentId") String componentId) {
        return virtualHumanComponentInfoService.getInfo(componentId);
    }


    //====================================对接小冰虚拟人=========================================


    /**
     * @description: 获取所有订阅的企业subscriptionList
     * @author: caohaifeng
     * @date: 2024/8/8 17:16
     **/
    @GetMapping("/getSubscriptionList")
    public Result getSubscriptionList() {
        return virtualHumanComponentInfoService.getSubscriptionList();
    }

    /**
     * @description: 生成小冰token
     * @author: caohaifeng
     * @date: 2024/8/8 17:16
     **/
    @GetMapping("/getAuthTokenBySubscription/{subscription}")
    public Result getAuthTokenBySubscription(@PathVariable(name = "subscription", required = true) String subscription) {
        return virtualHumanComponentInfoService.getAuthTokenBySubscription(subscription);
    }

    /**
     * @description: 生成小冰token
     * @author: caohaifeng
     * @date: 2024/8/8 17:16
     **/
    @GetMapping("/getAuthTokenBySubscriptionNew")
    public Result getAuthTokenBySubscriptionNew() {
        return virtualHumanComponentInfoService.getAuthTokenBySubscription(xiaoiceSubscription);
    }

    /**
     * @description: 销毁小冰token
     * @author: caohaifeng
     * @date: 2024/8/9 11:36
     **/
    @PostMapping("/destroyAuthTokenBySubscription")
    public Result destroyAuthTokenBySubscription(@RequestBody VirtualHumanManageParam virtualHumanManageParam) {
        return virtualHumanComponentInfoService.destroyAuthTokenBySubscription(virtualHumanManageParam);
    }


    /**
     * @description: 获取可用的数字人列表
     * @author: caohaifeng
     * @date: 2024/10/18 10:36
     **/
    @GetMapping("/getAvailableHumanList")
    public Result getAvailableHumanList() {
        return virtualHumanComponentInfoService.getAvailableHumanList();
    }






}