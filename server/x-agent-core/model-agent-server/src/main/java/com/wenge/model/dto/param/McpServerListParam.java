package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class McpServerListParam extends WgPageInfo {

    private static final long serialVersionUID = 1458270068497132956L;

    private String mcpName;

    private String createBy;

    private String mcpServerUserStatus;

    /**
     * mcp归属类型
     */
    private String ownerType;

    /**
     * 主键，用于修改预置状态
     */
    private Long id;

    /**
     * mcp分类
     */
    private String type;

    /**
     * 是否开通mcp  1开通 0关闭
     */
    private String status;

    /**
     * mcpId，用于修改mcp状态
     */
    private String mcpId;

}
