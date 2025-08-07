package com.wg.appframe.yayi.config;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class YayiPluginSelectionModelConfig extends YayiConfigSupper{

    private static final long serialVersionUID = -2636009891782155260L;
    private String uri;
    private List<String> avaliable_plugins;
    private Integer max_new_tokens;
    private Integer best_of;
    private Integer max_window_size;
    private Boolean use_recommendation;
    private Boolean use_only_custom_plugins;
}
