package com.wenge.model.service.impl;

import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.LlmManufacturerModelParam;
import com.wenge.model.dto.result.LlmManufacturerResult;
import com.wenge.model.entity.LlmManufacturerModel;
import com.wenge.model.mapper.LlmManufacturerModelMapper;
import com.wenge.model.service.LlmManufacturerModelService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.StringParam;
import com.wg.appframe.mybatisflex.core.Wrappers;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.wenge.model.entity.table.LlmManufacturerModelTableDef.LLM_MANUFACTURER_MODEL;

@Service
public class LlmManufacturerModelServiceImpl extends ServiceImpl<LlmManufacturerModelMapper, LlmManufacturerModel> implements LlmManufacturerModelService {

    @Override
    public Result<List<LlmManufacturerResult>> getLlmManufacturer() {
        QueryWrapper queryWrapper = Wrappers.init()
                .select(QueryMethods.distinct(LLM_MANUFACTURER_MODEL.MANUFACTURER),
                        LLM_MANUFACTURER_MODEL.ID,
                        LLM_MANUFACTURER_MODEL.MANUFACTURER_ICON,
                        LLM_MANUFACTURER_MODEL.WEBSITE)
                .orderBy(LLM_MANUFACTURER_MODEL.ID.asc());
        List<LlmManufacturerResult> result = mapper.selectListByQueryAs(queryWrapper, LlmManufacturerResult.class);

        Set<String> seen = new LinkedHashSet<>();
        List<LlmManufacturerResult> deduplicated = new ArrayList<>();

        for (LlmManufacturerResult item : result) {
            if (seen.add(item.getManufacturer())) {
                deduplicated.add(item);
            }
        }

        return Result.success(deduplicated);
    }

    @Override
    public Result<List<String>> getLlmManufacturerModelList(StringParam param) {
        List<LlmManufacturerModel> list = Wrappers.of(mapper)
                .select(LLM_MANUFACTURER_MODEL.MODEL)
                .and(LLM_MANUFACTURER_MODEL.MANUFACTURER.eq(param.getParam()))
                .list();
        List<String> modelList = list.stream().map(LlmManufacturerModel::getModel).distinct().collect(Collectors.toList());
        return Result.success(modelList);
    }

    @Override
    public Result<LlmManufacturerModel> getLlmManufacturerModel(LlmManufacturerModelParam param) {
        LlmManufacturerModel manufacturerModel = Wrappers.of(mapper)
                .and(LLM_MANUFACTURER_MODEL.MANUFACTURER.eq(param.getManufacturer()))
                .and(LLM_MANUFACTURER_MODEL.MODEL.eq(param.getModelName()))
                .one();
        return Result.success(manufacturerModel);
    }

    @Override
    public List<LlmManufacturerModel> getLlmManufacturerList() {
        List<LlmManufacturerModel> llmManufacturerModels =  mapper.selectAll();
        return llmManufacturerModels;
    }
}
