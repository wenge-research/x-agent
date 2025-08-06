package com.wenge.model.dto.param;

import lombok.Data;

import java.util.List;

@Data
public class ParserInfoDeleteParam{

    private static final long serialVersionUID = -4583495844404370140L;

    private List<String> ids;
}
