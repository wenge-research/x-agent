package com.wenge.model.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.wenge.model.constants.FileDataTypeConstants;
import com.wenge.model.dto.param.ApiDataPageParam;
import com.wenge.model.entity.ApiData;

import com.wenge.model.entity.KnowledgeApi;
import com.wenge.model.mapper.es.ApiDataMapper;
import com.wenge.model.service.ApiDataService;
import com.wenge.model.service.DenseVectorService;
import com.wenge.oauth.holder.AppContextHolder;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.ListStringParam;
import com.wg.appframe.core.utils.DateUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.core.biz.EsPageInfo;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.wenge.model.constants.AnswerStrategyContant.CONTENT_DENSE_FILED;

@Service
public class ApiDataServiceImpl implements ApiDataService {


    @Autowired
    private DenseVectorService denseVectorService;

    @Autowired
    private ApiDataMapper apiDataMapper;


    @Override
    public Result addApiData(ApiData apiData) {
        String accountName = AppContextHolder.getAccountName();
        String format = LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DateUtil.DEFAULT_FORMAT);
        apiData.setUpdateTime(format);

        apiData.setUpdateUser(accountName);
        if (StringUtils.isNotBlank(apiData.getId())) {
            denseVectorService.modelEncode(apiData.getContent(), apiData.getKnowledgeId(), apiData, CONTENT_DENSE_FILED);
            apiDataMapper.updateById(apiData);
        } else {
            if (apiData.getType() == null) {
                // 设置类型为新建
                apiData.setType(FileDataTypeConstants.FILE);
            }
            apiData.setParaNum(0);
            apiData.setPageNum(0);
            apiData.setCreateTime(format);
            apiData.setCreateUser(accountName);
            denseVectorService.modelEncode(apiData.getContent(), apiData.getKnowledgeId(), apiData, CONTENT_DENSE_FILED);
            apiDataMapper.insert(apiData);
        }
        return Result.success();
    }

    @Override
    public Result deleteApiData(ListStringParam apiDataId) {
        if (CollectionUtils.isNotEmpty(apiDataId.getParam())) {
            apiDataMapper.deleteBatchIds(apiDataId.getParam());
        }
        return Result.success();
    }

    @Override
    public Result getApiDatas(ApiDataPageParam param) {
        LambdaEsQueryWrapper<ApiData> wrapper = EsWrappers.lambdaQuery(ApiData.class)
                .notSelect(ApiData::getContentDense)
                .eq(StringUtils.isNotBlank(param.getKnowledgeApiId()), ApiData::getKnowledgeApiId, param.getKnowledgeApiId())
                .eq(StringUtils.isNotBlank(param.getType()), ApiData::getType, param.getType())
                .matchPhrasePrefixQuery(StringUtils.isNotBlank(param.getContent()), ApiData::getContent, param.getContent());
        EsPageInfo<ApiData> fileDataEsPageInfo = apiDataMapper.pageQuery(wrapper, param.getPageNo(), param.getPageSize());
        return Result.success(fileDataEsPageInfo);
    }

    @Override
    public Result<String> getApiDataByApiId(ApiDataPageParam param) {
        LambdaEsQueryWrapper<ApiData> wrapper = EsWrappers.lambdaQuery(ApiData.class)
                .notSelect(ApiData::getContentDense)
                .eq(StringUtils.isNotBlank(param.getKnowledgeApiId()), ApiData::getKnowledgeApiId, param.getKnowledgeApiId())
                .matchPhrasePrefixQuery(StringUtils.isNotBlank(param.getContent()), ApiData::getContent, param.getContent());
        List<ApiData> fileData = apiDataMapper.selectList(wrapper);
        return Result.success(fileData.stream().map(ApiData::getContent).collect(Collectors.joining()));
    }

    @Override
    public List<ApiData> getApiDatasByApiId(ApiDataPageParam param) {
        LambdaEsQueryWrapper<ApiData> wrapper = EsWrappers.lambdaQuery(ApiData.class)
                .notSelect(ApiData::getContentDense)
                .eq(StringUtils.isNotBlank(param.getKnowledgeApiId()), ApiData::getKnowledgeApiId, param.getKnowledgeApiId())
                .matchPhrasePrefixQuery(StringUtils.isNotBlank(param.getContent()), ApiData::getContent, param.getContent());
        return apiDataMapper.selectList(wrapper);
    }

    @Override
    public Result copyApiData(ApiData apiData) {
        LambdaEsQueryWrapper<ApiData> wrapper = EsWrappers.lambdaQuery(ApiData.class)
                .notSelect(ApiData::getContentDense)
                .eq(ApiData::getId, apiData.getId());
        apiData = apiDataMapper.selectOne(wrapper);
        // 设置复制类型切片
        apiData.setType(FileDataTypeConstants.COPY);
        addApiData(apiData);
        return Result.success(apiData);
    }

    /**
     * 清空该文件的所有es数据
     */
    @Override
    public void clearEsData(KnowledgeApi knowledgeApi) {
        if (StringUtils.isBlank(knowledgeApi.getKnowledgeApiId())) {
            return;
        }
        List<String> deleteType = new ArrayList<>();
        deleteType.add(FileDataTypeConstants.FILE);
        deleteType.add(FileDataTypeConstants.COPY);
        LambdaEsQueryWrapper<ApiData> esQueryWrapper = EsWrappers.lambdaQuery(ApiData.class)
                .eq(ApiData::getKnowledgeApiId, knowledgeApi.getKnowledgeApiId())
                .in(ApiData::getType, deleteType)
                ;
        apiDataMapper.delete(esQueryWrapper);
    }

    @Override
    public List<ApiData> getApiDataByKnowledgeId(ApiDataPageParam param) {
        LambdaEsQueryWrapper<ApiData> wrapper = EsWrappers.lambdaQuery(ApiData.class)
                .notSelect(ApiData::getContentDense)
                .in(CollectionUtils.isNotEmpty(param.getKnowledgeIds()), ApiData::getKnowledgeId, param.getKnowledgeIds())
                .eq(StringUtils.isNotBlank(param.getType()), ApiData::getType, param.getType())
                .matchPhrasePrefixQuery(StringUtils.isNotBlank(param.getContent()), ApiData::getContent, param.getContent());
        return apiDataMapper.selectList(wrapper);
    }
}
