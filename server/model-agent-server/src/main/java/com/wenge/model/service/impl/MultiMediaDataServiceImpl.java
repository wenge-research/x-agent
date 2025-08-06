package com.wenge.model.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.wenge.model.dto.param.MultiMediaDataPageParam;
import com.wenge.model.entity.MultiMediaData;
import com.wenge.model.mapper.es.MultiMediaDataMapper;
import com.wenge.model.service.DenseVectorService;
import com.wenge.model.service.MultiMediaDataService;
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

import java.util.List;
import java.util.stream.Collectors;

import static com.wenge.model.constants.AnswerStrategyContant.CONTENT_DENSE_FILED;

@Service
public class MultiMediaDataServiceImpl implements MultiMediaDataService {

    @Autowired
    private MultiMediaDataMapper multiMediaDataMapper;

    @Autowired
    private DenseVectorService denseVectorService;

    @Override
    public Result addFileData(MultiMediaData mediaData) {
        String accountName = AppContextHolder.getAccountName();
        String format = LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DateUtil.DEFAULT_FORMAT);
        mediaData.setUpdateTime(format);
        mediaData.setUpdateUser(accountName);
        if (StringUtils.isNotBlank(mediaData.getId())) {
            denseVectorService.modelEncode(mediaData.getContent(), mediaData.getKnowledgeId(), mediaData, CONTENT_DENSE_FILED);

            //mediaData.setContentDense(knowledgeDataService.modelEncode(mediaData.getContent()));
            multiMediaDataMapper.updateById(mediaData);
        } else {
            mediaData.setParaNum(0);
            mediaData.setPageNum(0);
            mediaData.setCreateTime(format);
            mediaData.setCreateUser(accountName);
            denseVectorService.modelEncode(mediaData.getContent(), mediaData.getKnowledgeId(), mediaData, CONTENT_DENSE_FILED);
            multiMediaDataMapper.insert(mediaData);
        }
        return Result.success();
    }

    @Override
    public Result deleteFileData(ListStringParam fileDataId) {
        if (CollectionUtils.isNotEmpty(fileDataId.getParam())) {
            multiMediaDataMapper.deleteBatchIds(fileDataId.getParam());
        }
        return Result.success();
    }

    @Override
    public Result getFileDatas(MultiMediaDataPageParam param) {
        LambdaEsQueryWrapper<MultiMediaData> wrapper = EsWrappers.lambdaQuery(MultiMediaData.class)
                .notSelect(MultiMediaData::getContentDense, MultiMediaData::getContentDense1024)
                .eq(StringUtils.isNotBlank(param.getMultiMediaId()), MultiMediaData::getMultiMediaId, param.getMultiMediaId())
                .matchPhrasePrefixQuery(StringUtils.isNotBlank(param.getContent()), MultiMediaData::getContent, param.getContent());
        EsPageInfo<MultiMediaData> fileDataEsPageInfo = multiMediaDataMapper.pageQuery(wrapper, param.getPageNo(), param.getPageSize());
        return Result.success(fileDataEsPageInfo);
    }

    @Override
    public Result<String> getDataByFileId(MultiMediaDataPageParam param) {
        LambdaEsQueryWrapper<MultiMediaData> wrapper = EsWrappers.lambdaQuery(MultiMediaData.class)
                .notSelect(MultiMediaData::getContentDense)
                .eq(StringUtils.isNotBlank(param.getMultiMediaId()), MultiMediaData::getMultiMediaId, param.getMultiMediaId())
                .matchPhrasePrefixQuery(StringUtils.isNotBlank(param.getContent()), MultiMediaData::getContent, param.getContent());
        List<MultiMediaData> multiMediaData = multiMediaDataMapper.selectList(wrapper);
        return Result.success(multiMediaData.stream().map(MultiMediaData::getContent).collect(Collectors.joining()));
    }
}
