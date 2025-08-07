package com.wenge.model.service;

import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.ImportInterceptWordDataParam;
import com.wenge.model.dto.param.InterceptWordPageParam;
import com.wenge.model.dto.param.LegalAndRegulatoryDataPageParam;
import com.wenge.model.dto.param.SyncLegalAndRegulatoryDataParam;
import com.wenge.model.entity.InterceptWord;
import com.wenge.model.entity.LegalAndRegulatoryData;
import com.wg.appframe.core.bean.Result;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Description: 法律法规库服务类
 *
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-09 21:15:57
 */
public interface LegalAndRegulatoryDataService {

    Result getLegalAndRegulatoryDataList(LegalAndRegulatoryDataPageParam legalAndRegulatoryDataPageParam);


    Result synchronousLegalAndRegulatoryData(SyncLegalAndRegulatoryDataParam syncLegalAndRegulatoryDataParam);

    boolean isExist(String title);

}