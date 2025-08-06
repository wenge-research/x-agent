package com.wenge.model.workflow.component.variable;

import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.wenge.model.workflow.entity.MetaParam;
import com.wenge.model.workflow.enums.MetaParamEnum;

public class IntegerSegment extends Segments<Long> {
    public IntegerSegment(String variable, Long value) {
        super(variable, MetaParamEnum.INTEGER, value);
    }

    @Override
    public Object conversionType(MetaParam metaParam) {
        if (null == value) {
            return null;
        }
        String type = metaParam.getType();
        MetaParamEnum type1 = MetaParamEnum.getType(type);
        if (null != type1) {
            Long data = value;
            switch (type1) {
                case STRING:
                    return String.valueOf(data);
                case ARRAY_OBJ:
                    return Lists.newArrayList(data);
                case OBJECT:
                    return data;
                case ARRAY_STR:
                    return Lists.newArrayList(String.valueOf(data));
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
        throw new UnsupportedOperationException("数值类型不能转为json格式");
    }
}
