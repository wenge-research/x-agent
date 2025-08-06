package com.wenge.model.workflow.component.variable;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.wenge.model.workflow.entity.MetaParam;
import com.wenge.model.workflow.enums.MetaParamEnum;
import com.wenge.model.workflow.util.JsonUtil;

import java.util.stream.Collectors;

public class ObjectSegment extends Segments<Object> {
    public ObjectSegment(String variable, Object value) {
        super(variable, MetaParamEnum.OBJECT, value);
    }

    @Override
    public String text() {
        return JSONUtil.toJsonStr(value);
    }

    @Override
    public String markdown() {
        // json对象转成markdown格式
        return JsonUtil.jsonToMarkdown(value, new StringBuilder(), 0);
    }

    @Override
    public Object conversionType(MetaParam metaParam) {
        String type = metaParam.getType();
        MetaParamEnum type1 = MetaParamEnum.getType(type);
        if (null != type1) {
            switch (type1) {
                case STRING:
                    value = JSON.toJSONString(value);
                    break;
                case ARRAY_OBJ:
                    value = ListUtil.toList(value);
                    break;
                case ARRAY_STR:
                    value = ListUtil.toList(value).stream().map(JSON::toJSONString).collect(Collectors.toList());
                    break;
                case OBJECT:
                default:
                    break;
            }
        }
        return value;
    }
}
