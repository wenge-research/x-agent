package com.wenge.model.workflow.component.variable;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.wenge.model.workflow.entity.MetaParam;
import com.wenge.model.workflow.enums.MetaParamEnum;

import java.util.stream.Collectors;

public class StringSegment extends Segments<String> {
    public StringSegment(String variable, String value) {
        super(variable, MetaParamEnum.STRING, value);
    }

    @Override
    public Object conversionType(MetaParam metaParam) {
        if (null == value) {
            return null;
        }
        String type = metaParam.getType();
        MetaParamEnum type1 = MetaParamEnum.getType(type);
        if (null != type1) {
            String segmentsValue = value;
            switch (type1) {
                case STRING:
                    return segmentsValue;
                case ARRAY_OBJ:
                    if (JSONUtil.isTypeJSONArray(segmentsValue)) {
                        return JSON.parseArray(segmentsValue);
                    }
                case OBJECT:
                    if (JSONUtil.isTypeJSONObject(segmentsValue)) {
                        return JSON.parseObject(segmentsValue);
                    }
                case ARRAY_STR:
                    if (!JSONUtil.isTypeJSONArray(segmentsValue)) {
                        JSONArray array = JSON.parseArray(segmentsValue);
                        return array.stream().map(JSON::toJSONString).collect(Collectors.toList());
                    }
                default:
                    return value;
            }
        }
        return null;
    }

    @Override
    public String text() {
        return super.text();
    }

    @Override
    public String markdown() {
        return super.markdown();
    }

    @Override
    public String json() {
        return super.json();
    }
}
