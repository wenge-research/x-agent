package com.wenge.model.workflow.component.variable;

import com.alibaba.fastjson.JSON;
import com.wenge.model.workflow.entity.MetaParam;
import com.wenge.model.workflow.enums.MetaParamEnum;

import java.util.List;
import java.util.stream.Collectors;

public class ArrayBooleanSegment extends ArraySegment<Boolean> {
    public ArrayBooleanSegment(String variable, List<Boolean> value) {
        super(variable, MetaParamEnum.ARRAY_BOOL, value);
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
        throw new UnsupportedOperationException("布尔数组不能转为json格式");
    }

    @Override
    public Object conversionType(MetaParam metaParam) {
        if (null == value) {
            return null;
        }
        String type = metaParam.getType();
        MetaParamEnum type1 = MetaParamEnum.getType(type);
        if (null != type1) {
            List<Boolean> boolList = value;
            switch (type1) {
                case STRING:
                    // arrInt转string
                    return String.valueOf(value);
                case OBJECT:
                    // arrInt转json
                    return JSON.toJSONString(boolList);
                case ARRAY_OBJ:
                case ARRAY_INT:
                    return boolList;
                case ARRAY_STR:
                    return boolList.stream().map(Object::toString).collect(Collectors.toList());
                default:
                    return value;
            }
        }
        return null;

    }
}
