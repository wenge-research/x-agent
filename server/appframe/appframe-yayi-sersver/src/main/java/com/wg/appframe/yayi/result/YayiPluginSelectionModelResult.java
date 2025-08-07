package com.wg.appframe.yayi.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class YayiPluginSelectionModelResult extends YayiResult implements Serializable {

    private static final long serialVersionUID = 4181351086430043015L;

    private PluginData data;

    @Data
    public static class PluginData implements Serializable {

        private static final long serialVersionUID = 6715753608148676225L;

        private Message message;
    }

    @Data
    public static class Message implements Serializable {
        private static final long serialVersionUID = 5613784940167754455L;

        private String selection; // 选择的插件类型
        private String pluginChoosed; // 已选择的插件
        private String pluginRecommended; // 推荐的插件（可能为 null）
        private String pluginArgs; // 插件参数
        private String role; // 角色
        private String type; // 请求类型
    }
}
