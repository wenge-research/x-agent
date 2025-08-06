package com.wenge.model.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.AiSearchSourceFilePageParam;
import com.wenge.model.dto.result.AiSearchFileSizeCountResult;
import com.wenge.model.entity.AiSearchSourceFile;
import com.wg.appframe.core.bean.Result;


public interface AiSearchSourceFileService extends IService<AiSearchSourceFile> {

    Result<Page<AiSearchSourceFile>> getFileList(AiSearchSourceFilePageParam param);

    Result<AiSearchFileSizeCountResult> getFileSizeList();

}