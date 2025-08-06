package com.wenge.model.controller;

import com.mybatisflex.core.paginate.Page;
import com.wenge.model.dto.param.MatterGuideListParam;
import com.wenge.model.dto.param.MatterGuideParam;
import com.wenge.model.entity.MatterGuide;
import com.wenge.model.service.MatterGuideService;
import com.wenge.model.vo.MatterGuideListVo;
import com.wg.appframe.core.bean.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/matterGuide")
@Slf4j
public class MatterGuideController {


	@Autowired
	private MatterGuideService matterGuideService;


	@PostMapping("/getList")
	public Result<List<MatterGuideListVo>> getList(@RequestBody MatterGuideListParam param) {
		return matterGuideService.getList(param);
	}

	/**
	 * 查询有效的表单
	 * @param param
	 * @return
	 */
	@PostMapping("/getActiveList")
	public Result<List<MatterGuide>> getActiveList(@RequestBody MatterGuideListParam param) {
		return matterGuideService.getActiveList(param);
	}



	/**
	 * 添加事项表单
	 * @param matterGuide
	 * @return
	 */
	@PostMapping("/addMatter")
	public Result addMatter(@RequestBody MatterGuide matterGuide) {
		return matterGuideService.addMatter(matterGuide);
	}


	/**
	 * 编辑事项表单
	 * @param matterGuide
	 * @return
	 */
	@PostMapping("/editMatter")
	public Result editMatter(@RequestBody MatterGuide matterGuide) {
		return matterGuideService.editMatter(matterGuide);
	}


	/**
		 * 根据事项ID获取详情 包括字段 选项等
	 *
	 * @param id
	 * @return
	 */
	@GetMapping("/getMatterById")
	public Result getMatterById(@RequestParam(value = "id", required = true) Long id) {
		return matterGuideService.getMatterById(id);
	}


	/**
	 * 获取事项表单
	 * @param matterGuide
	 * @return
	 */
	@PostMapping("/getMatterList")
	public Result<Page<MatterGuide>> getMatterList(@RequestBody MatterGuideParam matterGuide) {
		return matterGuideService.getMatterList(matterGuide);
	}

	/**
	 * 删除事项表单
	 * @param guideParam
	 * @return
	 */
	@PostMapping("/deleteMatter")
	public Result deleteMatter(@RequestBody MatterGuideParam guideParam) {
		return matterGuideService.deleteMatter(guideParam);
	}

	/**
	 * 关闭-开启
	 * @param matterGuide
	 * @return
	 */
	@PostMapping("/updateShowFlag")
	public Result updateShowFlag(@RequestBody MatterGuide matterGuide) {
		return matterGuideService.updateShowFlag(matterGuide);
	}

}