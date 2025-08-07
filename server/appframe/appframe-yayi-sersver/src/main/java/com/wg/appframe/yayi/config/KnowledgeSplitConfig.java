package com.wg.appframe.yayi.config;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class KnowledgeSplitConfig extends YayiConfigSupper {

    private static final long serialVersionUID = -7199816189171454777L;

    private String uri;

    private Integer ctsChunkSize;
    private Integer ctsChunkOverlap;
    private Integer rctsChunkOverlap;
    private Integer rctsChunkSize;
    private Boolean return_chunk;
    private List<String> merge_split_approach;

}
