package com.wenge.model.controller;

import com.wenge.model.dto.param.LhmzParam;
import com.wenge.model.entity.*;
import com.wenge.model.service.BusinessCircleService;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;


/**
 * 龙华明治需求
 */
@RestController
@RequestMapping("/lhmz")
@Slf4j
@Api(tags = "商圈")
public class BusinessCircleController {

    @Autowired
    private BusinessCircleService businessCircleService;


    /**
     * 获取民治街道所有商圈信息
     */
    @GetMapping("/businessCircle/queryAll")
    public Result getAllBusinessCircles(LhmzParam lhmzParam) {


        return Result.success(businessCircleService.getAllBusinessCircles(lhmzParam));
    }

    /**
     * 获取民治街道产业空间的基本情况
     */
    @GetMapping("/industrialSpace/queryAll")
    public Result getAllIndustrialSpaces(LhmzParam lhmzParam) {
        return Result.success(businessCircleService.getAllIndustrialSpaces(lhmzParam));
    }


    /**
     * 获取民治街道产业空间的基本情况
     */
    @GetMapping("/industrialSpace2/queryAll")
    public Result getAllIndustrialSpaces2(LhmzParam lhmzParam) {
        return Result.success(businessCircleService.getAllIndustrialSpaces2(lhmzParam));
    }


    /**
     * 获取民治街道银行的基本情况
     */
    @GetMapping("/bankBranch/queryAll")
    public Result getAllBankBranch(LhmzParam lhmzParam) {
        return Result.success(businessCircleService.getAllBankBranchs(lhmzParam));
    }


    /**
     * 获取民治街道保障性住房项目情况
     */
    @GetMapping("/affordableHousingProject/queryAll")
    public Result getAllAffordableHousingProjects(LhmzParam lhmzParam) {
        return Result.success(businessCircleService.getAllAffordableHousingProjects(lhmzParam));
    }

    /**
     * 获取民治街道在售房地产项目统计表
     */
    @GetMapping("/realEstateSale/queryAll")
    public Result getAllRealEstateSales(LhmzParam lhmzParam) {
        return Result.success(businessCircleService.getAllRealEstateSales(lhmzParam));
    }


    /**
     * 获取民治街道大型综合体、商超数据
     */
    @GetMapping("/supermarketInfo/queryAll")
    public Result getAllSupermarketInfos(LhmzParam lhmzParam) {
        return Result.success(businessCircleService.getAllSupermarketInfos(lhmzParam));
    }

    /**
     * 获取民治街道政策汇编
     */
    @GetMapping("/policyRewardInfo/queryAll")
    public Result getAllPolicyRewardInfo(LhmzParam lhmzParam) {
        return Result.success(businessCircleService.getAllPolicyRewardInfo(lhmzParam));
    }

    /**
     * 获取民治街道政策汇编
     */
    @GetMapping("/policyRewardInfo/queryIndustryType")
    public Result getAllPolicyRewardInfo() {

        return Result.success(businessCircleService.queryIndustryType());
    }


    /**
     * 民治街道投资线索收集
     * @param investmentLead
     * @return
     */
    @PostMapping("/addInvestmentLead")
    public Result addInvestmentLead(@RequestBody InvestmentLead investmentLead) {
        businessCircleService.addInvestmentLead(investmentLead);
        return Result.success();
    }

    @PostMapping("/deleteInvestmentLead")
    public Result deleteInvestmentLead(@RequestBody List<String> idList) {
        businessCircleService.deleteInvestmentLead(idList);
        return Result.success();
    }

    /**
     * 查询全部咨询信息
     */
    @GetMapping("/investmentLead/getAllInvestmentLead")
    public Result getAllInvestmentLead(LhmzParam lhmzParam) {
        return Result.success(businessCircleService.getAllInvestmentLead(lhmzParam));
    }

    @PostMapping("/replaceR")
    public Result replaceR() {
        return businessCircleService.replaceR();
    }

    /**
     * 添加咨询留言
     * @param lhmzMessage
     * @return
     */
    @PostMapping("/addLhmzMessage")
    public Result addInvestmentLead(@RequestBody LhmzMessage lhmzMessage) {
        businessCircleService.addLhmzMessage(lhmzMessage);
        return Result.success();
    }

    @PostMapping("/deleteLhmzMessage")
    public Result deleteLhmzMessage(@RequestBody List<String> idList) {
        businessCircleService.deleteMessage(idList);
        return Result.success();
    }

    /**
     * 查询全部咨询信息
     */
    @GetMapping("/lhmzMessage/getAllLhmzMessage")
    public Result getAllLhmzMessage(LhmzParam lhmzParam) {
        return Result.success(businessCircleService.getAllLhmzMessage(lhmzParam));
    }

    /**
     * 查询应用业务数据类型
     * @return
     */
    @GetMapping("/getApplicationBusinessType")
    public Result getApplicationBusinessType(LhmzParam lhmzParam) {
        if (Objects.isNull(lhmzParam) || Objects.isNull(lhmzParam.getApplicationId())) {
            return Result.fail("应用id不能为空");
        }
        return Result.success(businessCircleService.getApplicationBusinessType(lhmzParam));
    }
}