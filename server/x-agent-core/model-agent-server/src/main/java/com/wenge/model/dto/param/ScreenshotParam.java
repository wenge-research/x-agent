package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ScreenshotParam extends WGParam {

    private static final long serialVersionUID = -7842128436646870529L;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 截图链接
     */
    private List<String> urlList;
}
