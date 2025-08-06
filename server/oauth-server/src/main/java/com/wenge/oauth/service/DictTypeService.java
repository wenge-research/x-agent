package com.wenge.oauth.service;

import com.mybatisflex.core.service.IService;
import com.wenge.oauth.entity.DictType;
import com.wg.appframe.core.bean.Result;

import java.util.List;

/**
 * Description: 字典类型服务类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-08-30 18:03:59
 *
 */
public interface DictTypeService extends IService<DictType> {

    Result addDictType(DictType dictType);

    Result getDictTypeList(DictType dictType);

    Result updateDictType(DictType dictType);

    Result deleteDictType(List<String> idList);

}