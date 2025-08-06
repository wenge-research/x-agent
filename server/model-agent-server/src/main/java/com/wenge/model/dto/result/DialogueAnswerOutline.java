package com.wenge.model.dto.result;

import com.wenge.model.enums.StepStatusEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DialogueAnswerOutline implements Serializable {

    private static final long serialVersionUID = -826248558154588966L;

    private String title;

    private List<String> content;

}
