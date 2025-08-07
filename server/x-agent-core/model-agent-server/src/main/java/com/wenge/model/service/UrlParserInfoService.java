package com.wenge.model.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.*;
import com.wenge.model.entity.UrlParserInfo;
import com.wg.appframe.core.bean.Result;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Description: 文件服务类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-07 18:06:36
 *
 */
public interface UrlParserInfoService extends IService<UrlParserInfo> {

    Result add(UrlParserInfo info);

    Result<Page<UrlParserInfo>> getList(ParserInfoPageParam file);

    Result update(UrlParserInfo info);

    Result getDetail(UrlParserInfoDetailParam param) throws IOException;

    Result deleteBatch(ParserInfoDeleteParam param);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    UrlParserInfo getById(String id);

    List<UrlParserInfo> getParseredKnowledgeUrl();

    Result<String> importUrlParserInfoData(importUrlParserDataParam param);

    void downloadKnowledgeUrlTemp(HttpServletResponse response);

    void saveCollectPlatformKnowledgeContent(SaveCollectPlatformContentDataParam param);

}