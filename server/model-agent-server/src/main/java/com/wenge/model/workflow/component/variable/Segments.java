package com.wenge.model.workflow.component.variable;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSON;
import com.wenge.model.enums.VariableStatusEnum;
import com.wenge.model.workflow.entity.MetaParam;
import com.wenge.model.workflow.enums.MetaParamEnum;
import lombok.Data;
import org.aspectj.weaver.loadtime.Agent;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;

/**
 * 字段类型
 */
@Data
public class Segments<T> {
    public static final String TEXT = "text";
    public static final String MARKDOWN = "markdown";
    public static final String JSON_TYPE = "json";

    /**
     * 字段类型
     */
    protected MetaParamEnum valueType;

    /**
     * 状态
     */
    protected VariableStatusEnum statusEnum;

    /**
     * 子类用指定类型重写
     */
    public T value;
    public String variable;

    public Segments(String variable, MetaParamEnum valueType, T value) {
        this.valueType = valueType;
        this.value = value;
        this.variable = variable;
    }

    /**
     * 父类方法，将变量转为文本
     * @return
     */
    public String text() {
        return String.valueOf(value);
    }

    /**
     * 父类方法，将变量转为markdown，主要为了转换数组格式
     * @return
     */
    public String markdown() {
        return String.valueOf(value);
    }

    /**
     * 父类方法，将变量转为json格式
     * @return
     */
    public String json() {
        return JSON.toJSONString(value);
    }

    /**
     * 父类方法，将变量转为markdown
     * @return
     */
    public String log() {
        return String.valueOf(value);
    }

    /**
     * 返回值的长度
     * @return
     */
    public Long size() {
        Instrumentation instrumentation = Agent.getInstrumentation();
        return instrumentation.getObjectSize(value);
    }


    /**
     * Segment转为指定metaParam类型
     *
     * @param metaParam
     * @return
     */
    public Object conversionType(MetaParam metaParam) {
        return null;
    };
}

abstract class ArraySegment<T> extends Segments<List<T>> {
    public ArraySegment(String variable, MetaParamEnum valueType, List<T> value) {
        super(variable, valueType, value);
    }

    @Override
    public String markdown() {
        List<String> items = new ArrayList<>();
        Assert.isInstanceOf(List.class, this.value);
        for (Object item : this.value) {
            items.add(String.valueOf(item));
        }
        return String.join("\n", items);
    }
}




