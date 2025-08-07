package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RecordCheckParam extends WgPageInfo {

    private static final long serialVersionUID = 4578633806400682521L;

    /**
     * 开始时间
     */
    private String timeStart;

    /**
     * 结束时间
     */
    private String timeEnd;

    /**
     * 部门id
     */
    private String deptId;

    /**
     * 审核状态：0-待审核；1-审核通过；2-审核不通过
     */
    private String auditStatus;

    /**
     * 问题或答案
     */
    private String text;

    /**
     * 应用id
     */
    private String applicationId;


}
