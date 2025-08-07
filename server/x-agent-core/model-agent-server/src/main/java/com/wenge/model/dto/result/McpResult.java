package com.wenge.model.dto.result;

import lombok.Data;

import java.io.Serializable;

@Data
public class McpResult implements Serializable {

    private static final long serialVersionUID = 5015719492632441599L;

    private String role;
    private String tool_call_id;
    private String name;
    private String content;
}
