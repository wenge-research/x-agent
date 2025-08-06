package com.wenge.model.workflow.component.variable;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.wenge.model.workflow.component.file.File;
import com.wenge.model.workflow.entity.MetaParam;
import com.wenge.model.workflow.enums.MetaParamEnum;
import com.wenge.model.workflow.util.JsonUtil;

import java.util.List;
import java.util.stream.Collectors;

public class ArrayFileSegment extends ArraySegment<File> {

    public ArrayFileSegment(String variable, List<File> value) {
        super(variable, MetaParamEnum.ARRAY_FILE, value);
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
        if (null == type1) {
            return null;
        }
        List<File> fileList = value;
        switch (type1) {
            case STRING:
                // arrInt转string
                return String.valueOf(value);
            case OBJECT:
                // arrInt转json
                return JSON.toJSONString(fileList);
            case ARRAY_OBJ:
            case ARRAY_INT:
            case ARRAY_FILE:
                return fileList;
            case ARRAY_STR:
                return fileList.stream().map(Object::toString).collect(Collectors.toList());
            default:
                return value;
        }
    }
}
