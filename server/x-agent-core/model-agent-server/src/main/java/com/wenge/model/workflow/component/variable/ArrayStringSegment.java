package com.wenge.model.workflow.component.variable;

import com.alibaba.fastjson.JSON;
import com.wenge.model.workflow.entity.MetaParam;
import com.wenge.model.workflow.enums.MetaParamEnum;

import java.util.ArrayList;
import java.util.List;

public class ArrayStringSegment extends ArraySegment<String> {

    public ArrayStringSegment(String variable, List<String> value) {
        super(variable, MetaParamEnum.ARRAY_STR, value);
    }

    @Override
    public String text() {
        // 当value为[]的时候，应当返回空值
        if (value.isEmpty()) return "";
        return super.text();
    }


    @Override
    public String markdown() {
        return super.markdown();
    }

    @Override
    public String json() {
        throw new UnsupportedOperationException("字符串数组不能转为json格式");
    }

    @Override
    public Object conversionType(MetaParam metaParam) {
        if (null == value) {
            return null;
        }

        String type = metaParam.getType();
        MetaParamEnum type1 = MetaParamEnum.getType(type);
        if (null == type1) {
            return null;
        }
        List<String> strList = value;
        strList = new ArrayList<>(strList);
        switch (type1) {
            case STRING:
                return JSON.toJSONString(strList);
            case ARRAY_OBJ:
                return strList;
            case OBJECT:
                return JSON.parseObject(JSON.toJSONString(strList));
            case ARRAY_STR:
                return value;
            default:
                return value;
        }
    }
}
