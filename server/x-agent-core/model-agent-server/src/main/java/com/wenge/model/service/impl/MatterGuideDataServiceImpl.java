package com.wenge.model.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSON;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.AddMatterGuideDataItem;
import com.wenge.model.dto.param.AddMatterGuideDataParam;
import com.wenge.model.entity.*;
import com.wenge.model.enums.MatterInfoStatusEnum;
import com.wenge.model.mapper.MatterGuideDataMapper;
import com.wenge.model.mapper.MatterGuideFiledMapper;
import com.wenge.model.mapper.MatterGuideInfoMapper;
import com.wenge.model.mapper.es.MatterDataMapper;
import com.wenge.model.service.MatterGuideDataService;
import com.wenge.model.utils.DateUtil;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.holder.AppContextHolder;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.StringConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.annotation.rely.RefreshPolicy;
import org.dromara.easyes.common.utils.ExceptionUtils;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;

import static com.wenge.model.entity.table.MatterGuideFiledTableDef.MATTER_GUIDE_FILED;
import static com.wenge.model.entity.table.MatterGuideTableDef.MATTER_GUIDE;


/**
 * @author DELL
 */
@Service
@Slf4j
public class MatterGuideDataServiceImpl extends ServiceImpl<MatterGuideDataMapper, MatterGuideData> implements MatterGuideDataService {

    @Autowired
    MatterGuideInfoMapper guideInfoMapper;

    @Autowired
    MatterGuideDataMapper guideDataMapper;

    @Autowired
    MatterGuideFiledMapper guideFiledMapper;

    @Autowired
    private MatterDataMapper matterDataMapper;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public Result submitFormInfo(AddMatterGuideDataParam param) {
        log.info("MatterGuideDataServiceImpl.submitFormInfo param is ：{}", JSON.toJSONString(param));

        List<AddMatterGuideDataItem> guideDataItems = param.getItems();
        Assert.notEmpty(guideDataItems, "参数不能为空");

        if (StringUtils.isBlank(param.getMatterId())) {
            return Result.fail("事项不能为空");
        }

        List<MatterGuide> lists = MatterGuide.create()
                .where(MATTER_GUIDE.MATTER_ID.eq(param.getMatterId()))
                .limit(1)
                .lists();
        if (CollectionUtils.isEmpty(lists)) {
            return Result.fail("事项不存在");
        }
        MatterGuide matterGuide = lists.get(0);

        // 校验字段唯一性
        String res = checkFiledUnique(param.getMatterId(), guideDataItems, param.getApplicationId());
        if (StringUtils.isNotBlank(res)) {
            return Result.fail(res);
        }

        MatterData matterData = buildData(guideDataItems, matterGuide, param.getTraceId(), param.getApplicationId());
        GetIndexResponse index = matterDataMapper.getIndex();
        IndexRequest indexRequest = new IndexRequest();
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.setRefreshPolicy(RefreshPolicy.NONE.getValue());
        bulkRequest.add(indexRequest);

        indexRequest.index(index.getIndices()[0]);
        indexRequest.source(matterData, XContentType.JSON);
        indexRequest.id(matterData.getId());
        try {
            BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            if (bulkResponse.hasFailures()) {
                log.error("bulkRequest error:{}", bulkResponse.buildFailureMessage());
            }
        } catch (IOException var8) {
            IOException e = var8;
            throw ExceptionUtils.eee("bulkRequest exception", e);
        }

        // 提交数据
        return Result.success();
    }

    /**
     * 事项配置字段唯一性校验
     *
     * @param matterId       事项业务id
     * @param guideDataItems 表单提交数据
     * @return 错误信息，没有错误信息返回空字符串
     */
    private String checkFiledUnique(String matterId, List<AddMatterGuideDataItem> guideDataItems, String appId) {
        String res = "";
        List<MatterGuideFiled> filedList = MatterGuideFiled.create()
                .where(MATTER_GUIDE_FILED.MATTER_ID.eq(matterId))
                .lists();
        for (MatterGuideFiled matterGuideFiled : filedList) {
            if ("是".equals(matterGuideFiled.getUniqueFlag())) {
                String filedCode = matterGuideFiled.getFiledCode();
                String filedValue = "";
                for (AddMatterGuideDataItem guideDataItem : guideDataItems) {
                    if (guideDataItem.getFiledId().equals(filedCode)) {
                        filedValue = guideDataItem.getDataValue().toString();
                        break;
                    }
                }
                if (StringUtils.isNotBlank(filedValue)) {
                    LambdaEsQueryWrapper<MatterData> wrapper = EsWrappers.lambdaQuery(MatterData.class)
                            .eq(MatterData::getMatterId, matterId)
                            .eq(MatterData::getApplicationId, appId)
                            .eq(filedCode + ".keyword", filedValue);
                    Long count = matterDataMapper.selectCount(wrapper);
                    if (count > 0) {
                        res = "表单中[" + filedValue + "]已提交过，请修改后提交";
                        return res;
                    }
                }
            }
        }
        return res;
    }

