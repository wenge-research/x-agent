package com.wenge.model.service;

import com.mybatisflex.core.paginate.Page;
import com.wenge.model.dto.param.AiSearchRequest;
import com.wenge.model.dto.param.FilePageParam;
import com.wenge.model.dto.param.FileResult;
import com.wenge.model.entity.File;
import com.wg.appframe.core.bean.Result;

public interface AiSearchService {

    Result searchByWord(AiSearchRequest aiSearchRequest);

    Result searchByJgh(AiSearchRequest aiSearchRequest);
    
    Result queryKnowledgeList(AiSearchRequest aiSearchRequest);

    Result<Page<File>> getFileList(AiSearchRequest aiSearchRequest);

    Result<Page<FileResult>> getHotFileList(AiSearchRequest aiSearchRequest);

    Result<Page<FileResult>> getSubFileList(AiSearchRequest aiSearchRequest);

    Result<Page<FileResult>> getMySubFileList(AiSearchRequest aiSearchRequest);
}
