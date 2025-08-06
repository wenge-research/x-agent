package com.wenge.model.workflow.component.variable;

import com.alibaba.fastjson.JSON;
import com.wenge.model.workflow.entity.MetaParam;
import com.wenge.model.workflow.enums.MetaParamEnum;

import java.util.Collections;

public class BooleanSegment extends Segments<Boolean> {
    public BooleanSegment(String variable, Boolean value) {
        super(variable, MetaParamEnum.BOOLEAN, value);
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
        throw new UnsupportedOperationException("布尔值不能转为json格式");
    }


    @Override
    public Object conversionType(MetaParam metaParam) {
        if (null == value) {
            return null;
        }

        String type = metaParam.getType();
        MetaParamEnum type1 = MetaParamEnum.getType(type);
        if (null != type1) {
            Boolean bool = value;
            switch (type1) {
                case STRING:
                    return String.valueOf(bool);
                case ARRAY_OBJ:
                    return bool;
                case OBJECT:
                    return JSON.parseObject(JSON.toJSONString(bool));
                case ARRAY_STR:
                    return Collections.singletonList(String.valueOf(bool));
                case BOOLEAN:
                    return value;
                default:
                    return value;
            }
        }
        return null;
    }
}
