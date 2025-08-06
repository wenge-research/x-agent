package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class GenEchartsParam extends WGParam {

    private static final long serialVersionUID = -2736025103437068168L;

    /**
     * 应用 id
     */
    @NotBlank(message = "应用 id 不能为空")
    private String applicationId;

    /**
     * 用户指令
     */
    @NotBlank(message = "用户指令不能为空")
    private String content;
}
