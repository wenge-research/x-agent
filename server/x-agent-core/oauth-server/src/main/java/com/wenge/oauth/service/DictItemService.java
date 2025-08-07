package com.wenge.oauth.service;

import com.mybatisflex.core.service.IService;
import com.wenge.oauth.entity.DictItem;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.StringParam;

import java.util.List;

/**
 * Description: 字典条目服务类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-08-30 18:04:39
 *
 */
public interface DictItemService extends IService<DictItem> {

    Result addDictItem(DictItem dictItem);

    Result getDictItemList(DictItem dictItem);

    Result updateDictItem(DictItem dictItem);

    Result deleteDictItem(List<String> idList);

    Result getDictItemByType(StringParam typeParam);
}