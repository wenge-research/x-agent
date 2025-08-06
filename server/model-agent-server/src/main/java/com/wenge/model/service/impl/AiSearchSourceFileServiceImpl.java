package com.wenge.model.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.AiSearchSourceFilePageParam;
import com.wenge.model.dto.result.AiSearchFileSizeCountResult;
import com.wenge.model.entity.AiSearchSourceFile;
import com.wenge.model.mapper.AiSearchSourceFileMapper;
import com.wenge.model.service.AiSearchSourceFileService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.wenge.model.entity.table.AiSearchSourceFileTableDef.AI_SEARCH_SOURCE_FILE;

/**
 * Description: Ai智能搜索服务类
 *
 * @Author: ZHAISHUAI
 * Version: 1.0
 * Create Date Time: 2025-02-13 02:06:36
 */
@Service
@Slf4j
public class AiSearchSourceFileServiceImpl extends ServiceImpl<AiSearchSourceFileMapper, AiSearchSourceFile> implements AiSearchSourceFileService {



    @Override
    public Result<Page<AiSearchSourceFile>> getFileList(AiSearchSourceFilePageParam param) {
        Wrappers<Object> wrapper = Wrappers.init()
                .where(AI_SEARCH_SOURCE_FILE.FILE_NAME.like(param.getFileName()))
                .and(CollectionUtil.isNotEmpty(param.getFileTypes()), AI_SEARCH_SOURCE_FILE.FILE_TYPE.in(param.getFileTypes()))
                .and(param.getFileSize() != null, AI_SEARCH_SOURCE_FILE.FILE_SIZE.eq(param.getFileSize()))
                .and(StringUtils.isNotBlank(param.getCreateStartTime()), AI_SEARCH_SOURCE_FILE.CREATE_TIME.ge(param.getCreateStartTime()))
                .and(StringUtils.isNotBlank(param.getCreateEndTime()), AI_SEARCH_SOURCE_FILE.CREATE_TIME.le(param.getCreateEndTime()))
                .and(StringUtils.isNotBlank(param.getRecordStartTime()), AI_SEARCH_SOURCE_FILE.RECORD_TIME.ge(param.getRecordStartTime()))
                .and(StringUtils.isNotBlank(param.getRecordEndTime()), AI_SEARCH_SOURCE_FILE.RECORD_TIME.le(param.getRecordEndTime()))
                .and(param.getFileSize() != null, AI_SEARCH_SOURCE_FILE.FILE_SIZE.eq(param.getFileSize()))
                .and(param.getPicColorType() != null, AI_SEARCH_SOURCE_FILE.PIC_COLOR_TYPE.eq(param.getPicColorType()))
                .and(StringUtils.isNotBlank(param.getType()), AI_SEARCH_SOURCE_FILE.TYPE.eq(param.getType()))
                .orderBy(AI_SEARCH_SOURCE_FILE.CREATE_TIME.desc());
        Page<AiSearchSourceFile> page = page(Page.of(param.getPageNo(), param.getPageSize()), wrapper);

        return Result.success(page);
    }


    @Override
    public Result<AiSearchFileSizeCountResult> getFileSizeList() {
        AiSearchFileSizeCountResult result = new AiSearchFileSizeCountResult();
        Wrappers<Object> wrapper = Wrappers.init().and(AI_SEARCH_SOURCE_FILE.FILE_SIZE.isNotNull());
        List<AiSearchSourceFile> aiSearchSourceFiles = mapper.selectListByQuery(wrapper);
        if (CollectionUtil.isEmpty(aiSearchSourceFiles)) {
            result.setPictureTotalSize(0L);
            result.setAudioTotalSize(0L);
            result.setVideoTotalSize(0L);
            result.setDocumentTotalSize(0L);
            return Result.success(result);
        }

        long picCount = aiSearchSourceFiles.stream()
                .filter(file -> StringUtils.equals("png", file.getFileType()))
                .count();
        result.setPictureTotalSize(picCount);
        long audioCount = aiSearchSourceFiles.stream()
                .filter(file -> StringUtils.equals("audio", file.getFileType()))
                .count();
        result.setAudioTotalSize(audioCount);
        long videoCount = aiSearchSourceFiles.stream()
                .filter(file -> StringUtils.equals("video", file.getFileType()))
                .count();
        result.setVideoTotalSize(videoCount);
        List<String> documentTypeList = ListUtil.toList("docx", "pdf", "pptx", "xlsx", "txt");
        long documentCount = aiSearchSourceFiles.stream()
                .filter(file -> documentTypeList.contains(file.getFileType()))
                .count();
        result.setDocumentTotalSize(documentCount);
        return Result.success(result);
    }

}