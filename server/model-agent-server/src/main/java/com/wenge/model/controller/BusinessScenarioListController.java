package com.wenge.model.controller;

import com.wenge.model.service.BusinessScenarioListService;
import com.wenge.model.service.VirtualHumanComponentInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author: caohaifeng
 * @date: 2024/10/24 11:06
 **/
@RestController
@RequestMapping("/businessScenarioList")
@Slf4j
public class BusinessScenarioListController {

    @Autowired
    private BusinessScenarioListService businessScenarioListService;
}