package com.wenge.model.dto.result;

import cn.hutool.json.JSONObject;
import com.wg.appframe.core.dto.results.WGResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DecisionStatusResult extends WGResult {

    private static final long serialVersionUID = 1040092651128848012L;

    private String session_id;
    private Integer current_round;
    private Integer total_agents;
    private String status;
    private Boolean is_running;
    private String last_event;
    private String last_update;
    private String decisions_summary;
    private JSONObject environment_summary;

}
