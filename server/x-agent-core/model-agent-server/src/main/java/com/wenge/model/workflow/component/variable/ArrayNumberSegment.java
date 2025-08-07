package com.wenge.model.workflow.component.variable;

import cn.hutool.json.JSONUtil;
import com.wenge.model.workflow.entity.MetaParam;
import com.wenge.model.workflow.enums.MetaParamEnum;
import com.wg.appframe.core.constant.StringConstant;

import java.util.List;

public class ArrayNumberSegment extends ArraySegment<Number> {
    public ArrayNumberSegment(String variable, List<Number> value) {
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
        if (null != value) {
            return JSONUtil.toJsonStr(value);
        }
        return StringConstant.BLANK;
    }

    @Override
    public Object conversionType(MetaParam metaParam) {
        return null;
    }
}
