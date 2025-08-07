package com.wenge.model.dto.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class KnowledgeContent implements Serializable {

    private static final long serialVersionUID = 6197328847804414722L;

    private List<String> contentList;
    private String module;
}
