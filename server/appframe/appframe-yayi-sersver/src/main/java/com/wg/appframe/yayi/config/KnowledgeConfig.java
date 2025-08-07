package com.wg.appframe.yayi.config;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class KnowledgeConfig extends YayiConfigSupper {

    private static final long serialVersionUID = -7199816189171454777L;

    private String uri;
    private Integer ctsChunkSize;
    private Integer ctsChunkOverlap;
    private Integer rctsChunkOverlap;
    private Integer rctsChunkSize;
    private List<String> mergeSplitApproach;
}