    private Map<String,Object> writeData(AddMatterGuideDataParam param, List<AddMatterGuideDataItem> dataItems, String infoId) {
        Map<String,Object> res = new HashMap<>();
        if (CollectionUtil.isEmpty(dataItems)) return res;
        // 还没提交过
        List<MatterGuideData> saveDatas = new ArrayList<>();
        for (AddMatterGuideDataItem guideDataItem : dataItems) {
            Object dataValue = guideDataItem.getDataValue();
            if (dataValue instanceof ArrayList) {
                ArrayList<?> objects = (ArrayList<?>) dataValue;
                String jsonString = JSON.toJSONString(objects);
                MatterGuideData guideData = buildData(param, guideDataItem.getFiledId(), jsonString, infoId);
                saveDatas.add(guideData);
            } else {
                MatterGuideData guideData = buildData(param, guideDataItem.getFiledId(), dataValue, infoId);
                saveDatas.add(guideData);
            }
            if ("idCard".equals(guideDataItem.getFiledId())) {
                res.put("idCard", String.valueOf(dataValue));
            }
            if ("phone".equals(guideDataItem.getFiledId())) {
                res.put("phone", String.valueOf(dataValue));
            }
            if ("peopleName".equals(guideDataItem.getFiledId())) {
                res.put("peopleName", String.valueOf(dataValue));
            }
        }
        if (!CollectionUtils.isEmpty(saveDatas)) {
            guideDataMapper.insertBatch(saveDatas);
        }
        return res;
    }

    private MatterGuideInfo buildInfo(String matterId, String userId, String userName, String idCard, String phone) {
        Date time = new Date();
        return MatterGuideInfo.builder()
                .matterId(matterId)
                .infoId(IdUtil.randomUUID())
                .userId(userId)
                .userName(userName)
                .idCard(idCard)
                .phone(phone)
                .status(MatterInfoStatusEnum.APPROVE.getStatus())
                .createTime(time)
                .updateTime(time)
                .build();
    }

    private MatterGuideData buildData(AddMatterGuideDataParam param, String filedId,Object dataValue, String infoId) {
        return MatterGuideData.builder()
                .infoId(infoId)
                .matterId(param.getMatterId())
                .dataId(IdUtil.randomUUID())
                .filedId(filedId)
                .dataValue(String.valueOf(dataValue))
                .build();
    }

    /**
     * 构建matterData
     *
     * @param guideDataItems
     * @param matterGuide
     * @return
     */
    private MatterData buildData(List<AddMatterGuideDataItem> guideDataItems, MatterGuide matterGuide, String traceId, String appId) {
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        MatterData matterData = new MatterData();
        matterData.setApplicationId(appId);
        matterData.setCreateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DateUtil.PATTERN_1));
        matterData.setMatterId(matterGuide.getMatterId());
        matterData.setStatus("未审批");
        matterData.setId(IdUtil.simpleUUID());
        matterData.setTraceId(traceId);
        matterData.setCreateUser(tokenUserInfo.getUserName());

        if (CollectionUtil.isNotEmpty(guideDataItems)) {
            for (AddMatterGuideDataItem guideDataItem : guideDataItems) {
                if (guideDataItem.getDataValue() == null) {
                    matterData.put(guideDataItem.getFiledId(), StringConstant.BLANK);
                } else if (guideDataItem.getDataValue() instanceof String) {
                    matterData.put(guideDataItem.getFiledId(), guideDataItem.getDataValue().toString());
                } else {
                    matterData.put(guideDataItem.getFiledId(), JSON.toJSONString(guideDataItem.getDataValue()));
                }
            }
        }
        return matterData;
    }
}