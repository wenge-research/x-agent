package com.wenge.model.service;

import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.*;
import com.wenge.model.entity.ExcelParserInfo;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.wos.exception.WosException;

import java.io.IOException;

/**
 * Description: 文件服务类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-07 18:06:36
 *
 */
public interface ExcelParserInfoService extends IService<ExcelParserInfo> {

    Result addExcelParserInfo(AddExcelParserInfoParam param) throws IOException, WosException;

    Result runTask(RunTaskExcelParserInfoParam param) throws ClassNotFoundException;

    Result getDetail(String id);

    Result getExcelTableDataList(ExcelParserTablePageParam param);

    Result enable(EnableParam param);

    ExcelParserInfo getExcelParserInfo(String id);

    Result updateValidDate(UpdateStructDataValidDateParam param);
}