package com.wg.appframe.yayi.param;

import com.wg.appframe.yayi.entity.YayiMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class YayiPluginSelectionModelParam extends YayiParam implements Serializable {

    private static final long serialVersionUID = -8920588013254873426L;

    private List<YayiMessage> messages;
    private List<String> avaliable_plugins;
    private Integer max_new_tokens;
    private Integer best_of;
    private Integer max_window_size;
    private Boolean use_recommendation;
    private Boolean use_only_custom_plugins;
    private List<CustomPlugin> custom_plugin;
    
    @Data
    public static class CustomPlugin implements Serializable {

        private static final long serialVersionUID = 7652243440814391570L;

        private String nameForHuman;
        private String nameForModel;
        private String descriptionForModel;
        private String descriptionForModelEn;
        private List<Parameter> parameters;

    }

    @Data
    public static class Parameter {
        private String name;
        private String description;
        private String descriptionEn;
        private boolean required;
        private Schema schema;
    }

    @Data
    public static class Schema {
        private String type;
    }
}
