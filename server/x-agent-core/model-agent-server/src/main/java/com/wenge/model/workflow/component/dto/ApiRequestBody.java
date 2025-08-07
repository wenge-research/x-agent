package com.wenge.model.workflow.component.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ApiRequestBody implements Serializable {

    private static final long serialVersionUID = -7762609496210791985L;

    private String name;
    private String fullName;
    private Object value;
    private String type;
    private List<ApiRequestBody> children;


}
