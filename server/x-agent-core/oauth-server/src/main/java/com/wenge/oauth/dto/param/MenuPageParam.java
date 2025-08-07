package com.wenge.oauth.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MenuPageParam extends WgPageInfo {

    private static final long serialVersionUID = 6574415736172522009L;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 菜单状态
     */
    private String status;

    /**
     * 菜单类型
     */
    private String menuType;
    private String startDate;
    private String endDate;
}
