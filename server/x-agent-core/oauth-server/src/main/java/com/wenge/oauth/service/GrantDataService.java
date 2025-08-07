package com.wenge.oauth.service;

import com.mybatisflex.core.service.IService;
import com.wenge.oauth.dto.param.GrantDataGetParam;
import com.wenge.oauth.dto.param.GrantDataParam;
import com.wenge.oauth.entity.GrantData;
import com.wg.appframe.core.bean.Result;

import java.util.List;

/**
 * Description: 授权数据服务类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-08-08 19:59:43
 *
 */
public interface GrantDataService extends IService<GrantData> {

    Result addGrantData(GrantDataParam param);

    Result getGrantDataList(GrantDataGetParam param);

    Result updateGrantData(GrantData grantData);

    Result deleteGrantData(List<String> idList);

}