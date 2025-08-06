package com.wenge.model.dto.param;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
public class CanvasData implements Serializable {

    private static final long serialVersionUID = 7430966643396741949L;

    private Position position;
    private Size size;
    private String view = "vue-shape-view";
    private String shape = "dag-output";
    private Component component;
    private Long time;
    private Integer zIndex;
    private Ports ports;
    private JSONObject data;
    private String id;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Position implements Serializable {

        private static final long serialVersionUID = 490862690668833355L;

        private int x;
        private int y;
    }

    @Data
    public static class Size implements Serializable {

        private static final long serialVersionUID = -1955940132702059438L;

        private int width;
        private int height;
    }

    @Data
    public static class Component implements Serializable {

        private static final long serialVersionUID = 4555461391226637876L;

        private String template = "<onlyout />";
        private JSONObject components= JSONUtil.parseObj("{\"onlyout\":{\"name\":\"paramsFilter\",\"inject\":{\"getGraph\":{\"from\":\"getGraph\"},\"getNode\":{\"from\":\"getNode\"}},\"mixins\":[{\"components\":{\"RunningState\":{\"props\":{\"show\":{\"default\":false},\"isShowBtn\":{\"default\":true},\"isRunningText\":{\"default\":\"运行成功\"}},\"methods\":{},\"staticRenderFns\":[],\"_compiled\":true,\"_scopeId\":\"data-v-04260e77\",\"__file\":\"src/views/workflowConfig/dragDemo/components/nodeTheme/components/running-state.vue\"},\"HeadTool\":{\"name\":\"HeadTool\",\"props\":{\"label\":{\"default\":\"\"},\"showSub\":{\"default\":true},\"showDraw\":{\"default\":false},\"imgWidth\":{\"type\":[null,null],\"default\":18},\"imgHeight\":{\"type\":[null,null],\"default\":18},\"imgSuffix\":{\"default\":\"kaishi\"}},\"watch\":{\"label\":{\"deep\":true,\"immediate\":true,\"user\":true}},\"methods\":{},\"staticRenderFns\":[],\"_compiled\":true,\"_scopeId\":\"data-v-7076a1d9\",\"__file\":\"src/views/workflowConfig/dragDemo/components/nodeTheme/components/head-tool.vue\",\"_Ctor\":{}},\"RunningResult\":{\"props\":{\"inputList\":{\"type\":[null,null]},\"outputJsonData\":{\"default\":\"\"}},\"methods\":{},\"staticRenderFns\":[],\"_compiled\":true,\"_scopeId\":\"data-v-6eb6d627\",\"__file\":\"src/views/workflowConfig/dragDemo/components/nodeTheme/components/running-result.vue\"}},\"methods\":{}}],\"methods\":{},\"staticRenderFns\":[],\"_compiled\":true,\"_scopeId\":\"data-v-cebe7836\",\"__file\":\"src/views/workflowConfig/dragDemo/components/nodeTheme/onlyOut.vue\",\"_Ctor\":{}}}");
        private JSONObject _Ctor = new JSONObject();
    }

    @Data
    public static class Ports implements Serializable {

        private static final long serialVersionUID = 5279202672453116873L;

        private JSONObject groups = JSONUtil.parseObj("{\"left\":{\"position\":\"left\",\"attrs\":{\"circle\":{\"r\":4,\"magnet\":true,\"stroke\":\"#1c50fd\",\"strokeWidth\":1,\"fill\":\"#fff\"}}},\"right\":{\"position\":\"right\",\"attrs\":{\"circle\":{\"r\":4,\"magnet\":true,\"stroke\":\"#1c50fd\",\"strokeWidth\":1,\"fill\":\"#fff\"}}}}");
        private List<PortsItems> items;
    }

    @Data
    public static class PortsItems implements Serializable {

        private static final long serialVersionUID = -7398617150698260994L;

        private String id;
        private String group;
        private JSONObject attrs = JSONUtil.parseObj("{\"circle\":{\"r\":4}}");
    }
}
