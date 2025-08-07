package com.wenge.model.workflow.component.variable;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.wenge.model.workflow.entity.MetaParam;
import com.wenge.model.workflow.enums.MetaParamEnum;
import com.wenge.model.workflow.util.JsonUtil;

import java.util.List;
import java.util.stream.Collectors;

public class ArrayObjectSegment extends ArraySegment<Object> {

    public ArrayObjectSegment(String variable, List<Object> value) {
        super(variable, MetaParamEnum.ARRAY_OBJ, value);
    }

    @Override
    public String text() {
        return JSONUtil.toJsonStr(value);
    }

    @Override
    public String markdown() {
        return JsonUtil.jsonToMarkdown(value, new StringBuilder(), 0);
    }

    @Override
    public String json() {
        return super.json();
    }

    @Override
    public Object conversionType(MetaParam metaParam) {
        if (null == value) {
            return null;
        }
        String type = metaParam.getType();
        MetaParamEnum type1 = MetaParamEnum.getType(type);
        if (null != type1) {
            List<Object> dataList = value;
            switch (type1) {
                case STRING:
                    return JSON.toJSONString(dataList);
                case ARRAY_OBJ:
                    return dataList;
                case OBJECT:
                    return JSON.parseObject(JSON.toJSONString(dataList));
                case ARRAY_STR:
                    return dataList.stream().map(JSON::toJSONString).collect(Collectors.toList());
                default:
                    return value;
            }
        }
        return null;
    }
}
