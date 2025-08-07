package com.wenge.model.controller;

import com.alibaba.fastjson2.JSONObject;
import com.wenge.model.dto.param.MatterInfoPageParam;
import com.wenge.model.dto.param.MatterInfoUpdateParam;
import com.wenge.model.service.MatterGuideInfoService;
import com.wenge.model.utils.PageInfo;
import com.wenge.model.vo.MatterGuideInfoPageVo;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;


@RestController
@RequestMapping("/matterGuideInfo")
@Slf4j
public class MatterGuideInfoController {

	@Autowired
	MatterGuideInfoService matterGuideInfoService;

	@PostMapping("/getList")
	public Result<PageInfo<JSONObject>> getList(@RequestBody MatterInfoPageParam param) {
		return matterGuideInfoService.getListNew(param);
	}

	@GetMapping("/getDetail/{infoId}")
	public Result getDetail(@PathVariable("infoId")String infoId) {
		return matterGuideInfoService.getDetail(infoId);
	}

	@GetMapping("/getDetailNew/{infoId}")
	public Result getDetailNew(@PathVariable("infoId")String infoId) {
		return matterGuideInfoService.getDetailNew(infoId);
	}

	@PostMapping("/update")
	public Result update(@RequestBody MatterInfoUpdateParam param) {
		return matterGuideInfoService.updateInfo(param);
	}

	@PostMapping("/export")
	public void export(@RequestBody MatterInfoPageParam param, HttpServletResponse response)  {
        try {
            matterGuideInfoService.exportNew(param,response);
        } catch (Exception e) {
            log.info("导出失败：{}",e.getMessage());
        }
    }



    /**
     * @description: 事项处置-根据事项id动态获取检索条件
     * @author: caohaifeng
     * @date: 2024/10/9 14:23
     **/
	@ApiOperation(value = "事项处置-根据事项id动态获取检索条件")
	@GetMapping("/getSearchFiledByMatterId")
	public Result getSearchFiledByMatterId(@RequestParam("matterId") String matterId) {
		return matterGuideInfoService.getSearchFiledByMatterId(matterId);
	}

	/**
	 * @description: 事项处置-根据事项id动态获取表头
	 * @author: caohaifeng
	 * @date: 2024/10/9 14:24
	 **/
	@ApiOperation(value = "事项处置-根据事项id动态获取表头")
	@GetMapping("/getTableHeadFiledByMatterId")
	public Result getTableHeadFiledByMatterId(@RequestParam("matterId") String matterId) {
		return matterGuideInfoService.getTableHeadFiledByMatterId(matterId);
	}


}