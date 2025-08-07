package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class DecisionRunParam extends WGParam {

    private static final long serialVersionUID = 4745655471581043044L;

    /**
     * 用户事件
     */
    private String userEvent;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 广播目标
     */
    private List<String> broadcastTargets;
}
