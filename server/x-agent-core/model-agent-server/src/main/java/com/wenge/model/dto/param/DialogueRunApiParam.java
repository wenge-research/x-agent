package com.wenge.model.dto.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class DialogueRunApiParam implements Serializable {
    private static final long serialVersionUID = 1L;

    private RunComponentNodeParam param;
    private String apiKey;

}
