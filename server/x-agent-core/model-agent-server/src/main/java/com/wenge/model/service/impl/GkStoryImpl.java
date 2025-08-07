package com.wenge.model.service.impl;

import com.wenge.model.dto.result.GkColumn;
import com.wenge.model.entity.GkStoryData;
import com.wenge.model.mapper.es.GkStoryMapper;
import com.wenge.model.service.GkStoryService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.EmptyParam;
import com.wg.appframe.core.dto.params.StringParam;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GkStoryImpl implements GkStoryService {


    @Autowired
    private GkStoryMapper gkStoryMapper;

    @Override
    public Result<List<GkColumn>> getColumnList(EmptyParam param) {

        LambdaEsQueryWrapper<GkStoryData> queryWrapper = EsWrappers.lambdaQuery(GkStoryData.class)
                .select(GkStoryData::getColumnId, GkStoryData::getColumnName)
                .matchAllQuery()
                .size(999);

        List<GkStoryData> gkStoryDataList = gkStoryMapper.selectList(queryWrapper);
        List<GkColumn> columnList = gkStoryDataList.stream().map(p -> p.getColumnId() + "--" + p.getColumnName()).distinct()
                .map(p -> {
                    GkColumn gkColumn = new GkColumn();
                    gkColumn.setColumnId(p.split("--")[0]);
                    gkColumn.setColumnName(p.split("--")[1]);
                    return gkColumn;
                })
                .collect(Collectors.toList());


        return Result.success(columnList);
    }

    @Override
    public Result<List<GkStoryData>> getStoryByColumnId(StringParam param) {
        if (StringUtils.isBlank(param.getParam())) {
            return Result.success(Lists.newArrayList());
        }

        LambdaEsQueryWrapper<GkStoryData> wrapper = EsWrappers.lambdaQuery(GkStoryData.class)
                .eq(GkStoryData::getColumnId, param.getParam())
                .size(999);

        List<GkStoryData> storyDataList = gkStoryMapper.selectList(wrapper);
        return Result.success(storyDataList);
    }

    @Override
    public Result<GkStoryData> getStoryById(StringParam param) {
        if (StringUtils.isBlank(param.getParam())) {
            return Result.success(new GkStoryData());
        }

        GkStoryData gkStoryData = gkStoryMapper.selectById(param.getParam());

        return Result.success(gkStoryData);
    }
}
