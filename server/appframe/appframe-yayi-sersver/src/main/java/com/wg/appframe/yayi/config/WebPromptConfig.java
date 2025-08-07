package com.wg.appframe.yayi.config;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class WebPromptConfig extends YayiConfigSupper {

    private static final long serialVersionUID = -2579091525285612905L;

    private String uri;
    private String function;
    private String userId;
    private Integer promptMaxTokens;
    private Integer getNewsNum;
    private Integer topK;
    private List<String> webSourceList;
    private Float timeLimit;
    private String dataType;
    private Boolean sortLabel;
    private Boolean genMindMap;
    private Integer genMaxTokens;
}
