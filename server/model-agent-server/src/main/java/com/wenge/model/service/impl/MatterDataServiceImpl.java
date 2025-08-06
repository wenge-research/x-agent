package com.wenge.model.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.wenge.model.dto.param.MatterDataParam;
import com.wenge.model.entity.MatterData;
import com.wenge.model.entity.MatterGuideFiled;
import com.wenge.model.mapper.es.MatterDataMapper;
import com.wenge.model.service.MatterDataService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.StringParam;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.core.biz.EsPageInfo;
import org.dromara.easyes.core.conditions.update.LambdaEsUpdateWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.wenge.model.entity.table.MatterGuideFiledTableDef.MATTER_GUIDE_FILED;

@Service
public class MatterDataServiceImpl implements MatterDataService {

    @Autowired
    private MatterDataMapper matterDataMapper;

    @Override
    public Result getMatterDataList(MatterDataParam param) {
        if (StringUtils.isBlank(param.getApplicationId()) || StringUtils.isBlank(param.getMatterId())) {
            return Result.success();
        }

        LambdaEsUpdateWrapper<MatterData> wrapper = EsWrappers.lambdaUpdate(MatterData.class)
                .eq(MatterData::getMatterId, param.getMatterId())
                .eq(MatterData::getApplicationId, param.getApplicationId());

        if (null != param.getCondition()) {
            List<MatterGuideFiled> guideFields = MatterGuideFiled.create()
                    .where(MATTER_GUIDE_FILED.MATTER_ID.eq(param.getMatterId()))
                    .lists();
            JSONObject condition = param.getCondition();
            guideFields.forEach(filed -> {
                if (StringUtils.isNotBlank(filed.getSearchType()) && StringUtils.isNotBlank(condition.getString(filed.getFiledCode()))) {
                    switch (filed.getSearchType()) {
                        case "1":
                            wrapper.eq(filed.getFiledCode(), condition.get(filed.getFiledCode()));
                            break;
                        case "2":
                            wrapper.matchPhrasePrefixQuery(filed.getFiledCode(), condition.get(filed.getFiledCode()));
                            break;
                        case "3":
                            String conditionValue = condition.getString(filed.getFiledCode());
                            String[] split = conditionValue.split(",");
                            wrapper.in(filed.getFiledCode(), split);
                            break;
                        default:
                            break;
                    }
                }
            });
        }

        EsPageInfo<MatterData> pageInfo = matterDataMapper.pageQuery(wrapper, param.getPageNo(), param.getPageSize());
        return Result.success(pageInfo);
    }

    @Override
    public Result getMatterDataDetail(StringParam param) {
        if (StringUtils.isBlank(param.getParam())) {
            return Result.fail();
        }
        MatterData matterData = matterDataMapper.selectById(param.getParam());
        return Result.success(matterData);
    }
}
