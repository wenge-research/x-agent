package com.wenge.oauth.service.impl;


import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.oauth.entity.DictItem;
import com.wenge.oauth.mapper.DictItemMapper;
import com.wenge.oauth.service.DictItemService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.StringParam;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.wenge.oauth.entity.table.DictItemTableDef.DICT_ITEM;

/**
 * Description: 字典条目服务实现类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-08-30 18:04:39
 *
 */
@Service
@Slf4j
public class DictItemServiceImpl extends ServiceImpl<DictItemMapper, DictItem> implements DictItemService {
	/**
	 * 	字典条目数据库处理类
	 */
	@Autowired
	private DictItemMapper dictItemMapper;

	@Override
	public Result addDictItem(DictItem dictItem){
		save(dictItem);
		return Result.success();
	}

	@Override
	public Result getDictItemList(DictItem dictItem){
		return Result.success(null);
	}

	@Override
	public Result updateDictItem(DictItem dictItem){
		updateById(dictItem);
		return Result.success();
	}

	@Override
	public Result deleteDictItem(List<String> idList){
		removeByIds(idList);
		return Result.success();
	}

	@Override
	public Result getDictItemByType(StringParam typeParam) {
		if (StringUtils.isBlank(typeParam.getParam())) {
			return Result.success();
		}

		List<DictItem> list = Wrappers.of(mapper)
				.where(DICT_ITEM.DICT_TYPE.eq(typeParam.getParam()))
				.orderBy(DICT_ITEM.ITEM_ORDER.asc())
				.list();

		Map<String, List<DictItem>> pidMap = list.stream().collect(Collectors.groupingBy(DictItem::getPid));
		list.forEach(item -> {
			item.setChildren(pidMap.get(item.getItemId()));
		});


		return Result.success(list);
	}

}