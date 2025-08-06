package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class LastestRecordPageParam extends WgPageInfo {

    private static final long serialVersionUID = 2633065896977765066L;


    /**
     * 每页显示的记录数
     */
    private Integer pageSize;

    /**
     * 起始页数
     */
    private Integer pageNo;


    /**
     * 是否查询最新的一条记录 0是上一轮 1是分页查询历史对话
     */
    private String lastestFlag;


    /**
     * 应用id
     */
    private String applicationId;
    private List<String> applicationIds;


}
