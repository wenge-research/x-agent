package com.wenge.model.workflow.component.variable;

import com.wenge.model.workflow.entity.MetaParam;
import com.wenge.model.workflow.enums.MetaParamEnum;

public class FloatSegment extends Segments<Double> {
    public FloatSegment(String variable, Double value) {
        super(variable, MetaParamEnum.INTEGER, value);
    }

    @Override
    public Object conversionType(MetaParam metaParam) {
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
        throw new UnsupportedOperationException("浮点数不能转为json格式");
    }
}
