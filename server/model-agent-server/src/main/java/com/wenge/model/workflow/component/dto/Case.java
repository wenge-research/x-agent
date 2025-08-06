package com.wenge.model.workflow.component.dto;

import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 条件case
 */
@Data
public class Case implements Serializable, Cloneable {

    private static final long serialVersionUID = 6691165288719083773L;

    /**
     * true AND false OR
     */
    private Boolean tag;

    /**
     * 前端使用参数
     */
    private String logicName;

    /**
     * caseId 用于绑定边
     */
    private String caseId;

    private String sourceNodeId;

    private List<Condition> conditions;

    public Boolean getResult() {
        if (tag) {
            return conditions.stream().allMatch(Condition::getResult);
        } else {
            return conditions.stream().anyMatch(Condition::getResult);
        }
    }

    @Override
    public Case clone() {
        try {
            Case clone = (Case) super.clone();
            if (CollectionUtils.isNotEmpty(conditions)) {
                clone.conditions = conditions.stream().map(Condition::clone).collect(Collectors.toList());
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
