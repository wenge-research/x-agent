package com.wenge.model.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.TableDirectoryDataChooseFieldParam;
import com.wenge.model.dto.param.TableDirectoryDataPageParam;
import com.wenge.model.dto.param.TableDirectoryInfoPageParam;
import com.wenge.model.entity.TableDirectoryInfo;
import com.wg.appframe.core.bean.Result;

/**
 * Description: 应用信息服务类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-03 19:02:51
 *
 */
public interface TableDirectoryInfoService extends IService<TableDirectoryInfo> {

    Result<Page<TableDirectoryInfo>> getInfoList(TableDirectoryInfoPageParam param);

    Result getDataList(TableDirectoryDataPageParam param);

    Result chooseField(TableDirectoryDataChooseFieldParam param);
}