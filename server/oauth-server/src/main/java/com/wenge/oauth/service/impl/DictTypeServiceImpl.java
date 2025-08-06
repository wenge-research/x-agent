package com.wenge.oauth.service.impl;


import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.oauth.entity.DictType;
import com.wenge.oauth.mapper.DictTypeMapper;
import com.wenge.oauth.service.DictTypeService;
import com.wg.appframe.core.bean.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description: 字典类型服务实现类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-08-30 18:03:59
 *
 */
@Service
@Slf4j
public class DictTypeServiceImpl extends ServiceImpl<DictTypeMapper, DictType> implements DictTypeService {
	/**
	 * 	字典类型数据库处理类
	 */
	@Autowired
	private DictTypeMapper dictTypeMapper;

	@Override
	public Result addDictType(DictType dictType){
		save(dictType);
		return Result.success();
	}

	@Override
	public Result getDictTypeList(DictType dictType){
		return Result.success(null);
	}

	@Override
	public Result updateDictType(DictType dictType){
		updateById(dictType);
		return Result.success();
	}

	@Override
	public Result deleteDictType(List<String> idList){
		removeByIds(idList);
		return Result.success();
	}
}