package com.wenge.model.service.impl;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.alibaba.fastjson.JSONObject;
import com.wenge.model.constants.FileDataTypeConstants;
import com.wenge.model.dto.param.FileDataPageParam;
import com.wenge.model.entity.FileData;
import com.wenge.model.entity.KnowledgeData;
import com.wenge.model.mapper.es.FileDataMapper;
import com.wenge.model.service.DenseVectorService;
import com.wenge.model.service.FileDataService;
import com.wenge.oauth.entity.InterfaceCallLogRecording;
import com.wenge.oauth.entity.UmsOperation;
import com.wenge.oauth.holder.AppContextHolder;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.core.constant.enums.YesNoEnum;
import com.wg.appframe.core.dto.params.ListStringParam;
import com.wg.appframe.core.utils.DateUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.core.biz.EsPageInfo;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.ScriptScoreQueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.wenge.model.constants.AnswerStrategyContant.*;

@Service
public class FileDataServiceImpl implements FileDataService {

    @Autowired
    private FileDataMapper fileDataMapper;

    @Autowired
    private DenseVectorService denseVectorService;

    @Override
    public Result addFileData(FileData fileData) {
        String accountName = AppContextHolder.getAccountName();
        String format = LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DateUtil.DEFAULT_FORMAT);
        fileData.setUpdateTime(format);
        if (fileData.getType() == null) {
            // 设置类型为新建
            fileData.setType(FileDataTypeConstants.NEW);
        }
        fileData.setUpdateUser(accountName);
        if (StringUtils.isNotBlank(fileData.getId())) {
            denseVectorService.modelEncode(fileData.getContent(), fileData.getKnowledgeId(), fileData, CONTENT_DENSE_FILED);
            fileDataMapper.updateById(fileData);
        } else {
            fileData.setParaNum(0);
            fileData.setPageNum(0);
            fileData.setCreateTime(format);
            fileData.setCreateUser(accountName);
            denseVectorService.modelEncode(fileData.getContent(), fileData.getKnowledgeId(), fileData, CONTENT_DENSE_FILED);

            //fileData.setContentDense(knowledgeDataService.modelEncode(fileData.getContent()));
            fileDataMapper.insert(fileData);
        }
        return Result.success();
    }

    @Override
    public Result deleteFileData(ListStringParam fileDataId) {
        if (CollectionUtils.isNotEmpty(fileDataId.getParam())) {
            fileDataMapper.deleteBatchIds(fileDataId.getParam());
        }
        return Result.success();
    }

    @Override
    public Result getFileDatas(FileDataPageParam param) {
        LambdaEsQueryWrapper<FileData> wrapper = EsWrappers.lambdaQuery(FileData.class)
                .notSelect(FileData::getContentDense)
                .eq(StringUtils.isNotBlank(param.getFileId()), FileData::getFileId, param.getFileId())
                .eq(StringUtils.isNotBlank(param.getType()), FileData::getType, param.getType())
                .eq(StringUtils.isNotBlank(param.getKnowledgeId()), FileData::getKnowledgeId, param.getKnowledgeId())
                .in(CollectionUtils.isNotEmpty(param.getKnowledgeIds()), FileData::getKnowledgeId, param.getKnowledgeIds())
                .matchPhrasePrefixQuery(StringUtils.isNotBlank(param.getContent()), FileData::getContent, param.getContent());
        EsPageInfo<FileData> fileDataEsPageInfo = fileDataMapper.pageQuery(wrapper, param.getPageNo(), param.getPageSize());

        return Result.success(fileDataEsPageInfo);
    }

    @Override
    public List<FileData> getFileDataKnowledges(FileDataPageParam param) {
        LambdaEsQueryWrapper<FileData> wrapper = EsWrappers.lambdaQuery(FileData.class)
                .notSelect(FileData::getContentDense)
                .eq(StringUtils.isNotBlank(param.getFileId()), FileData::getFileId, param.getFileId())
                .eq(StringUtils.isNotBlank(param.getType()), FileData::getType, param.getType())
                .eq(StringUtils.isNotBlank(param.getKnowledgeId()), FileData::getKnowledgeId, param.getKnowledgeId())
                .in(CollectionUtils.isNotEmpty(param.getKnowledgeIds()), FileData::getKnowledgeId, param.getKnowledgeIds())
                // 合并标题和内容，当title不为空的时候，同时匹配标题和内容
                .and(StringUtils.isNotBlank(param.getContent()), w -> {
                    String content = param.getContent();
                    String[] keyword = content.split("\\,");
                    Set<String> keywordSet = Arrays.stream(keyword)
                        .map(String::trim)
                        .filter(StringUtils::isNotBlank)
                        .collect(Collectors.toSet());
                    w.or(ow -> {
                        keywordSet.forEach(word -> {
                            ow.should(w1 -> w1.matchPhrasePrefixQuery(FileData::getFileName, word));
                            ow.should(w2 -> w2.matchPhrasePrefixQuery(FileData::getContent, word));
                        });
                    });
                })
                .orderByDesc(FileData::getUpdateTime);;


        List<FileData> fileDataEsPageInfo = fileDataMapper.selectList(wrapper);
        return fileDataEsPageInfo;
    }

    @Override
    public Result<String> getDataByFileId(FileDataPageParam param) {
        LambdaEsQueryWrapper<FileData> wrapper = EsWrappers.lambdaQuery(FileData.class)
                .notSelect(FileData::getContentDense)
                .eq(StringUtils.isNotBlank(param.getFileId()), FileData::getFileId, param.getFileId())
                .matchPhrasePrefixQuery(StringUtils.isNotBlank(param.getContent()), FileData::getContent, param.getContent());
        List<FileData> fileData = fileDataMapper.selectList(wrapper);
        return Result.success(fileData.stream().map(FileData::getContent).collect(Collectors.joining()));
    }

    @Override
    public Result copyFileData(FileData fileData) {
        LambdaEsQueryWrapper<FileData> wrapper = EsWrappers.lambdaQuery(FileData.class)
                .notSelect(FileData::getContentDense)
                .eq(FileData::getId, fileData.getId());
        fileData = fileDataMapper.selectOne(wrapper);
        // 设置复制类型切片
        fileData.setType(FileDataTypeConstants.COPY);
        addFileData(fileData);
        return Result.success(fileData);
    }

    @Override
    public Result searchDataByKeywords(FileDataPageParam param) {
        if (StringUtils.isBlank(param.getKeywords())) {
            return Result.success(Lists.newArrayList());
        }
        FileDataPageParam fileDataPageParam = new FileDataPageParam();
        fileDataPageParam.setContent(param.getKeywords());
        fileDataPageParam.setFileId(param.getFileId());
        List<FileData> fileDataKnowledges = getFileDataKnowledges(fileDataPageParam);
        List<FileData> uniqueFileDataList = fileDataKnowledges.stream()
                .collect(Collectors.toMap(
                        FileData::getId,
                        Function.identity(),
                        (existing, replacement) -> existing, // 如果 id 重复，保留第一个
                        LinkedHashMap::new         // 保持原始顺序
                ))
                .values()                      // 获取去重后的对象集合
                .stream()
                .collect(Collectors.toList());
        return Result.success(uniqueFileDataList);
    }
}
