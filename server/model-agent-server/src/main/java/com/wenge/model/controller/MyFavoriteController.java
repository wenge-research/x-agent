package com.wenge.model.controller;

import com.mybatisflex.core.paginate.Page;
import com.wenge.model.dto.param.MyFavoriteQueryParam;
import com.wenge.model.entity.MyFavorite;
import com.wenge.model.service.MyFavoriteService;
import com.wg.appframe.core.bean.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/myFavorite")
@Slf4j
public class MyFavoriteController {


	@Autowired
	private MyFavoriteService myFavoriteService;


	/**
	 * 编辑
	 * @param param
	 * @return
	 */
	@PostMapping("/editMyFavorite")
	public Result editMyFavorite(@RequestBody MyFavoriteQueryParam param) {
		return myFavoriteService.editMyFavorite(param);
	}


	/**
	 * 根据应用id获取当前用户的收藏详情
	 *
	 * @param applicationId
	 * @return
	 */
	@GetMapping("/getMyFavoriteByApplicationId")
	public Result getMyFavoriteByApplicationId(@RequestParam(value = "applicationId", required = true) String applicationId) {
		return myFavoriteService.getMyFavoriteByApplicationId(applicationId);
	}

	/**
	 * 获取当前用户的收藏详情
	 *
	 * @return
	 */
	@PostMapping("/getAllMyFavorite")
	public Result<Page<MyFavorite>> getAllMyFavorite(@RequestBody MyFavoriteQueryParam param) {
		return myFavoriteService.getAllMyFavorite(param);
	}

}