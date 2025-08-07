package com.wenge.model.workflow.component.dto;

import com.wenge.model.workflow.enums.OperatorsEnum;
import lombok.Data;

import java.io.Serializable;

@Data
public class Condition implements Serializable, Cloneable {

    private static final long serialVersionUID = 1124931633458214332L;

    private String left;

    private OperatorsEnum operator;

    private String right;

    /* 前端使用字段 */
    private String selectedGroup;

    private String type;

    private String value;

    /**
     * 计算表达式结果
     *
     * @return
     */
    public Boolean getResult() {
        return operator.test(left, right);
    }

    @Override
    public Condition clone() {
        try {
            Condition clone = (Condition) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
