package com.wenge.model.workflow.component.variable;

import com.alibaba.fastjson.JSON;
import com.wenge.model.workflow.entity.MetaParam;
import com.wenge.model.workflow.enums.MetaParamEnum;

import java.util.List;
import java.util.stream.Collectors;

public class ArrayIntegerSegment extends ArraySegment<Long> {
    public ArrayIntegerSegment(String variable, List<Long> value) {
        super(variable, MetaParamEnum.ARRAY_INT, value);
    }

    @Override
    public String markdown() {
        return super.markdown();
    }

    @Override
    public String text() {
        return super.text();
    }

    @Override
    public String json() {
        throw new UnsupportedOperationException("数值数组不能转为json格式");
    }

    @Override
    public Object conversionType(MetaParam metaParam) {
        if (null == value) {
            return null;
        }
        String type = metaParam.getType();
        MetaParamEnum type1 = MetaParamEnum.getType(type);
        if (null != type1) {
            List<Long> intList = value;
            switch (type1) {
                case STRING:
                    // arrInt转string
                    return String.valueOf(value);
                case OBJECT:
                    // arrInt转json
                    return JSON.toJSONString(intList);
                case ARRAY_OBJ:
                case ARRAY_INT:
                    return intList;
                case ARRAY_STR:
                    return intList.stream().map(Object::toString).collect(Collectors.toList());
                default:
                    return value;
            }
        }
        return null;
    }
}
